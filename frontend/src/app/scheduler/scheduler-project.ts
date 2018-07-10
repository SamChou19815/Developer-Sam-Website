import { SchedulerRecord } from './scheduler-record';

/**
 * A project in Scheduler.
 */
export class SchedulerProject implements SchedulerRecord {

  /**
   * Key of the project.
   */
  readonly key: string | undefined;
  /**
   * Title of the project.
   */
  readonly title: string;
  /**
   * Deadline of the project.
   */
  readonly deadline: number;
  /**
   * Whether the project is completed.
   */
  readonly isCompleted: boolean;
  /**
   * Detail of the project.
   */
  readonly detail: string;
  /**
   * Minimum time units required to work on it consecutively.
   */
  readonly minimumTimeUnits: number;
  /**
   * Estimated number of time units to complete it.
   */
  readonly estimatedTimeUnits: number;
  /**
   * Whether the project is a group project.
   */
  readonly isGroupProject: boolean;
  /**
   * Weight of the project, used for auto-scheduling.
   */
  readonly weight: number;

  /**
   * Construct by nothing or another project.
   *
   * @param {SchedulerEvent} another another project, which can be omitted.
   */
  constructor(another?: SchedulerProject) {
    if (another == null) {
      this.title = '';
      const nowDate = new Date();
      nowDate.setDate(nowDate.getDate() + 1);
      nowDate.setHours(0, 0, 0, 0);
      this.deadline = nowDate.getTime();
      this.isCompleted = false;
      this.detail = '';
      this.minimumTimeUnits = 1;
      this.estimatedTimeUnits = 1;
      this.isGroupProject = false;
      this.weight = 1;
    } else {
      this.key = another.key;
      this.title = another.title;
      this.deadline = another.deadline;
      this.isCompleted = another.isCompleted;
      this.detail = another.detail;
      this.minimumTimeUnits = another.minimumTimeUnits;
      this.estimatedTimeUnits = another.estimatedTimeUnits;
      this.isGroupProject = another.isGroupProject;
      this.weight = another.weight;
    }
  }

  /**
   * Returns the deadline as a Date object.
   *
   * @returns {Date} the deadline as a Date object.
   */
  get deadlineDate(): Date {
    return new Date(this.deadline);
  }

  /**
   * Returns the total hours left.
   *
   * @returns {number} the total hours left.
   */
  private get totalHoursLeft(): number {
    const millisLeft = this.deadline - new Date().getTime();
    return millisLeft / 1000 / 3600;
  }

  /**
   * Returns the string representation of the deadline.
   *
   * @returns {string} the string representation of the deadline.
   */
  get deadlineString(): string {
    return this.deadlineDate.toLocaleString();
  }

  /**
   * Returns the number of days left.
   *
   * @returns {number} number of days left.
   */
  get daysLeft(): number {
    return Math.floor(this.totalHoursLeft / 24);
  }

  /**
   * Returns the number of hours left, ignoring days info.
   *
   * @returns {number} the number of hours left, ignoring days info.
   */
  get hoursLeft(): number {
    return Math.floor(this.totalHoursLeft % 24);
  }

}
