import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SchedulerProject } from '../../scheduler-data';
import { SchedulerNetworkService } from '../../scheduler-network.service';

@Component({
  selector: 'app-scheduler-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.scss']
})
export class ItemComponent implements OnInit {

  @Input() schedulerProject: SchedulerProject = new SchedulerProject();
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
    const daysLeft = this.schedulerProject.daysLeft;
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
    return this.schedulerProject.isGroupProject
      ? `[Group Project] ${this.schedulerProject.title}` : this.schedulerProject.title;
  }

}
