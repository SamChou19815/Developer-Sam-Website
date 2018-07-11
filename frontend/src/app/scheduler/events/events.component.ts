import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerEvent, SchedulerEvents, SchedulerEventType } from '../scheduler-event';
import { SchedulerNetworkService } from '../scheduler-network.service';
import { EditorDialogComponent } from './editor-dialog/editor-dialog.component';

/**
 * An event with its index.
 */
export interface EventWithIndex {
  /**
   * Event.
   */
  event: SchedulerEvent;
  /**
   * Index.
   */
  index: number;
}

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

  /**
   * Process the event list after successfully editing it.
   *
   * @param {SchedulerEvent} newEvent the edited new event.
   * @param {EventWithIndex} eventWithIndex original event with index.
   */
  private processListAfterEditing(newEvent: SchedulerEvent, eventWithIndex?: EventWithIndex): void {
    // remove old
    if (eventWithIndex != null) {
      const { index } = eventWithIndex;
      switch (newEvent.type) {
        case SchedulerEventType.ONE_TIME:
          this.events.oneTimeEvents.splice(index, 1);
          break;
        case SchedulerEventType.WEEKLY:
          this.events.weeklyEvents.splice(index, 1);
          break;
      }
    }
    switch (newEvent.type) {
      case SchedulerEventType.ONE_TIME:
        this.events.oneTimeEvents.push(newEvent);
        this.events.oneTimeEvents.sort((a, b) => a.repeatConfig - b.repeatConfig);
        break;
      case SchedulerEventType.WEEKLY:
        this.events.weeklyEvents.push(newEvent);
        this.events.weeklyEvents.sort((a, b) => b.repeatConfig - a.repeatConfig);
        break;
    }
  }

  /**
   * Edit the event with index.
   *
   * @param {EventWithIndex} eventWithIndex the event with index, which can be omitted.
   */
  editEvent(eventWithIndex?: EventWithIndex): void {
    (async () => {
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
      const newEvent = new SchedulerEvent(<SchedulerEvent>{ ...edited, key: key });
      this.processListAfterEditing(newEvent, eventWithIndex);
      ref.close();
    })();
  }

  /**
   * Delete an event.
   *
   * @param {SchedulerEvent} event event to delete.
   * @param {number} index index of the event.
   */
  deleteEvent(event: SchedulerEvent, index: number): void {
    (async () => {
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
    })();
  }

}
