import { HttpClient } from '@angular/common/http';
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
    return this.postDataForText('/apis/user/scheduler/edit?type=item', data);
  }

  async deleteItem(key: string): Promise<string> {
    return this.deleteWithParams('/apis/user/scheduler/delete', {
      'type': 'item', 'key': key
    });
  }

  async markAs(completed: boolean, key: string) {
    return this.postParams('/apis/user/scheduler/mark_item_as', {
      'key': key, 'completed': String(completed).trim()
    });
  }

}
