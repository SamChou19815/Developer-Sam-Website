import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerEvent, SchedulerEvents } from '../scheduler-event';
import { SchedulerNetworkService } from '../scheduler-network.service';
import { EditorDialogComponent } from './editor-dialog/editor-dialog.component';

@Component({
  selector: 'app-scheduler-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  /**
   * The events to display.
   *
   * @type {SchedulerEvents}
   */
  events: SchedulerEvents = <SchedulerEvents>{ oneTimeEvents: [], weeklyEvents: [] };

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
      this.events = SchedulerEvent.classify(data.events.map(i => new SchedulerEvent(i)));
      ref.close();
    });
  }

  async editEvent(event?: SchedulerEvent) {
    const toBeEdited = event == null ? new SchedulerEvent() : new SchedulerEvent(event);
    const value: any = await this.dialog
      .open(EditorDialogComponent, { data: toBeEdited })
      .afterClosed()
      .toPromise();
    if (value == null) {
      return;
    }
    console.log(value);
    /*
    const edited = value as SchedulerEvent;
    const ref = this.loadingService.open();
    // const key = await this.networkService.editItem(edited);
    ref.close();
    const projectsWithOldRemoved = project == null
      ? this.projects : this.projects.filter(i => i.key !== project.key);
    projectsWithOldRemoved.push(new SchedulerProject(<SchedulerProject>{ ...edited, key: key }));
    this.projects = projectsWithOldRemoved.sort((a, b) => a.deadline - b.deadline);
    */
  }

}
