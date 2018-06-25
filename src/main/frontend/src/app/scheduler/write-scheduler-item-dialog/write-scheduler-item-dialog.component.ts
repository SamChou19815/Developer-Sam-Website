import { Component, Inject, OnInit } from '@angular/core';
import { FormControl } from "@angular/forms";
import { MAT_DIALOG_DATA } from '@angular/material';
import { SchedulerItem } from '../scheduler-item';

@Component({
  selector: 'app-write-scheduler-item-dialog',
  templateUrl: './write-scheduler-item-dialog.component.html',
  styleUrls: ['./write-scheduler-item-dialog.component.css']
})
export class WriteSchedulerItemDialogComponent implements OnInit {

  readonly key: string | undefined;
  title: string;
  date: FormControl;
  hour: number;
  private readonly isCompleted: boolean;
  detail: string;

  constructor(@Inject(MAT_DIALOG_DATA) data: any) {
    const item = data as SchedulerItem;
    this.key = item.key;
    this.title = item.title;
    const d = item.deadlineDate;
    this.date = new FormControl(new Date(d.getTime()));
    this.hour = d.getHours();
    this.isCompleted = item.isCompleted;
    this.detail = item.detail;
  }

  ngOnInit() {
  }

  private get deadline(): number {
    const d: Date = this.date.value;
    d.setHours(this.hour, 0, 0, 0);
    return d.getTime();
  }

  // noinspection JSMethodCanBeStatic
  get possibleHours(): number[] {
    const array = Array<number>(24);
    for (let i = 0; i < 24; i++) {
      array[i] = i;
    }
    return array;
  }

  get submitDisabled(): boolean {
    try {
      return this.title.trim().length === 0 || new Date().getTime() - this.deadline > 0;
    } catch (e) {
      return true;
    }
  }

  get generatedItem(): SchedulerItem {
    return <SchedulerItem>{
      key: this.key,
      title: this.detail,
      deadline: this.deadline,
      isCompleted: this.isCompleted,
      detail: this.detail
    };
  }

}
