import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material';
import {
  SchedulerEvent,
  SchedulerEventRepeats as Repeats,
  SchedulerEventType
} from '../../scheduler-event';

const possibleHoursArray = Array<number>(24);
for (let i = 0; i < 24; i++) {
  possibleHoursArray[i] = i;
}

@Component({
  selector: 'app-scheduler-event-editor-dialog',
  templateUrl: './editor-dialog.component.html',
  styleUrls: ['./editor-dialog.component.css']
})
export class EditorDialogComponent implements OnInit {

  types = SchedulerEventType;

  readonly key: string | undefined;
  type: SchedulerEventType;
  title: string;
  startHourInThisTimeZone: number;
  endHourInThisTimeZone: number;

  // One-time specific
  date: FormControl;

  // Weekly specific
  repeatSelected: [boolean, boolean, boolean, boolean, boolean, boolean, boolean];

  constructor(@Inject(MAT_DIALOG_DATA) data: any) {
    const event = data as SchedulerEvent;
    this.key = event.key;
    this.type = event.type;
    this.title = event.title;
    const [thisTimezoneStart, thisTimezoneEnd] =
      SchedulerEvent.convertHours(event.startHour, event.endHour, false);
    this.startHourInThisTimeZone = thisTimezoneStart;
    this.endHourInThisTimeZone = thisTimezoneEnd;
    if (this.isOneTimeEvent) {
      this.date = new FormControl(new Date(event.repeatConfig));
      this.repeatSelected = [true, true, true, true, true, true, true];
    } else {
      this.date = new FormControl(new Date());
      this.repeatSelected = [
        Repeats.inConfig(Repeats.SUNDAY, event.repeatConfig),
        Repeats.inConfig(Repeats.MONDAY, event.repeatConfig),
        Repeats.inConfig(Repeats.TUESDAY, event.repeatConfig),
        Repeats.inConfig(Repeats.WEDNESDAY, event.repeatConfig),
        Repeats.inConfig(Repeats.THURSDAY, event.repeatConfig),
        Repeats.inConfig(Repeats.FRIDAY, event.repeatConfig),
        Repeats.inConfig(Repeats.SATURDAY, event.repeatConfig)
      ];
    }
  }

  ngOnInit() {
  }

  /**
   * Returns the title of the editor.
   *
   * @returns {string} the title of the editor.
   */
  get editorTitle(): string {
    return this.key == null
      ? 'Add Scheduler Event'
      : `Edit ${this.isOneTimeEvent ? 'One-time' : 'Weekly'} Scheduler Event`;
  }

  // noinspection JSMethodCanBeStatic
  get possibleHours(): number[] {
    return possibleHoursArray;
  }

  get isOneTimeEvent(): boolean {
    return this.type === SchedulerEventType.ONE_TIME;
  }

  /**
   * Returns the generated repeat config.
   *
   * @returns {number} the generated repeat config.
   */
  private get repeatConfig(): number {
    return this.isOneTimeEvent
      ? SchedulerEvent.dateToUTCDateAtZeroAm(<Date>this.date.value)
      : Repeats.getDayConfig(Repeats.SUNDAY, this.repeatSelected[0]) |
      Repeats.getDayConfig(Repeats.MONDAY, this.repeatSelected[1]) |
      Repeats.getDayConfig(Repeats.TUESDAY, this.repeatSelected[2]) |
      Repeats.getDayConfig(Repeats.WEDNESDAY, this.repeatSelected[3]) |
      Repeats.getDayConfig(Repeats.THURSDAY, this.repeatSelected[4]) |
      Repeats.getDayConfig(Repeats.FRIDAY, this.repeatSelected[5]) |
      Repeats.getDayConfig(Repeats.SATURDAY, this.repeatSelected[6]);
  }

  /**
   * Returns whether the repeat config is valid.
   *
   * @returns {boolean} whether the repeat config is valid.
   */
  private get isRepeatConfigValid(): boolean {
    return this.isOneTimeEvent || Repeats.inConfig(this.repeatConfig, Repeats.EVERYDAY);
  }

  get submitDisabled(): boolean {
    return this.title.trim().length === 0 ||
      this.startHourInThisTimeZone >= this.endHourInThisTimeZone ||
      !this.isRepeatConfigValid;
  }

  /**
   * Returns the generated event from the content of the dialog.
   *
   * @returns {SchedulerEvent} the generated event from the content of the dialog.
   */
  get generatedEvent(): SchedulerEvent {
    const [utcStart, utcEnd] = SchedulerEvent.convertHours(
      this.startHourInThisTimeZone, this.endHourInThisTimeZone, true);
    return <SchedulerEvent>{
      key: this.key, type: this.type, title: this.title,
      startHour: utcStart, endHour: utcEnd, repeatConfig: this.repeatConfig
    };
  }

}
