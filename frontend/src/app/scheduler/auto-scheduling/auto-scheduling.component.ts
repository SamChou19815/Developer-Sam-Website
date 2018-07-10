import { Component, OnInit } from '@angular/core';
import { GoogleUserService } from '../../shared/google-user.service';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { SchedulerNetworkService } from '../scheduler-network.service';
import { SchedulerTaggedInterval } from '../scheduler-tagged-interval';

@Component({
  selector: 'app-scheduler-auto-scheduling',
  templateUrl: './auto-scheduling.component.html',
  styleUrls: ['./auto-scheduling.component.css']
})
export class AutoSchedulingComponent implements OnInit {

  taggedIntervals: SchedulerTaggedInterval[] = [];

  constructor(private googleUserService: GoogleUserService,
              private networkService: SchedulerNetworkService,
              private loadingService: LoadingOverlayService) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      this.taggedIntervals = await this.networkService.getAutoScheduling();
      ref.close();
    });
  }

}
