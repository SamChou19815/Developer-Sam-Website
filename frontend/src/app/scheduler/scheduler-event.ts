/**
 * All supported event type.
 */
export enum SchedulerEventType { ONE_TIME = 'ONE_TIME', WEEKLY = 'WEEKLY' }

export namespace SchedulerEventRepeats {
  /**
   * [SUNDAY] means repeating on Sunday.
   */
  export const SUNDAY: number = 1 << 0;
  /**
   * [MONDAY] means repeating on Monday.
   */
  export const MONDAY: number = 1 << 1;
  /**
   * [TUESDAY] means repeating on Tuesday.
   */
  export const TUESDAY: number = 1 << 2;
  /**
   * [WEDNESDAY] means repeating on Wednesday.
   */
  export const WEDNESDAY: number = 1 << 3;
  /**
   * [THURSDAY] means repeating on Thursday.
   */
  export const THURSDAY: number = 1 << 4;
  /**
   * [FRIDAY] means repeating on Friday.
   */
  export const FRIDAY: number = 1 << 5;
  /**
   * [SATURDAY] means repeating on Saturday.
   */
  export const SATURDAY: number = 1 << 6;

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
    return (day | config) === config;
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

}

/**
 * An event in scheduler.
 */
export class SchedulerEvent {

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
      this.startHour = 0;
      this.endHour = 23;
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

}

export namespace SchedulerEvent {

  /**
   * Returns a tuple of (utcDateZeroAmMs, utcHour) from the given date.
   *
   * @param {Date} date the date to convert.
   * @returns {[number]} a tuple of (utcDateZeroAmMs, utcHour).
   */
  export function dateToUTCDateHour(date: Date): [number, number] {
    const utcHour = date.getUTCHours();
    const utcDateMs = Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
      utcHour, 0, 0, 0);
    const utcDate = new Date(utcDateMs);
    utcDate.setUTCHours(0, 0, 0, 0);
    const utcDateZeroAmMs = utcDate.getUTCMilliseconds();
    return [utcDateZeroAmMs, utcHour];
  }

  /**
   * Returns a date object from utcDateZeroAmMs and utcHour.
   *
   * @param {number} utcDateZeroAmMs the time in ms at the 0AM of the date in UTC.
   * @param {number} utcHour The corresponding hour in 0..23.
   * @returns {Date} a date object from utcDateZeroAmMs and utcHour.
   */
  export function utcDateHourToDate(utcDateZeroAmMs: number, utcHour: number): Date {
    const time = utcDateZeroAmMs + 3600 * 1000 * utcHour;
    return new Date(time);
  }

  /**
   * Returns a collection of classified events.
   *
   * @param {SchedulerEvent[]} rawEvents unclassified events.
   * @returns {SchedulerEvents} a collection of classified events.
   */
  export function classify(rawEvents: SchedulerEvent[]): SchedulerEvents {
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
