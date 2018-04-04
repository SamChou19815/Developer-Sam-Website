import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SchedulerItem, SchedulerItemData } from './scheduler-item';
import { LoadingOverlayService } from '../overlay/loading-overlay.service';

@Injectable()
export class SchedulerNetworkService {

  /**
   * Initialize the service via the injected http client and a loading service.
   *
   * @param {HttpClient} http the http client.
   * @param {LoadingOverlayService} loadingService the loading service.
   */
  constructor(private http: HttpClient, private loadingService: LoadingOverlayService) { }

  /**
   * Load a list of scheduler items and give back the list to client.
   *
   * @param {(items: SchedulerItem[]) => void} success to handle the list when succeeded.
   */
  loadSchedulerItems(success: (items: SchedulerItem[]) => void): void {
    const token = localStorage.getItem('token');
    this.http.get<SchedulerItem[]>('/apis/scheduler/load?token=' + token, {
      withCredentials: true
    }).subscribe(items => success(items));
  }

  /**
   * Write a scheduler item.
   *
   * @param {SchedulerItemData} schedulerItemData the data to be written to the database.
   * @param {(items: SchedulerItem[]) => void} success to handle when succeed.
   */
  writeSchedulerItem(schedulerItemData: SchedulerItemData, success: (items: SchedulerItem[]) => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.post('/apis/scheduler/write?token=' + token, schedulerItemData, {
      responseType: 'text',
      withCredentials: true
    }).subscribe(_ => {
      ref.close();
      this.loadSchedulerItems(success);
    });
  }

  /**
   * Delete a scheduler item.
   *
   * @param {string} key key of the item.
   * @param {(items: SchedulerItem[]) => void} success to handle when succeed.
   */
  deleteSchedulerItem(key: string, success: (items: SchedulerItem[]) => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.delete<string>('/apis/scheduler/delete?token=' + token, {
      params: new HttpParams().set('key', key),
      withCredentials: true
    }).subscribe(_ => {
      ref.close();
      this.loadSchedulerItems(success);
    });
  }

  /**
   * Mark a scheduler item as completed or uncompleted.
   *
   * @param {boolean} completed whether the items should be marked as completed or not.
   * @param {string} key key of the item.
   * @param {(items: SchedulerItem[]) => void} success to handle when succeed.
   */
  markSchedulerItem(completed: boolean, key: string, success: (items: SchedulerItem[]) => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.post('/apis/scheduler/markAs?token=' + token, '', {
      params: new HttpParams().set('key', key).set('completed', String(completed)),
      withCredentials: true
    }).subscribe(_ => {
      ref.close();
      this.loadSchedulerItems(success);
    });
  }

}
