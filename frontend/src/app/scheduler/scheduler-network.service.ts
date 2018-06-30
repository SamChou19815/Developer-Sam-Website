import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { SchedulerItem } from './scheduler-item';

@Injectable()
export class SchedulerNetworkService {

  constructor(private http: HttpClient) {
  }

  async loadItems(): Promise<SchedulerItem[]> {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    return await this.http.get<SchedulerItem[]>('/apis/user/scheduler/load', {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).toPromise();
  }

  async editItem(data: SchedulerItem): Promise<string> {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const key = await this.http.post('/apis/user/scheduler/write', data, {
      responseType: 'text', withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).toPromise();
    if (key == null) {
      throw new Error();
    }
    return key;
  }

  async deleteItem(key: string) {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    await this.http.delete<string>('/apis/user/scheduler/delete', {
      params: new HttpParams().set('key', key), withCredentials: true,
      headers: { 'Firebase-Auth-Token': token }
    }).toPromise();
  }

  async markAs(completed: boolean, key: string) {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    await this.http.post('/apis/user/scheduler/mark_as', '', {
      params: new HttpParams().set('key', key).set('completed', String(completed)),
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).toPromise();
  }

}
