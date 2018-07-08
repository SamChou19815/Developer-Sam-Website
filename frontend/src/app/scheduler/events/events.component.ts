import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerEvent } from '../scheduler-event';
import { SchedulerNetworkService } from '../scheduler-network.service';

@Component({
  selector: 'app-scheduler-events',
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css']
})
export class EventsComponent implements OnInit {

  events: SchedulerEvent[] = [];

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
      this.events = data.events.map(i => new SchedulerEvent(i));
      ref.close();
    });
  }

}
