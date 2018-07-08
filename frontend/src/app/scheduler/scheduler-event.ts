export enum SchedulerEventType { ONE_TIME = 'ONE_TIME', WEEKLY = 'WEEKLY' }

export class SchedulerEvent {

  readonly key: string | undefined;
  type: SchedulerEventType;
  title: string;
  startHour: number;
  endHour: number;
  repeatConfig: number;

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

}
