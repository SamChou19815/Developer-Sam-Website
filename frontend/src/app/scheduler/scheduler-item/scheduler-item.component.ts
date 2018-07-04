import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SchedulerItem } from '../scheduler-item';
import { SchedulerNetworkService } from '../scheduler-network.service';

@Component({
  selector: 'app-scheduler-item',
  templateUrl: './scheduler-item.component.html',
  styleUrls: ['./scheduler-item.component.scss']
})
export class SchedulerItemComponent implements OnInit {

  @Input() schedulerItem: SchedulerItem = new SchedulerItem();
  @Output() editClicked = new EventEmitter<undefined>();
  @Output() deleteClicked = new EventEmitter<undefined>();
  @Output() markAsClicked = new EventEmitter<boolean>();

  /**
   * Initialize itself with injected network service.
   *
   * @param {SchedulerNetworkService} schedulerNetworkService the injected network service.
   */
  constructor(private schedulerNetworkService: SchedulerNetworkService) {
  }

  ngOnInit() {
  }

  /**
   * Compute style class of the component.
   *
   * @returns {string} a string of all classes that should be attached to title.
   */
  get titleStyle(): string {
    const daysLeft = this.schedulerItem.daysLeft;
    if (daysLeft <= 1) {
      return 'level-0-urgent';
    } else if (daysLeft <= 3) {
      return 'level-1-urgent';
    } else if (daysLeft <= 6) {
      return 'level-2-urgent';
    } else {
      return 'level-3-urgent';
    }
  }

  get title(): string {
    return this.schedulerItem.isGroupProject
      ? `[Group Project] ${this.schedulerItem.title}` : this.schedulerItem.title;
  }

}
