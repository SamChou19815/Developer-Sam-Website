import { Injectable } from '@angular/core';
import { GoogleUser } from '../shared/google-user';
import { GoogleUserService } from '../shared/google-user.service';
import { dummyFriendData, FriendData } from './friend-data';
import { FriendsNetworkService } from './friends-network.service';

@Injectable({
  providedIn: 'root'
})
export class FriendsDataService {

  private _data: FriendData = dummyFriendData;

  constructor(private googleUserService: GoogleUserService,
              private networkService: FriendsNetworkService) {
  }

  /**
   * Initialize the friends app with loaded data.
   *
   * @returns {Promise<void>} promise when done.
   */
  async initializedFriendsApp(): Promise<void> {
    if (!this._data.isNotInitialized) {
      return;
    }
    this.networkService.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    this._data = await this.networkService.friendData$;
  }

  /**
   * Returns the friend data.
   *
   * @returns {FriendData}
   */
  get data(): FriendData {
    return this._data;
  }

  /**
   * Respond a friend request from the given user.
   *
   * @param {GoogleUser} user the user who sent the request.
   * @param {index} index of the user in the list.
   * @param {boolean} approved whether to approve the request.
   * @returns {Promise<void>} promise when done.
   */
  async respondToFriendRequest(user: GoogleUser, index: number, approved: boolean): Promise<void> {
    await this.networkService.respondFriendRequest(user.key, approved);
    this._data.requests.splice(index, 1);
    if (approved) {
      this._data.list.push(user);
    }
  }

  /**
   * Remove a user as friend.
   *
   * @param {GoogleUser} user the user to unfriend.
   * @param {index} index of the user in the list.
   * @returns {Promise<void>} promise when done.
   */
  async removeFriend(user: GoogleUser, index: number): Promise<void> {
    await this.networkService.removeFriend(user.key);
    this._data.list.splice(index, 1);
  }

}
