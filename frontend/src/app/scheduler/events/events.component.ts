import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerDataService } from '../scheduler-data.service';
import { SchedulerEvent, SchedulerEvents, SchedulerEventWithIndex } from '../scheduler-event';
import { EditorDialogComponent } from './editor-dialog/editor-dialog.component';

@Component({
  selector: 'app-scheduler-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

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
   * Returns whether the data is initialized.
   *
   * @returns {boolean} whether the data is initialized.
   */
  get isInitialized(): boolean {
    return !this.dataService.schedulerData.isNotInitialized;
  }

  /**
   * Returns the events to display.
   *
   * @returns {SchedulerEvents} the events to display.
   */
  get events(): SchedulerEvents {
    return this.dataService.schedulerData.events;
  }

  /**
   * Edit the event with index.
   *
   * @param {SchedulerEventWithIndex} eventWithIndex the event with index, which can be omitted.
   */
  editEvent(eventWithIndex?: SchedulerEventWithIndex): void {
    const toBeEdited = eventWithIndex == null ?
      new SchedulerEvent() : new SchedulerEvent(eventWithIndex.event);
    const index = eventWithIndex ? eventWithIndex.index : undefined;
    this.dialog.open(EditorDialogComponent, { data: toBeEdited })
      .afterClosed().subscribe(value => {
      if (value == null) {
        return;
      }
      const ref = this.loadingService.open();
      this.dataService.editEvent(value as SchedulerEvent, index).then(ref.close);
    });
  }

  /**
   * Delete an event.
   *
   * @param {SchedulerEventWithIndex} deletedEventWithIndex the deleted event with its index.
   */
  deleteEvent(deletedEventWithIndex: SchedulerEventWithIndex): void {
    const ref = this.loadingService.open();
    this.dataService.deleteEvent(deletedEventWithIndex).then(ref.close);
  }

}
