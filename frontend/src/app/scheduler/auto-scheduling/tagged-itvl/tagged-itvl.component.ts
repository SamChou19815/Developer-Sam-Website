import { Component, Input, OnInit } from '@angular/core';
import { SchedulerTaggedInterval } from '../../scheduler-tagged-interval';

@Component({
  selector: 'app-scheduler-tagged-interval',
  templateUrl: './tagged-itvl.component.html',
  styleUrls: ['./tagged-itvl.component.css']
})
export class TaggedItvlComponent implements OnInit {

  @Input() taggedInterval: SchedulerTaggedInterval = {
    type: 'PROJECT', title: '', start: 0, end: 0
  };

  constructor() {
  }

  ngOnInit() {
  }

  // noinspection JSMethodCanBeStatic
  private timeToString(time: number): string {
    return new Date(time).toLocaleString();
  }

  get icon(): string {
    return this.taggedInterval.type === 'PROJECT' ? 'event_available' : 'event';
  }

  get title(): string {
    return this.taggedInterval.title;
  }

  get start(): string {
    return this.timeToString(this.taggedInterval.start);
  }

  get end(): string {
    return this.timeToString(this.taggedInterval.end);
  }

}
