import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerProject } from '../scheduler-data';
import { SchedulerNetworkService } from '../scheduler-network.service';
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

  async editItem(project?: SchedulerProject) {
    const toBeEdited = project == null ? new SchedulerProject() : new SchedulerProject(project);
    const value: any = await this.dialog
      .open(EditorDialogComponent, { data: toBeEdited })
      .afterClosed()
      .toPromise();
    if (value == null) {
      return;
    }
    const edited = value as SchedulerProject;
    const ref = this.loadingService.open();
    const key = await this.networkService.editItem(edited);
    ref.close();
    const projectsWithOldRemoved = project == null
      ? this.projects : this.projects.filter(i => i.key !== project.key);
    projectsWithOldRemoved.push(new SchedulerProject(<SchedulerProject>{ ...edited, key: key }));
    this.projects = projectsWithOldRemoved.sort((a, b) => a.deadline - b.deadline);
  }

  async deleteItem(project: SchedulerProject) {
    if (project.key == null) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.deleteItem(project.key);
    ref.close();
    this.projects = this.projects.filter(i => i.key !== project.key);
  }

  async markAs(completed: boolean, project: SchedulerProject) {
    if (project.key == null) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.markAs(completed, project.key);
    ref.close();
    project.isCompleted = completed;
  }

}
