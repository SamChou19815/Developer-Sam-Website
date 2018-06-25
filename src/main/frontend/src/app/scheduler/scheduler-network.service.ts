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
    const ref = this.loadingService.open();
    this.http.get<SchedulerItem[]>('/apis/scheduler/load?token=' + token, {
      withCredentials: true
    }).subscribe(items => {
      ref.close();
      success(items);
    });
  }

  editItem(data: SchedulerItem, success: (key: string) => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.post('/apis/scheduler/write?token=' + token, data, {
      responseType: 'text',
      withCredentials: true
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
    const ref = this.loadingService.open();
    this.http.delete<string>('/apis/scheduler/delete?token=' + token, {
      params: new HttpParams().set('key', key),
      withCredentials: true
    }).subscribe(() => {
      ref.close();
      success();
    });
  }

  markAs(completed: boolean, key: string, success: () => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.post('/apis/scheduler/mark_as?token=' + token, '', {
      params: new HttpParams().set('key', key).set('completed', String(completed)),
      withCredentials: true
    }).subscribe(() => {
      ref.close();
      success();
    });
  }

}
