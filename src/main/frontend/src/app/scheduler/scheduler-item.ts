export class SchedulerItem {

  readonly key: string | undefined;
  title: string;
  deadline: number;
  isCompleted: boolean;
  detail: string;

  constructor(another?: SchedulerItem) {
    if (another == null) {
      this.key = null;
      this.title = "";
      const nowDate = new Date();
      nowDate.setDate(nowDate.getDate() + 1);
      nowDate.setHours(0, 0, 0, 0);
      this.deadline = nowDate.getTime();
      this.isCompleted = false;
      this.detail = "";
    } else {
      this.key = another.key;
      this.title = another.title;
      this.deadline = another.deadline;
      this.isCompleted = another.isCompleted;
      this.detail = another.detail;
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
    return this.deadlineDate.toLocaleDateString();
  }

  get daysLeft(): string {
    return (this.totalHoursLeft / 24).toFixed(0);
  }

  get hoursLeft(): string {
    return (this.totalHoursLeft % 24).toFixed(0);
  }

}
