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

  readonly possibleHours: number[] = possibleHoursArray;

  readonly key: string | undefined;
  title: string;
  date: FormControl;
  hour: number;
  private readonly isCompleted: boolean;
  detail: string;
  minimumTimeUnits: number;
  estimatedTimeUnits: number;
  isGroupProject: boolean;
  weight: number;

  constructor(@Inject(MAT_DIALOG_DATA) data: any) {
    const item = data as SchedulerProject;
    this.key = item.key;
    this.title = item.title;
    const d = item.deadlineDate;
    this.date = new FormControl(new Date(d.getTime()));
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

  private get deadline(): number {
    const d: Date = this.date.value;
    d.setHours(this.hour, 0, 0, 0);
    return d.getTime();
  }

  get submitDisabled(): boolean {
    try {
      return this.title.trim().length === 0 || new Date().getTime() - this.deadline > 0;
    } catch (e) {
      return true;
    }
  }

  get generatedProject(): SchedulerProject {
    return <SchedulerProject>{
      key: this.key,
      title: this.title,
      deadline: this.deadline,
      isCompleted: this.isCompleted,
      detail: this.detail,
      minimumTimeUnits: this.minimumTimeUnits,
      estimatedTimeUnits: this.estimatedTimeUnits,
      isGroupProject: this.isGroupProject,
      weight: this.weight
    };
  }

}