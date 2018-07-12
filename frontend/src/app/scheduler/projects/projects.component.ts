import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerDataService } from '../scheduler-data.service';
import { SchedulerProject, SchedulerProjectWithIndex } from '../scheduler-project';
import { EditorDialogComponent } from './editor-dialog/editor-dialog.component';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {

  constructor(private dataService: SchedulerDataService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(() => {
      const ref = this.loadingService.open();
      this.dataService.initializedSchedulerApp().then(ref.close);
    });
  }

  /**
   * Returns the projects to display.
   *
   * @returns {SchedulerProject[]} the projects to display.
   */
  get projects(): SchedulerProject[] {
    return this.dataService.schedulerData.projects;
  }

  /**
   * Edit a project with index attached.
   *
   * @param projectWithIndex {SchedulerProjectWithIndex} the project with index, can be omitted.
   */
  editProject(projectWithIndex?: SchedulerProjectWithIndex): void {
    const toBeEdited = projectWithIndex == null
      ? new SchedulerProject() : new SchedulerProject(projectWithIndex.project);
    const index = projectWithIndex ? projectWithIndex.index : undefined;
    this.dialog.open(EditorDialogComponent, { data: toBeEdited })
      .afterClosed().subscribe(value => {
      if (value == null) {
        return;
      }
      const ref = this.loadingService.open();
      this.dataService.editProject(value as SchedulerProject, index).then(ref.close);
    });
  }

  /**
   * Delete a project
   * @param {SchedulerProjectWithIndex} deletedProjectWithIndex the deleted project with index.
   */
  deleteProject(deletedProjectWithIndex: SchedulerProjectWithIndex): void {
    const ref = this.loadingService.open();
    this.dataService.deleteProject(deletedProjectWithIndex).then(ref.close);
  }

  /**
   * Mark project as completed or not.
   *
   * @param {SchedulerProjectWithIndex} projectToMarkWithIndex the project to mark with index
   * @param {boolean} isCompleted whether the project should be marked as completed.
   */
  markProjectAs(projectToMarkWithIndex: SchedulerProjectWithIndex, isCompleted: boolean): void {
    const ref = this.loadingService.open();
    this.dataService.markProjectAs(projectToMarkWithIndex, isCompleted).then(ref.close);
  }

}
