import { SchedulerRecord } from './scheduler-record';

/**
 * All supported event type.
 */
export enum SchedulerEventType { ONE_TIME = 'ONE_TIME', WEEKLY = 'WEEKLY' }

export namespace SchedulerEventRepeats {
  /**
   * [SUNDAY] means repeating on Sunday.
   */
  export const SUNDAY = 1;
  /**
   * [MONDAY] means repeating on Monday.
   */
  export const MONDAY = 2;
  /**
   * [TUESDAY] means repeating on Tuesday.
   */
  export const TUESDAY = 4;
  /**
   * [WEDNESDAY] means repeating on Wednesday.
   */
  export const WEDNESDAY = 8;
  /**
   * [THURSDAY] means repeating on Thursday.
   */
  export const THURSDAY = 16;
  /**
   * [FRIDAY] means repeating on Friday.
   */
  export const FRIDAY = 32;
  /**
   * [SATURDAY] means repeating on Saturday.
   */
  export const SATURDAY = 64;

  /**
   * [WEEKDAYS] means repeating on weekdays.
   */
  const WEEKDAYS: number = MONDAY | TUESDAY | WEDNESDAY | THURSDAY | FRIDAY;
  /**
   * [WEEKENDS] means repeating on weekends.
   */
  const WEEKENDS: number = SATURDAY | SUNDAY;
  /**
   * [EVERYDAY] means repeating everyday.
   */
  export const EVERYDAY: number = WEEKDAYS | WEEKENDS;

  /**
   * Returns whether day is in config.
   *
   * @param {number} day day to check.
   * @param {number} config config to check.
   * @returns {boolean} whether day is in config.
   */
  export function inConfig(day: number, config: number): boolean {
    return day !== 0 && (day | config) === config;
  }

  /**
   * Returns the config of the day depending on whether it's selected.
   *
   * @param {number} day the day.
   * @param {boolean} selected whether the day is selected.
   * @returns {number} the config of the day depending on whether it's selected.
   */
  export function getDayConfig(day: number, selected: boolean): number {
    return selected ? day : 0;
  }

  /**
   * A short-hand for repeat selected config.
   */
  export type RepeatSelected = [boolean, boolean, boolean, boolean, boolean, boolean, boolean];

  /**
   * Returns the repeat selected form of the given config.
   *
   * @param {number} config config to convert.
   * @returns {RepeatSelected} the repeat selected form of the given config.
   */
  export function toRepeatSelected(config: number): RepeatSelected {
    return [
      inConfig(SUNDAY, config), inConfig(MONDAY, config), inConfig(TUESDAY, config),
      inConfig(WEDNESDAY, config), inConfig(THURSDAY, config), inConfig(FRIDAY, config),
      inConfig(SATURDAY, config),
    ];
  }

}

/**
 * An event in scheduler.
 */
export class SchedulerEvent implements SchedulerRecord {

  /**
   * The commonly used date hour offset.
   * @type {number}
   */
  private static dateHourOffSet: number = Math.round(new Date().getTimezoneOffset() / 60);

  /**
   * Key of the event.
   */
  readonly key: string | undefined;
  /**
   * Type of the event.
   */
  readonly type: SchedulerEventType;
  /**
   * Title of the event.
   */
  readonly title: string;
  /**
   * Start hour of the event.
   */
  readonly startHour: number;
  /**
   * End hour of the event.
   */
  readonly endHour: number;
  /**
   * Repeat config of the event.
   */
  readonly repeatConfig: number;

  /**
   * Construct by nothing or another event.
   *
   * @param {SchedulerEvent} another another event, which can be omitted.
   */
  constructor(another?: SchedulerEvent) {
    if (another == null) {
      this.type = SchedulerEventType.ONE_TIME;
      this.title = '';
      const [s, e] = SchedulerEvent.convertHours(0, 24, true);
      this.startHour = s;
      this.endHour = e;
      const nowDate = new Date();
      nowDate.setHours(0, 0, 0, 0);
      this.repeatConfig = nowDate.getTime();
    } else {
      this.key = another.key;
      this.type = another.type;
      this.title = another.title;
      this.startHour = another.startHour;
      this.endHour = another.endHour;
      this.repeatConfig = another.repeatConfig;
    }
  }

  /**
   * Returns the converted hours.
   *
   * @param {number} start start time.
   * @param {number} end end time.
   * @param {boolean} toUTC whether convert to UTC.
   * @returns {[number]} the converted hours.
   */
  static convertHours(start: number, end: number, toUTC: boolean): [number, number] {
    if (toUTC) {
      const utcStart = (start + SchedulerEvent.dateHourOffSet + 48) % 24;
      const utcEnd = utcStart + end - start;
      return [utcStart, utcEnd];
    } else {
      const thisTimezoneStart = (start - SchedulerEvent.dateHourOffSet + 48) % 24;
      const thisTimezoneEnd = thisTimezoneStart + end - start;
      return [thisTimezoneStart, thisTimezoneEnd];
    }
  }

  /**
   * Returns the UTC date at 0AM from the given date.
   *
   * @param {Date} date the date to convert.
   * @returns {number} a tuple of the UTC date at 0AM from the given date.
   */
  static dateToUTCDateAtZeroAm(date: Date): number {
    const utcHour = date.getUTCHours();
    const utcDateMs = Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
      utcHour, 0, 0, 0);
    const utcDate = new Date(utcDateMs);
    utcDate.setUTCHours(0, 0, 0, 0);
    return utcDate.getTime();
  }

  /**
   * Returns the correct local date at zero AM for the given utc date at zero am and hour offset.
   *
   * @param {number} utcDateAtZeroAm utc date at zero am
   * @param {number} hour hour offset.
   * @returns {Date} the correct local date at zero AM
   */
  static utcDateTimeAtZeroAMAndUTCHourToLocalDate(utcDateAtZeroAm: number, hour: number): Date {
    const date = new Date(utcDateAtZeroAm);
    date.setUTCHours(hour);
    const localDate = new Date(date.getTime());
    localDate.setHours(0);
    return localDate;
  }

  /**
   * Returns a collection of classified events.
   *
   * @param {SchedulerEvent[]} rawEvents unclassified events.
   * @returns {SchedulerEvents} a collection of classified events.
   */
  static classify(rawEvents: SchedulerEvent[]): SchedulerEvents {
    const oneTimeEvents = [], weeklyEvents = [];
    for (const rawEvent of rawEvents) {
      switch (rawEvent.type) {
        case SchedulerEventType.ONE_TIME:
          oneTimeEvents.push(rawEvent);
          break;
        case SchedulerEventType.WEEKLY:
          weeklyEvents.push(rawEvent);
          break;
      }
    }
    return <SchedulerEvents>{ oneTimeEvents, weeklyEvents };
  }

}

/**
 * A scheduler event with its index.
 */
export interface SchedulerEventWithIndex {
  /**
   * Event.
   */
  event: SchedulerEvent;
  /**
   * Index.
   */
  index: number;
}

/**
 * Classified Events for Scheduler.
 */
export interface SchedulerEvents {
  /**
   * All one time events.
   */
  readonly oneTimeEvents: SchedulerEvent[];
  /**
   * All weekly events.
   */
  readonly weeklyEvents: SchedulerEvent[];
}
