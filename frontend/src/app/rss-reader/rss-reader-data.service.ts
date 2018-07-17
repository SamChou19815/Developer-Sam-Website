import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AuthenticatedNetworkService } from '../shared/authenticated-network-service';
import { GoogleUserService } from '../shared/google-user.service';
import { ignore } from '../shared/util';
import { Feed, RssReaderData, UserFeed, UserFeedItem } from './rss-reader-data';

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
    feed: <UserFeed>{ items: [], limit: 50, cursor: '' },
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
   * @returns {Promise<void>} promise when done.
   */
  async loadMoreFeed(): Promise<void> {
    const feed = this._data.feed;
    const { cursor, items } = await this.getData<UserFeed>('/load_more_feed', {
      'cursor': feed.cursor
    });
    feed.cursor = cursor;
    feed.items.push(...items);
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
   */
  unsubscribe(feed: Feed, index: number): void {
    this._data.subscriptions.splice(index, 1);
    this.deleteData('/unsubscribe', { 'key': feed.key }).then(ignore);
  }

  /**
   * Mark the item as read or not.
   *
   * @param {UserFeedItem} item the item to mark.
   * @param {boolean} isRead whether the item should be marked as 'read' or not.
   */
  markAs(item: UserFeedItem, isRead: boolean): void {
    item.isRead = isRead;
    this.postParamsForText('/mark_as', {
      'key': item.key, 'is_read': String(isRead)
    }).then(ignore);
  }

  /**
   * Mark all items as read or not.
   *
   * @param {boolean} isRead whether the items should be marked as 'read' or not.
   */
  markAllAs(isRead: boolean): void {
    this._data.feed.items.forEach(item => item.isRead = isRead);
    this.postParamsForText('/mark_all_as', { 'is_read': String(isRead) }).then(ignore);
  }

}
