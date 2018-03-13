import { Component, Inject, OnInit, ViewChild } from '@angular/core';
import { NgModel } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material';
import { SchedulerItemData } from '../scheduler-item';

@Component({
  selector: 'app-write-scheduler-item-dialog',
  templateUrl: './write-scheduler-item-dialog.component.html',
  styleUrls: ['./write-scheduler-item-dialog.component.css']
})
export class WriteSchedulerItemDialogComponent implements OnInit {

  /**
   * Description field input.
   */
  @ViewChild('descriptionInput') descriptionInput: NgModel;
  /**
   * Deadline field input.
   */
  @ViewChild('deadlineInput') deadlineInput: NgModel;
  /**
   * Data of scheduler item.
   */
  schedulerItemData: SchedulerItemData;

  /**
   * Inject data to the dialog.
   *
   * @param data injected data.
   */
  constructor(@Inject(MAT_DIALOG_DATA) data: any) {
    this.schedulerItemData = data;
  }

  ngOnInit() {}

  /**
   * Report whether the submit button should be disabled.
   *
   * @returns {boolean} whether the submit button should be disabled.
   */
  get submitDisabled(): boolean {
    const basicCheck = !this.descriptionInput.valid || !this.deadlineInput.valid;
    if (basicCheck) {
      return true;
    }
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const dayCheck = yesterday.getTime() - new Date(this.schedulerItemData.deadline).getTime() > 0;
    if (dayCheck) {
      return true;
    }
    return this.schedulerItemData.deadlineHour !== null && this.schedulerItemData.deadlineHour !== undefined
      && !Number.isInteger(this.schedulerItemData.deadlineHour)
      && (this.schedulerItemData.deadlineHour <= 0 || this.schedulerItemData.deadlineHour > 24);
  }

}
