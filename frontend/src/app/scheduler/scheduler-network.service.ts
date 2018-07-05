import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { SchedulerData, SchedulerItem } from './scheduler-data';

@Injectable({
  providedIn: 'root'
})
export class SchedulerNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient) {
    super(http);
  }

  async loadData(): Promise<SchedulerData> {
    return this.getData<SchedulerData>('/apis/user/scheduler/load');
  }

  async editItem(data: SchedulerItem): Promise<string> {
    const key = await this.http.post('/apis/user/scheduler/edit?type=item', data, {
      responseType: 'text', withCredentials: true, headers: this.firebaseAuthHeader
    }).toPromise();
    if (key == null) {
      throw new Error();
    }
    return key;
  }

  async deleteItem(key: string) {
    return this.http.delete<string>('/apis/user/scheduler/delete', {
      params: new HttpParams().set('type', 'item').set('key', key), withCredentials: true,
      headers: this.firebaseAuthHeader
    }).toPromise();
  }

  async markAs(completed: boolean, key: string) {
    return this.http.post('/apis/user/scheduler/mark_item_as', '', {
      params: new HttpParams().set('key', key).set('completed', String(completed)),
      withCredentials: true, headers: this.firebaseAuthHeader
    }).toPromise();
  }

}
