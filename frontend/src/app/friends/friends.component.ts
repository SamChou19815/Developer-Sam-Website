import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { AlertComponent } from '../shared/alert/alert.component';
import { GoogleUser } from '../shared/google-user';
import { GoogleUserService } from '../shared/google-user.service';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { shortDelay } from '../shared/util';
import { FriendData } from './friend-data';
import { FriendsNetworkService } from './friends-network.service';

@Component({
  selector: 'app-friends',
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css']
})
export class FriendsComponent implements OnInit {

  emailInput = '';
  foundUser: GoogleUser | undefined;
  data: FriendData = FriendData.dummyData;

  constructor(private googleUserService: GoogleUserService,
              private networkService: FriendsNetworkService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(async () => {
      const ref = this.loadingService.open();
      this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
      this.data = await this.networkService.loadFriendsData();
      ref.close();
    });
  }

  async search() {
    const ref = this.loadingService.open();
    const user = await this.networkService.getUserInfo(this.emailInput);
    ref.close();
    if (user == null) {
      this.dialog.open(AlertComponent, { data: 'There is no user with this email.' });
      return;
    }
    this.foundUser = user;
  }

  async addFriendRequest() {
    const ref = this.loadingService.open();
    if (this.foundUser == null) {
      throw new Error();
    }
    await this.networkService.addFriendRequest(this.foundUser.key);
    ref.close();
    this.dialog.open(AlertComponent, { data: 'Your friend request has been sent.' });
  }

  async respond(user: GoogleUser, approved: boolean) {
    const action = approved ? 'accept' : 'reject';
    if (!confirm(`Do you want to ${action} this request?`)) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.respondFriendRequest(user.key, approved);
    this.data.requests = this.data.requests.filter(u => u.key !== user.key);
    if (approved) {
      this.data.list.push(user);
    }
    ref.close();
  }

  async removeFriend(user: GoogleUser) {
    if (!confirm(`Do you want to remove your friend (${user.name})?`)) {
      return;
    }
    const ref = this.loadingService.open();
    await this.networkService.removeFriend(user.key);
    this.data.list = this.data.list.filter(u => u.key !== user.key);
    ref.close();
  }

}
