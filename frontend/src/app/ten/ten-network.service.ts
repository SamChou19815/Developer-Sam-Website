import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TenClientMove, TenServerResponse } from './ten-board';

@Injectable()
export class TenNetworkService {

  constructor(private http: HttpClient) {
  }

  getGameResponse(clientMove: TenClientMove,
                  callback: (tenServerResponse: TenServerResponse) => void): void {
    this.http.post<TenServerResponse>('/apis/public/ten/response', clientMove)
      .subscribe(data => callback(data));
  }

}
