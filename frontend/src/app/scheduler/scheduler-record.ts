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

/**
 * [Interval] represents a simple interval.
 */
export interface Interval {
  /**
   * Start time.
   */
  start: number;
  /**
   * End time.
   */
  end: number;
}

/**
 * [AnnotatedSchedulerRecord] represents an interval annotated scheduler record.
 */
export interface AnnotatedSchedulerRecord {
  readonly record: SchedulerRecord;
  readonly intervals: Interval[];
}
