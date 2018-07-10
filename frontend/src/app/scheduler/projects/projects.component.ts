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

  projects: SchedulerProject[] = [];

  constructor(private googleUserService: GoogleUserService,
              private networkService: SchedulerNetworkService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  async ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      const data = await this.networkService.loadData();
      this.projects = data.projects.map(i => new SchedulerProject(i));
      ref.close();
    });
  }

  async editProject(projectWithIndex?: { project: SchedulerProject, index: number }) {
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

  async deleteProject(project: SchedulerProject, index: number) {
    if (project.key == null) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.deleteRecord(project.key, 'project');
    ref.close();
    this.projects.splice(index, 1);
  }

  async markProjectAs(completed: boolean, project: SchedulerProject, index: number) {
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
  }

}
