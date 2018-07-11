import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material';
import { possibleHoursArray } from '../../../shared/util';
import { SchedulerProject } from '../../scheduler-project';

@Component({
  selector: 'app-scheduler-project-editor-dialog',
  templateUrl: './editor-dialog.component.html',
  styleUrls: ['./editor-dialog.component.css']
})
export class EditorDialogComponent implements OnInit {

  /**
   * All possible hours.
   * @type {number[]}
   */
  readonly possibleHours: number[] = possibleHoursArray;

  /**
   * Key of the project.
   */
  readonly key: string | undefined;
  /**
   * Title of the project.
   */
  title: string;
  /**
   * Date control to select the deadline date.
   */
  readonly dateFormControl: FormControl;
  /**
   * Deadline hour.
   */
  hour: number;
  /**
   * Whether the project is completed.
   */
  private readonly isCompleted: boolean;
  /**
   * Detail of the project.
   */
  detail: string;
  /**
   * Minimum time units required to work on it consecutively.
   */
  minimumTimeUnits: number;
  /**
   * Estimated number of time units to complete it.
   */
  estimatedTimeUnits: number;
  /**
   * Whether it's a group project.
   */
  isGroupProject: boolean;
  /**
   * Weight of the project.
   */
  weight: number;

  constructor(@Inject(MAT_DIALOG_DATA) data: any) {
    const item = data as SchedulerProject;
    this.key = item.key;
    this.title = item.title;
    const d = item.deadlineDate;
    this.dateFormControl = new FormControl(new Date(d.getTime()));
    this.hour = d.getHours();
    this.isCompleted = item.isCompleted;
    this.detail = item.detail;
    this.minimumTimeUnits = item.minimumTimeUnits;
    this.estimatedTimeUnits = item.estimatedTimeUnits;
    this.isGroupProject = item.isGroupProject;
    this.weight = item.weight;
  }

  ngOnInit() {
  }

  /**
   * Returns the deadline time of the given input.
   *
   * @returns {number} the deadline time of the given input.
   */
  private get deadlineTime(): number {
    const d: Date = this.dateFormControl.value;
    d.setHours(this.hour, 0, 0, 0);
    return d.getTime();
  }

  /**
   * Returns whether the submit button should be disabled.
   *
   * @returns {boolean} whether the submit button should be disabled.
   */
  get submitDisabled(): boolean {
    try {
      return this.title.trim().length === 0 || new Date().getTime() - this.deadlineTime > 0;
    } catch (e) {
      return true;
    }
  }

  /**
   * Returns the auto-generated project from valid user input.
   *
   * @returns {SchedulerProject} the auto-generated project from valid user input.
   */
  get generatedProject(): SchedulerProject {
    return <SchedulerProject>{
      key: this.key,
      title: this.title,
      deadline: this.deadlineTime,
      isCompleted: this.isCompleted,
      detail: this.detail,
      minimumTimeUnits: this.minimumTimeUnits,
      estimatedTimeUnits: this.estimatedTimeUnits,
      isGroupProject: this.isGroupProject,
      weight: this.weight
    };
  }

}
