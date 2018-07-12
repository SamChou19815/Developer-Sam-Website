import { SchedulerEvent, SchedulerEvents } from './scheduler-event';
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

/**
 * [SchedulerProcessData] is the collection of processed scheduler app related data.
 */
export interface SchedulerProcessData {
  /**
   * A list of all projects.
   */
  readonly projects: SchedulerProject[];
  /**
   * The processed events.
   */
  readonly events: SchedulerEvents;
  /**
   * A marker to tell whether the data set has been initialized.
   */
  isNotInitialized?: boolean;
}

/**
 * The dummy placeholder scheduler data.
 * @type {SchedulerData}
 */
export const dummySchedulerProcessedData: SchedulerProcessData = <SchedulerProcessData>{
  projects: [], events: { oneTimeEvents: [], weeklyEvents: [] }, isNotInitialized: true
};
