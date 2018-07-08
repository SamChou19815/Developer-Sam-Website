import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
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

}
