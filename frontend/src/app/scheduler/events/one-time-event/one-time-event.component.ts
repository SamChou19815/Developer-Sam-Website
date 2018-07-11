import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SchedulerEvent } from '../../scheduler-event';

@Component({
  selector: 'app-scheduler-one-time-event',
  templateUrl: './one-time-event.component.html',
  styleUrls: ['./one-time-event.component.css']
})
export class OneTimeEventComponent implements OnInit {

  /**
   * The one-time event to display.
   * @type {SchedulerEvent}
   */
  @Input() schedulerEvent: SchedulerEvent = new SchedulerEvent();
  /**
   * Used to report when edit button is clicked.
   * @type {EventEmitter<undefined>}
   */
  @Output() editClicked = new EventEmitter<undefined>();
  /**
   * Used to report when delete button is clicked.
   * @type {EventEmitter<undefined>}
   */
  @Output() deleteClicked = new EventEmitter<undefined>();

  constructor() {
  }

  ngOnInit() {
  }

  /**
   * Returns the deadline date string.
   *
   * @returns {string} the deadline date string.
   */
  get dateString(): string {
    return SchedulerEvent.utcDateTimeAtZeroAMAndUTCHourToLocalDate(
      this.schedulerEvent.repeatConfig, this.schedulerEvent.startHour).toLocaleDateString();
  }

  /**
   * Returns the start and end hour in this time zone.
   *
   * @returns {[number, number]} the start and end hour in this time zone.
   */
  get startEndHour(): [number, number] {
    return SchedulerEvent.convertHours(
      this.schedulerEvent.startHour, this.schedulerEvent.endHour, false);
  }

}
