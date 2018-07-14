import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AuthenticatedNetworkService,
  HttpClientConfig
} from '../shared/authenticated-network-service';
import { SchedulerData } from './scheduler-data';
import { SchedulerEvent } from './scheduler-event';
import { SchedulerProject } from './scheduler-project';
import { SchedulerTaggedInterval } from './scheduler-tagged-interval';

@Injectable({
  providedIn: 'root'
})
export class SchedulerNetworkService extends AuthenticatedNetworkService {

  constructor(http: HttpClient) {
    super(http);
  }

  /**
   * Returns the promise of all scheduler related data.
   *
   * @returns {Promise<SchedulerData>} the promise of all scheduler related data.
   */
  async loadData(): Promise<SchedulerData> {
    return this.getData<SchedulerData>('/apis/user/scheduler/load');
  }

  /**
   * Returns the promise of the key of the edited project.
   *
   * @param {SchedulerProject} data the edited project.
   * @returns {Promise<string>} the promise of the key of the edited project.
   */
  async editProject(data: SchedulerProject): Promise<string> {
    return this.postDataForText('/apis/user/scheduler/edit/project', data);
  }

  /**
   * Returns the promise of the key of the edited event.
   *
   * @param {SchedulerEvent} data the edited event.
   * @returns {Promise<string>} the promise of the key of the edited event.
   */
  async editEvent(data: SchedulerEvent): Promise<string> {
    return this.postDataForText('/apis/user/scheduler/edit/event', data);
  }

  /**
   * Asynchronously deletes a scheduler record.
   *
   * @param {string} key key of the record.
   * @param {"project" | "event"} type type of the record.
   * @returns {Promise<string>} promise of completion.
   */
  async deleteRecord(key: string, type: 'project' | 'event'): Promise<void> {
    await this.deleteData(`/apis/user/scheduler/delete/${type}`, { 'key': key });
  }

  /**
   * Asynchronously marks project as complete or not.
   *
   * @param {boolean} completed whether the project should be marked as completed or not.
   * @param {string} key key of the project.
   * @returns {Promise<void>} promise of completion.
   */
  async markProjectAs(completed: boolean, key: string): Promise<void> {
    await this.postParams('/apis/user/scheduler/mark_project_as', {
      'key': key, 'completed': String(completed)
    });
  }

  /**
   * Returns the promise of auto scheduling result.
   *
   * @param {string} friendKey key of friend user, if omitted, it means fetching for user himself.
   * @returns {Promise<SchedulerTaggedInterval[]>} the promise of auto scheduling result.
   */
  async getAutoScheduling(friendKey?: string): Promise<SchedulerTaggedInterval[]> {
    const url = `/apis/user/scheduler/${friendKey ? 'friend' : 'personal' }_auto_schedule`;
    const params: HttpClientConfig = friendKey ? { 'friend_key': friendKey } : {};
    return this.getData<SchedulerTaggedInterval[]>(url, params);
  }

}
