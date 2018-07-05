import { HttpClient } from '@angular/common/http';

/**
 * [HttpClientHeader] is the type for HttpClient header.
 */
export interface HttpClientHeader {
  /**
   * The header config.
   */
  [p: string]: string | string[];
}

/**
 * [AuthenticatedNetworkService] is designed to be a superclass that provides a set of convenience
 * methods for a network service that involves auth to backend.
 */
export class AuthenticatedNetworkService {

  /**
   * [_firebaseAuthToken] is the token stored in the service.
   */
  private _firebaseAuthToken: string | undefined;

  /**
   * Construct itself by Angular's HTTP Client.
   *
   * @param {HttpClient} http Angular's HTTP Client.
   */
  constructor(protected http: HttpClient) {
  }

  /**
   * Sets the currently used [firebaseAuthToken] to [token].
   *
   * @param {string} token the new token.
   */
  set firebaseAuthToken(token: string) {
    this._firebaseAuthToken = token;
  }

  /**
   * Returns the header for auth.
   *
   * @returns {HttpClientHeader} the header for auth.
   */
  protected get firebaseAuthHeader(): HttpClientHeader {
    if (this._firebaseAuthToken == null) {
      throw new Error();
    }
    return { 'Firebase-Auth-Token': this._firebaseAuthToken };
  }

  /**
   * Returns the promise of data of type T.
   *
   * @param {string} url url to fetch.
   * @returns {Promise<T>} the promise of data of type T.
   */
  protected async getData<T>(url: string): Promise<T> {
    return this.http.get<T>(url, {
      withCredentials: true, headers: this.firebaseAuthHeader
    }).toPromise();
  }

}
