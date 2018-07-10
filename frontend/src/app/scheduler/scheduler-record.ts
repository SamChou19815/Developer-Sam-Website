/**
 * [SchedulerRecord] is designed to be the common super type of [SchedulerProject] and
 * [SchedulerEvent].
 */
export interface SchedulerRecord {
  /**
   * [key] of the record.
   */
  readonly key: string | undefined;
  /**
   * [title] of the record.
   */
  readonly title: string;
}
