export class SchedulerItem {

  readonly key: string | undefined;
  title: string;
  deadline: number;
  isCompleted: boolean;
  detail: string;
  minimumTimeUnits: number;
  estimatedTimeUnits: number;
  isGroupProject: boolean;
  weight: number;

  constructor(another?: SchedulerItem) {
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
