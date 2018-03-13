/**
 * Defines the simplified shape of scheduler item.
 */
export interface SchedulerItemData {
  keyString: string | null;
  description: string;
  deadline: string;
  deadlineHour?: number | null;
  detail?: string | null;
}

/**
 * Defines the shape of the scheduler item, which has some additional properties than SchedulerItemData.
 */
export interface SchedulerItem extends SchedulerItemData {
  daysLeft: number;
  hoursLeft: number;
  isCompleted: boolean;
}
