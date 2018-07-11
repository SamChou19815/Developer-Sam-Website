import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TenClientMove, TenServerResponse } from './ten-board';

@Injectable({
  providedIn: 'root'
})
export class TenNetworkService {

  constructor(private http: HttpClient) {
  }

  /**
   * Returns the promise of a server response.
   *
   * @param {TenClientMove} move the move of the client.
   * @returns {Promise<TenServerResponse>} the promise of a server response.
   */
  getGameResponse(move: TenClientMove): Promise<TenServerResponse> {
    return this.http.post<TenServerResponse>('/apis/public/ten/response', move).toPromise();
  }

}
