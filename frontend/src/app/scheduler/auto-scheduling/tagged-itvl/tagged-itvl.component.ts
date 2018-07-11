import { Component, Input, OnInit } from '@angular/core';
import { SchedulerTaggedInterval } from '../../scheduler-tagged-interval';

@Component({
  selector: 'app-scheduler-tagged-interval',
  templateUrl: './tagged-itvl.component.html',
  styleUrls: ['./tagged-itvl.component.css']
})
export class TaggedItvlComponent implements OnInit {

  /**
   * The tagged interval to display.
   * @type {SchedulerTaggedInterval}
   */
  @Input() taggedInterval: SchedulerTaggedInterval = <SchedulerTaggedInterval>{
    type: 'PROJECT', title: '', start: 0, end: 0
  };

  /**
   * Returns time as string.
   *
   * @param {number} t the time to convert.
   * @returns {string} the time string.
   */
  readonly timeToString: (t: number) => string = (t: number) => new Date(t).toLocaleString();

  constructor() {
  }

  ngOnInit() {
  }

  /**
   * Returns the icon of the interval.
   *
   * @returns {string} the icon of the interval.
   */
  get icon(): string {
    return this.taggedInterval.type === 'PROJECT' ? 'event_available' : 'event';
  }

  /**
   * Returns the title of the interval.
   *
   * @returns {string} the title of the interval.
   */
  get title(): string {
    return this.taggedInterval.title;
  }

  /**
   * Returns the start of the interval.
   *
   * @returns {string} the start of the interval.
   */
  get start(): string {
    return this.timeToString(this.taggedInterval.start);
  }

  /**
   * Returns the end of the interval.
   *
   * @returns {string} the end of the interval.
   */
  get end(): string {
    return this.timeToString(this.taggedInterval.end);
  }

}
