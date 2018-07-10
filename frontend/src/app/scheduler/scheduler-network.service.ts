import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AuthenticatedNetworkService,
  HttpClientConfig
} from '../shared/authenticated-network-service';
import { SchedulerData } from './scheduler-data';
import { SchedulerEvent } from './scheduler-event';
import { SchedulerProject } from './scheduler-project';
import { AnnotatedSchedulerRecord } from './scheduler-record';

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

  async editProject(data: SchedulerProject): Promise<string> {
    return this.postDataForText('/apis/user/scheduler/edit/project', data);
  }

  async editEvent(data: SchedulerEvent): Promise<string> {
    return this.postDataForText('/apis/user/scheduler/edit/event', data);
  }

  async deleteRecord(key: string, type: 'project' | 'event'): Promise<string> {
    return this.deleteData(`/apis/user/scheduler/delete/${type}`, { 'key': key });
  }

  async markProjectAs(completed: boolean, key: string): Promise<string> {
    return this.postParams('/apis/user/scheduler/mark_project_as', {
      'key': key, 'completed': String(completed)
    });
  }

  async getAutoScheduling(friendKey?: string): Promise<AnnotatedSchedulerRecord[]> {
    const url = '/apis/user/scheduler/auto_schedule';
    const params: HttpClientConfig = friendKey == null ? {} : { 'friend_key': friendKey };
    return this.getData<AnnotatedSchedulerRecord[]>(url, params);
  }

}
