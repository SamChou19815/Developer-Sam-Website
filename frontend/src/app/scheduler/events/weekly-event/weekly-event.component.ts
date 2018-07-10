import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SchedulerEvent, SchedulerEventRepeats as Repeats } from '../../scheduler-event';

@Component({
  selector: 'app-scheduler-weekly-event',
  templateUrl: './weekly-event.component.html',
  styleUrls: ['./weekly-event.component.css']
})
export class WeeklyEventComponent implements OnInit {

  @Input() schedulerEvent: SchedulerEvent = new SchedulerEvent();
  @Output() editClicked = new EventEmitter<undefined>();
  @Output() deleteClicked = new EventEmitter<undefined>();

  constructor() {
  }

  ngOnInit() {
  }

  /**
   * Returns the string describing repeats.
   *
   * @returns {string} the string describing repeats.
   */
  get repeatString(): string {
    const mapper: (selected: boolean, index: number) => string | null = (selected, index) => {
      if (!selected) {
        return null;
      }
      switch (index) {
        case 0:
          return 'SUN';
        case 1:
          return 'MON';
        case 2:
          return 'TUE';
        case 3:
          return 'WED';
        case 4:
          return 'THU';
        case 5:
          return 'FRI';
        case 6:
          return 'SAT';
        default:
          throw new Error();
      }
    };
    const filter: (opt: string | null) => boolean = opt => opt != null;
    return Repeats.toRepeatSelectedArray(this.schedulerEvent.repeatConfig)
      .map(mapper).filter(filter).join(' ');
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
