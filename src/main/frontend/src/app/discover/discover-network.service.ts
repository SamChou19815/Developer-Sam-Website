import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { LoadingOverlayService } from "../overlay/loading-overlay.service";
import { PublicUser, PublicUserData } from "./public-users";

@Injectable()
export class DiscoverNetworkService {

  /**
   * Initialize the service via the injected http client and a loading service.
   *
   * @param {HttpClient} http the http client.
   * @param {LoadingOverlayService} loadingService the loading service.
   */
  constructor(private http: HttpClient, private loadingService: LoadingOverlayService) { }

  /**
   * Load a list of public users and give back the list to client.
   *
   * @param {(items: PublicUser[]) => void} success to handle the list when succeeded.
   */
  loadPublicUsers(success: (users: PublicUser[]) => void): void {
    const token = localStorage.getItem('token');
    this.http.get<PublicUser[]>('/apis/discover/load?token=' + token, {
      withCredentials: true
    }).subscribe(users => success(users));
  }

  /**
   * Update public user data.
   *
   * @param {PublicUserData} publicUserData the data to be written to the database.
   * @param {(items: PublicUser[]) => void} success to handle when succeed.
   */
  writePublicUserData(publicUserData: PublicUserData, success: (users: PublicUser[]) => void): void {
    const token = localStorage.getItem('token');
    const ref = this.loadingService.open();
    this.http.post('/apis/discover/update?token=' + token, publicUserData, {
      responseType: 'text',
      withCredentials: true
    }).subscribe(resp => {
      ref.close();
      if (resp === 'true') {
        this.loadPublicUsers(success);
      } else {
        throw new Error('Bad Response: ' + resp);
      }
    });
  }

}
