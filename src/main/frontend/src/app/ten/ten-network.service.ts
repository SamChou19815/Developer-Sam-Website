import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TenClientMove, TenServerResponse } from './ten-board';

@Injectable()
export class TenNetworkService {

  /**
   * Initialize itself with injected http client.
   *
   * @param {HttpClient} http the injected http client.
   */
  constructor(private http: HttpClient) { }

  /**
   * Obtain the game response from the server.
   * This method is only responsible for receiving and parsing the resulting.
   *
   * @param {TenClientMove} clientMove the info from the client.
   * @param {(tenServerResponse: TenServerResponse) => void} callback callback function to process server response.
   */
  getGameResponse(clientMove: TenClientMove,
                  callback: (tenServerResponse: TenServerResponse) => void): void {
    this.http.post<TenServerResponse>('/apis/ten/response', clientMove)
      .subscribe(data => callback(data));
  }

}
