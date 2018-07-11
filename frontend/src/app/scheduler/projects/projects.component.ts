import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerNetworkService } from '../scheduler-network.service';
import { SchedulerProject } from '../scheduler-project';
import { EditorDialogComponent } from './editor-dialog/editor-dialog.component';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.css']
})
export class ProjectsComponent implements OnInit {

  /**
   * Projects to display.
   * @type {SchedulerProject[]}
   */
  projects: SchedulerProject[] = [];

  constructor(private googleUserService: GoogleUserService,
              private networkService: SchedulerNetworkService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      const data = await this.networkService.loadData();
      this.projects = data.projects.map(i => new SchedulerProject(i));
      ref.close();
    });
  }

  /**
   * Asynchronously edit a project with index attached.
   *
   * @param projectWithIndex the project with index, can be omitted.
   */
  private async asyncEditProject(
    projectWithIndex?: { project: SchedulerProject, index: number }
  ): Promise<void> {
    const toBeEdited = projectWithIndex == null
      ? new SchedulerProject() : new SchedulerProject(projectWithIndex.project);
    const value: any = await this.dialog
      .open(EditorDialogComponent, { data: toBeEdited })
      .afterClosed()
      .toPromise();
    if (value == null) {
      return;
    }
    const edited = value as SchedulerProject;
    const ref = this.loadingService.open();
    const key = await this.networkService.editProject(edited);
    // remove old
    if (projectWithIndex != null) {
      const { index } = projectWithIndex;
      this.projects.splice(index, 1);
    }
    this.projects.push(new SchedulerProject(<SchedulerProject>{ ...edited, key: key }));
    this.projects.sort((a, b) => a.deadline - b.deadline);
    ref.close();
  }

  /**
   * Edit a project with index attached.
   *
   * @param projectWithIndex the project with index, can be omitted.
   */
  editProject(projectWithIndex?: { project: SchedulerProject, index: number }): void {
    this.asyncEditProject(projectWithIndex).then(() => {
    });
  }

  /**
   * Delete a project.
   *
   * @param {SchedulerProject} project project to delete.
   * @param {number} index index of the project.
   */
  deleteProject(project: SchedulerProject, index: number): void {
    (async () => {
      if (project.key == null) {
        return;
      }
      const ref = this.loadingService.open();
      await this.networkService.deleteRecord(project.key, 'project');
      ref.close();
      this.projects.splice(index, 1);
    })();
  }

  /**
   * Mark project as completed or not.
   *
   * @param {boolean} completed whether the project should be marked as completed.
   * @param {SchedulerProject} project the project to mark.
   * @param {number} index index of the project.
   */
  markProjectAs(completed: boolean, project: SchedulerProject, index: number): void {
    (async () => {
      if (project.key == null) {
        return;
      }
      const ref = this.loadingService.open();
      await this.networkService.markProjectAs(completed, project.key);
      ref.close();
      const newProject = new SchedulerProject(<SchedulerProject>{
        ...project, isCompleted: completed
      });
      this.projects.splice(index, 1, newProject);
    })();
  }

}
