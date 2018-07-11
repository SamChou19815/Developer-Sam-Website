import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material';
import { possibleHoursArray } from '../../../shared/util';
import {
  SchedulerEvent,
  SchedulerEventRepeats as Repeats,
  SchedulerEventType
} from '../../scheduler-event';

@Component({
  selector: 'app-scheduler-event-editor-dialog',
  templateUrl: './editor-dialog.component.html',
  styleUrls: ['./editor-dialog.component.css']
})
export class EditorDialogComponent implements OnInit {

  /**
   * All possible hours.
   * @type {number[]}
   */
  readonly possibleHours: number[] = possibleHoursArray;
  types = SchedulerEventType;

  readonly key: string | undefined;
  type: SchedulerEventType;
  title: string;
  startHourInThisTimeZone: number;
  endHourInThisTimeZone: number;

  // One-time specific
  readonly dateFormControl: FormControl;

  // Weekly specific
  repeatSelected: Repeats.RepeatSelected;

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
      this.dateFormControl = new FormControl(
        SchedulerEvent.utcDateTimeAtZeroAMAndUTCHourToLocalDate(
          event.repeatConfig, event.startHour
        )
      );
      this.repeatSelected = [true, true, true, true, true, true, true];
    } else {
      this.dateFormControl = new FormControl(new Date());
      this.repeatSelected = Repeats.toRepeatSelected(event.repeatConfig);
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

  /**
   * Returns whether the edited event is a one-time event.
   *
   * @returns {boolean} whether the edited event is a one-time event.
   */
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
      ? SchedulerEvent.dateToUTCDateAtZeroAm(<Date>this.dateFormControl.value)
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

  /**
   * Returns whether the submit button should be disabled.
   *
   * @returns {boolean} whether the submit button should be disabled.
   */
  get submitDisabled(): boolean {
    return this.title.trim().length === 0 ||
      this.startHourInThisTimeZone >= this.endHourInThisTimeZone ||
      !this.isRepeatConfigValid;
  }

  /**
   * Returns the auto-generated event from valid user input.
   *
   * @returns {SchedulerEvent} the auto-generated project from valid user input.
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
