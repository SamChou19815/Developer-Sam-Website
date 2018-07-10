import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerNetworkService } from '../scheduler-network.service';

@Component({
  selector: 'app-scheduler-auto-scheduling',
  templateUrl: './auto-scheduling.component.html',
  styleUrls: ['./auto-scheduling.component.css']
})
export class AutoSchedulingComponent implements OnInit {

  constructor(private googleUserService: GoogleUserService,
              private networkService: SchedulerNetworkService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      const scheduledRecords = await this.networkService.getAutoScheduling();
      console.log(scheduledRecords);
      ref.close();
    });
  }

}
