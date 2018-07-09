import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { GoogleUser } from '../shared/google-user';
import { FriendData } from './friend-data';

@Injectable({
  providedIn: 'root'
})
export class FriendsNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient) {
    super(http);
  }

  async loadFriendsData(): Promise<FriendData> {
    return this.getData<FriendData>('/apis/user/friends/load');
  }

  async getUserInfo(email: string): Promise<GoogleUser | null> {
    return this.getData<GoogleUser | null>(`/apis/user/friends/get_user_info?email=${email}`);
  }

  async addFriendRequest(key: string) {
    await this.postParams('/apis/user/friends/add_friend_request', {
      'responder_user_key': key
    });
  }

  async respondFriendRequest(key: string, approved: boolean) {
    return this.postParams('/apis/user/friends/respond_friend_request', {
      'requester_user_key': key, 'approved': String(approved)
    });
  }

  async removeFriend(key: string) {
    return this.deleteWithParams('/apis/user/friends/remove_friend', {
      'removed_friend_key': key
    });
  }

}
