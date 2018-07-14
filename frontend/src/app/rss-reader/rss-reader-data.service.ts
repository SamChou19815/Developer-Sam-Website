import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { GoogleUserService } from '../shared/google-user.service';
import { CursoredFeed, dummyRssReaderData, RssReaderData } from './rss-reader-data';

@Injectable({
  providedIn: 'root'
})
export class RssReaderDataService extends AuthenticatedNetworkService {

  /**
   * The data stored.
   * @type {RssReaderData}
   * @private
   */
  private _data: RssReaderData = dummyRssReaderData;

  constructor(http: HttpClient, private googleUserService: GoogleUserService) {
    super(http, '/apis/user/rss_reader/');
  }

  /**
   * Initialize the RSS Reader app with loaded data.
   *
   * @returns {Promise<void>} promise when done.
   */
  async initializeRssReaderApp(): Promise<void> {
    if (!this._data.isNotInitialized) {
      return;
    }
    this.firebaseAuthToken = await this.googleUserService.afterSignedIn();
    this._data = await this.loadData();
  }

  /**
   * Returns the promise of the full RssReaderData.
   *
   * @returns {Promise<RssReaderData>} the promise of the full RssReaderData.
   */
  private async loadData(): Promise<RssReaderData> {
    return this.getData<RssReaderData>('/load');
  }

  /**
   * Load more feed data to the client.
   *
   * @returns {Promise<void>} promise when done.
   */
  async loadMoreFeed(): Promise<void> {
    const { cursor, items } = await this.getData<CursoredFeed>('/load_more_feed', {
      'cursor': this._data.feed.cursor
    });
    this._data.feed.cursor = cursor;
    this._data.feed.items.push(...items);
  }

  /**
   * Add a new feed to subscriptions.
   *
   * @param {string} url url of the feed.
   * @returns {Promise<boolean>} promise to tell whether the operations succeeds.
   */
  async addFeed(url: string): Promise<boolean> {
    const data = await this.postParamsForData<RssReaderData | null>(
      '/add_feed', { 'url': url });
    if (data == null) {
      return false;
    }
    this._data = data;
    return true;
  }

}
