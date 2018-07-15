import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { GoogleUserService } from '../shared/google-user.service';
import { CursoredUserFeed, Feed, RssReaderData } from './rss-reader-data';

@Injectable({
  providedIn: 'root'
})
export class RssReaderDataService extends AuthenticatedNetworkService {

  /**
   * The data stored.
   * @type {RssReaderData}
   * @private
   */
  private _data: RssReaderData = <RssReaderData>{
    feed: <CursoredUserFeed>{ items: [], cursor: '' },
    subscriptions: [],
    isNotInitialized: true
  };

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
   * Returns the RSS data for display.
   *
   * @returns {RssReaderData} the RSS data for display.
   */
  get data(): RssReaderData {
    return this._data;
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
    const { cursor, items } = await this.getData<CursoredUserFeed>('/load_more_feed', {
      'cursor': this._data.feed.cursor
    });
    this._data.feed.cursor = cursor;
    this._data.feed.items.push(...items);
  }

  /**
   * Subscribe to a feed with given URL.
   *
   * @param {string} url url of the feed.
   * @returns {Promise<boolean>} promise to tell whether the operations succeeds.
   */
  async subscribe(url: string): Promise<boolean> {
    const data = await this.postParamsForData<RssReaderData | null>(
      '/subscribe', { 'url': url });
    if (data == null) {
      return false;
    }
    this._data = data;
    return true;
  }

  /**
   * Unsubscribe a feed at the given the index.
   *
   * @param {Feed} feed the feed to unsubscribe.
   * @param {number} index the index of the feed.
   * @returns {Promise<void>} promise when done.
   */
  async unsubscribe(feed: Feed, index: number): Promise<void> {
    await this.deleteData('/unsubscribe', { 'key': feed.key });
    this._data.subscriptions.splice(index, 1);
  }

}
