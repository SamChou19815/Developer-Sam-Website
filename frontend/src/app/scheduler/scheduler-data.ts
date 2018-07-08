export class SchedulerProject {

  readonly key: string | undefined;
  readonly title: string;
  readonly deadline: number;
  isCompleted: boolean;
  readonly detail: string;
  readonly minimumTimeUnits: number;
  readonly estimatedTimeUnits: number;
  readonly isGroupProject: boolean;
  readonly weight: number;

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

  get deadlineDate(): Date {
    return new Date(this.deadline);
  }

  private get totalHoursLeft(): number {
    const millisLeft = this.deadline - new Date().getTime();
    return millisLeft / 1000 / 3600;
  }

  get deadlineString(): string {
    return this.deadlineDate.toLocaleString();
  }

  get daysLeft(): number {
    return Math.floor(this.totalHoursLeft / 24);
  }

  get hoursLeft(): number {
    return Math.floor(this.totalHoursLeft % 24);
  }

}

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

/**
 * [SchedulerData] is the collection of scheduler app related data.
 */
export interface SchedulerData {
  /**
   * A list of all projects.
   */
  projects: SchedulerProject[];
  /**
   * A list of all events.
   */
  events: SchedulerEvent[];
}
