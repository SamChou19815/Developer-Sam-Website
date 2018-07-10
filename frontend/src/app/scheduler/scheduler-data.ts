import { SchedulerEvent } from './scheduler-event';
import { SchedulerProject } from './scheduler-project';

/**
 * [SchedulerData] is the collection of scheduler app related data.
 */
export interface SchedulerData {
  /**
   * A list of all projects.
   */
  readonly projects: SchedulerProject[];
  /**
   * A list of all events.
   */
  readonly events: SchedulerEvent[];
}
