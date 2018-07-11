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

  /**
   * The input value of email to search for a user.
   * @type {string}
   */
  emailInput = '';
  /**
   * The user found from the server, used for user searching.
   */
  foundUser: GoogleUser | undefined;
  /**
   * All the friend data to display.
   * @type {FriendData}
   */
  data: FriendData = <FriendData>{ list: [], requests: [] };

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

  /**
   * Search for a user.
   */
  search(): void {
    (async () => {
      const ref = this.loadingService.open();
      const user = await this.networkService.getUserInfo(this.emailInput);
      ref.close();
      if (user == null) {
        this.dialog.open(AlertComponent, { data: 'There is no user with this email.' });
        return;
      }
      this.foundUser = user;
    })();
  }

  /**
   * Add a friend from the recorded user.
   */
  addFriendRequest(): void {
    (async () => {
      if (this.foundUser == null) {
        return;
      }
      const ref = this.loadingService.open();
      await this.networkService.addFriendRequest(this.foundUser.key);
      ref.close();
      this.dialog.open(AlertComponent, { data: 'Your friend request has been sent.' });
    })();
  }

  /**
   * Respond a friend request from the given user.
   *
   * @param {GoogleUser} user the user who sent the request.
   * @param {boolean} isApproved whether to approve the request.
   */
  respond(user: GoogleUser, isApproved: boolean) {
    (async () => {
      const action = isApproved ? 'accept' : 'reject';
      if (!confirm(`Do you want to ${action} this request?`)) {
        return;
      }
      const ref = this.loadingService.open();
      await this.networkService.respondFriendRequest(user.key, isApproved);
      this.data.requests = this.data.requests.filter(u => u.key !== user.key);
      if (isApproved) {
        this.data.list.push(user);
      }
      ref.close();
    })();
  }

  /**
   * Remove a user as friend.
   *
   * @param {GoogleUser} user the user to unfriend.
   */
  removeFriend(user: GoogleUser) {
    (async () => {
      if (!confirm(`Do you want to remove your friend (${user.name})?`)) {
        return;
      }
      const ref = this.loadingService.open();
      await this.networkService.removeFriend(user.key);
      this.data.list = this.data.list.filter(u => u.key !== user.key);
      ref.close();
    })();
  }

}
