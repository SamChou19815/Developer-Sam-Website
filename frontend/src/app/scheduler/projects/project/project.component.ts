import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SchedulerProject } from '../../scheduler-project';
import { SchedulerNetworkService } from '../../scheduler-network.service';

@Component({
  selector: 'app-scheduler-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.scss']
})
export class ProjectComponent implements OnInit {

  /**
   * The project to display.
   * @type {SchedulerProject}
   */
  @Input() schedulerProject: SchedulerProject = new SchedulerProject();
  /**
   * Used to report when edit button is clicked.
   * @type {EventEmitter<undefined>}
   */
  @Output() editClicked = new EventEmitter<undefined>();
  /**
   * Used to report when delete button is clicked.
   * @type {EventEmitter<undefined>}
   */
  @Output() deleteClicked = new EventEmitter<undefined>();
  /**
   * Used to report when mark as button is clicked.
   * @type {EventEmitter<boolean>}
   */
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

}
