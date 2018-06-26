import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoadingOverlayService } from '../overlay/loading-overlay.service';
import { SchedulerItem } from './scheduler-item';

@Injectable()
export class SchedulerNetworkService {

  constructor(private http: HttpClient, private loadingService: LoadingOverlayService) {
  }

  loadItems(success: (items: SchedulerItem[]) => void): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const ref = this.loadingService.open();
    this.http.get<SchedulerItem[]>('/apis/user/scheduler/load', {
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).subscribe(items => {
      ref.close();
      success(items);
    });
  }

  editItem(data: SchedulerItem, success: (key: string) => void): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const ref = this.loadingService.open();
    this.http.post('/apis/user/scheduler/write', data, {
      responseType: 'text', withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).subscribe(key => {
      ref.close();
      if (key == null) {
        throw new Error();
      }
      success(key);
    });
  }

  deleteItem(key: string, success: () => void): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const ref = this.loadingService.open();
    this.http.delete<string>('/apis/user/scheduler/delete', {
      params: new HttpParams().set('key', key), withCredentials: true,
      headers: { 'Firebase-Auth-Token': token }
    }).subscribe(() => {
      ref.close();
      success();
    });
  }

  markAs(completed: boolean, key: string, success: () => void): void {
    const token = localStorage.getItem('token');
    if (token == null) {
      throw new Error();
    }
    const ref = this.loadingService.open();
    this.http.post('/apis/user/scheduler/mark_as', '', {
      params: new HttpParams().set('key', key).set('completed', String(completed)),
      withCredentials: true, headers: { 'Firebase-Auth-Token': token }
    }).subscribe(() => {
      ref.close();
      success();
    });
  }

}
