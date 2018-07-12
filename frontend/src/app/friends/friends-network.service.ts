import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { GoogleUser } from '../shared/google-user';
import { wait } from '../shared/util';
import { FriendData } from './friend-data';

@Injectable({
  providedIn: 'root'
})
export class FriendsNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient) {
    super(http);
  }

  /**
   * Returns the promise of all friend data.
   *
   * @returns {Promise<FriendData>} the promise of all friend data.
   */
  get friendData$(): Promise<FriendData> {
    return this.getData<FriendData>('/apis/user/friends/load');
  }

  /**
   * Returns the promise of a friend information or `null` given the email.
   *
   * @param {string} email the given email.
   * @returns {Promise<GoogleUser | null>} the promise of a friend information or `null`.
   */
  async getUserInfo(email: string): Promise<GoogleUser | null> {
    return this.getData<GoogleUser | null>(`/apis/user/friends/get_user_info?email=${email}`);
  }

  /**
   * Asynchronously sends an add friend request.
   *
   * @param {string} key key of the user to friend.
   * @returns {Promise<void>} the promise when done.
   */
  async addFriendRequest(key: string): Promise<void> {
    await this.postParams('/apis/user/friends/add_friend_request', {
      'responder_user_key': key
    });
  }

  /**
   * Asynchronously sends a response to a friend request.
   *
   * @param {string} key key of the user to respond.
   * @param {boolean} isApproved whether to approve the friend request.
   * @returns {Promise<void>} the promise when done.
   */
  async respondFriendRequest(key: string, isApproved: boolean): Promise<void> {
    await this.postParams('/apis/user/friends/respond_friend_request', {
      'requester_user_key': key, 'approved': String(isApproved)
    });
  }

  /**
   * Asynchronously sends a remove friend request.
   *
   * @param {string} key key of the user to unfriend.
   * @returns {Promise<void>} the promise when done.
   */
  async removeFriend(key: string): Promise<void> {
    await this.deleteData('/apis/user/friends/remove_friend', {
      'removed_friend_key': key
    });
  }

}
