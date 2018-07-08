import { Component, OnInit } from '@angular/core';
import { GoogleUserService } from '../google-user/google-user.service';
import { LoadingOverlayService } from '../overlay/loading-overlay.service';
import { shortDelay } from '../shared/util';
import { FriendData } from './friend-data';
import { FriendsNetworkService } from './friends-network.service';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css']
})
export class FriendsComponent implements OnInit {

  data: FriendData = FriendData.dummyData;

  constructor(private googleUserService: GoogleUserService,
              private networkService: FriendsNetworkService,
              private loadingService: LoadingOverlayService) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      this.data = await this.networkService.loadFriendsData();
      ref.close();
    });
  }

}
