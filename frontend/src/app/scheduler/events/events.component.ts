import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerEvent, SchedulerEvents, SchedulerEventType } from '../scheduler-event';
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

  async editEvent(eventWithIndex?: { event: SchedulerEvent, index: number }) {
    const toBeEdited = eventWithIndex == null ?
      new SchedulerEvent() : new SchedulerEvent(eventWithIndex.event);
    const value: any = await this.dialog
      .open(EditorDialogComponent, { data: toBeEdited })
      .afterClosed()
      .toPromise();
    if (value == null) {
      return;
    }
    const edited = value as SchedulerEvent;
    const ref = this.loadingService.open();
    const key = await this.networkService.editEvent(edited);
    // remove old
    if (eventWithIndex != null) {
      const { index } = eventWithIndex;
      switch (edited.type) {
        case SchedulerEventType.ONE_TIME:
          this.events.oneTimeEvents.splice(index, 1);
          break;
        case SchedulerEventType.WEEKLY:
          this.events.weeklyEvents.splice(index, 1);
          break;
      }
    }
    const newEvent = new SchedulerEvent(<SchedulerEvent>{ ...edited, key: key });
    switch (edited.type) {
      case SchedulerEventType.ONE_TIME:
        this.events.oneTimeEvents.push(newEvent);
        this.events.oneTimeEvents.sort((a, b) => a.repeatConfig - b.repeatConfig);
        break;
      case SchedulerEventType.WEEKLY:
        this.events.weeklyEvents.push(newEvent);
        this.events.weeklyEvents.sort((a, b) => b.repeatConfig - a.repeatConfig);
        break;
    }
    ref.close();
  }

  async deleteEvent(event: SchedulerEvent, index: number) {
    if (event.key == null) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.deleteRecord(event.key, 'event');
    switch (event.type) {
      case SchedulerEventType.ONE_TIME:
        this.events.oneTimeEvents.splice(index, 1);
        break;
      case SchedulerEventType.WEEKLY:
        this.events.weeklyEvents.splice(index, 1);
        break;
    }
    ref.close();
  }

}
