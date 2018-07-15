import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { AlertComponent } from '../shared/alert/alert.component';
import { GoogleUser } from '../shared/google-user';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { asyncRun, shortDelay } from '../shared/util';
import { FriendData } from './friend-data';
import { FriendsDataService } from './friends-data.service';
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

  constructor(private networkService: FriendsNetworkService,
              private dataService: FriendsDataService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(() => {
      const ref = this.loadingService.open();
      this.dataService.initializedFriendsApp().then(ref.close);
    });
  }

  /**
   * Returns all the friend data to display.
   *
   * @returns {FriendData} all the friend data to display.
   */
  get data(): FriendData {
    return this.dataService.data;
  }

  /**
   * Search for a user.
   *
   * @param {KeyboardEvent} event the optional keyboard event.
   */
  search(event?: KeyboardEvent): void {
    if (event && event.code !== 'Enter') {
      return;
    }
    asyncRun(async () => {
      const ref = this.loadingService.open();
      const user = await this.networkService.getUserInfo(this.emailInput);
      ref.close();
      if (user == null) {
        this.dialog.open(AlertComponent, { data: 'There is no user with this email.' });
        return;
      }
      this.foundUser = user;
    });
  }

  /**
   * Add a friend from the recorded user.
   */
  addFriendRequest(): void {
    asyncRun(async () => {
      if (this.foundUser == null) {
        return;
      }
      const ref = this.loadingService.open();
      await this.networkService.addFriendRequest(this.foundUser.key);
      ref.close();
      this.dialog.open(AlertComponent, { data: 'Your friend request has been sent.' });
    });
  }

  /**
   * Respond a friend request from the given user.
   *
   * @param {GoogleUser} user the user who sent the request.
   * @param {index} index of the user in the list.
   * @param {boolean} approved whether to approve the request.
   */
  respondToFriendRequest(user: GoogleUser, index: number, approved: boolean): void {
    if (!confirm(`Do you want to ${approved ? 'accept' : 'reject'} this request?`)) {
      return;
    }
    const ref = this.loadingService.open();
    this.dataService.respondToFriendRequest(user, index, approved).then(ref.close);
  }

  /**
   * Remove a user as friend.
   *
   * @param {GoogleUser} user the user to unfriend.
   * @param {index} index of the user in the list.
   */
  removeFriend(user: GoogleUser, index: number): void {
    if (!confirm(`Do you want to remove your friend (${user.name})?`)) {
      return;
    }
    const ref = this.loadingService.open();
    this.dataService.removeFriend(user, index).then(ref.close);
  }

}
