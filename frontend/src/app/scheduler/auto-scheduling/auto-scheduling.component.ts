import { Component, OnInit } from '@angular/core';
import { FriendsNetworkService } from '../../friends/friends-network.service';
import { GoogleUser } from '../../shared/google-user';
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

  /**
   * A list of all tagged intervals for user only.
   * @type {SchedulerTaggedInterval[]}
   */
  taggedIntervals: SchedulerTaggedInterval[] = [];
  /**
   * A list of all friends.
   * @type {GoogleUser[]}
   */
  friends: GoogleUser[] = [];

  /**
   * The selected friend.
   */
  selectedFriend: GoogleUser | undefined;
  /**
   * A list of all tagged intervals for user and his friend.
   * @type {SchedulerTaggedInterval[]}
   */
  taggedIntervalsWithFriends: SchedulerTaggedInterval[] = [];

  constructor(private googleUserService: GoogleUserService,
              private schedulerNetworkService: SchedulerNetworkService,
              private friendsNetworkService: FriendsNetworkService,
              private loadingService: LoadingOverlayService) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      const firebaseAuthToken = await this.googleUserService.afterSignedIn();
      this.schedulerNetworkService.firebaseAuthToken = firebaseAuthToken;
      this.friendsNetworkService.firebaseAuthToken = firebaseAuthToken;
      const [taggedIntervals, friendsData] = await Promise.all([
        this.schedulerNetworkService.getAutoScheduling(),
        this.friendsNetworkService.loadFriendsData()
      ]);
      this.taggedIntervals = taggedIntervals;
      this.friends = friendsData.list;
      ref.close();
    });
  }

  /**
   * Do auto scheduling with selected friend. Must be called only when the friend selected is
   * active.
   */
  doAutoSchedulingWithFriend() {
    (async () => {
      if (this.selectedFriend == null) {
        throw new Error();
      }
      this.taggedIntervalsWithFriends = [];
      const ref = this.loadingService.open();
      this.taggedIntervalsWithFriends =
        await this.schedulerNetworkService.getAutoScheduling(this.selectedFriend.key);
      ref.close();
    })();
  }

  /**
   * Clear intervals with friends to start over.
   */
  clearIntervalsWithFriends() {
    this.taggedIntervalsWithFriends = [];
    this.selectedFriend = undefined;
  }

}
