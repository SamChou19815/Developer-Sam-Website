import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SchedulerItem } from '../scheduler-item';
import { SchedulerNetworkService } from '../scheduler-network.service';

@Component({
  selector: 'app-scheduler-item',
  templateUrl: './scheduler-item.component.html',
  styleUrls: ['./scheduler-item.component.scss']
})
export class SchedulerItemComponent implements OnInit {

  @Input() schedulerItem: SchedulerItem;
  @Output() editClicked = new EventEmitter<void>();
  @Output() deleteClicked = new EventEmitter<void>();
  @Output() markAsClicked = new EventEmitter<boolean>();

  /**
   * Initialize itself with injected network service.
   *
   * @param {SchedulerNetworkService} schedulerNetworkService the injected network service.
   */
  constructor(private schedulerNetworkService: SchedulerNetworkService) { }

  ngOnInit() {}

  /**
   * A helper property to output the string for the deadline.
   *
   * @returns {string} the formatted string for the deadline.
   */
  get deadline(): string {
    const actualDeadlineHour: number = this.schedulerItem.deadlineHour ? this.schedulerItem.deadlineHour : 24;
    return `${this.schedulerItem.deadline} ${actualDeadlineHour - 1}:59`;
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

  /**
   * Edit itself.
   */
  editMyself(): void {
    this.editClicked.emit(null);
  }

  /**
   * Delete itself from the server.
   */
  deleteMyself(): void {
    this.deleteClicked.emit(null);
  }

  /**
   * Try to mark itself as completed or uncompleted.
   *
   * @param {boolean} completed true => completed, false => uncompleted.
   */
  markAs(completed: boolean): void {
    this.markAsClicked.emit(completed);
  }

}
