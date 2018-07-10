/**
 * [SchedulerTaggedInterval] represents a simple interval with type and title.
 */
export interface SchedulerTaggedInterval {
  /**
   * Type of the interval.
   */
  type: 'PROJECT' | 'EVENT';
  /**
   * Title of the interval.
   */
  title: string;
  /**
   * Start time.
   */
  start: number;
  /**
   * End time.
   */
  end: number;
}
