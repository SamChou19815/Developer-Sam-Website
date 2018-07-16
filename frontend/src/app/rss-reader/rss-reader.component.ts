import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { shortDelay } from '../shared/util';
import { RssReaderData, UserFeedItem, UserFeedItemWithIndex } from './rss-reader-data';
import { RssReaderDataService } from './rss-reader-data.service';

@Component({
  selector: 'app-rss-reader',
  templateUrl: './rss-reader.component.html',
  styleUrls: ['./rss-reader.component.css']
})
export class RssReaderComponent implements OnInit {

  /**
   * Selected item with index is the current article selected by the user and its index, which can
   * be null to indicate that the user is not reading any one now.
   * @type {UserFeedItemWithIndex | null}
   */
  private _selectedItemWithIndex: UserFeedItemWithIndex | null = null;

  constructor(private dataService: RssReaderDataService,
              private loadingService: LoadingOverlayService,
              private dialog: MatDialog) {
  }

  ngOnInit() {
    shortDelay(() => {
      const ref = this.loadingService.open();
      this.dataService.initializeRssReaderApp().then(ref.close);
    });
  }

  /**
   * Returns whether the data is initialized.
   *
   * @returns {boolean} whether the data is initialized.
   */
  get isInitialized(): boolean {
    return !this.dataService.data.isNotInitialized;
  }

  /**
   * Returns the feed for display.
   *
   * @returns {RssReaderData} the feed for display.
   */
  get feed(): UserFeedItem[] {
    return this.dataService.data.feed.items;
  }

  /**
   * Returns whether we have a selected item.
   *
   * @returns {boolean} whether we have a selected item.
   */
  get hasSelectedItem(): boolean {
    return this._selectedItemWithIndex != null;
  }

  /**
   * Returns the current article selected by the user, which must be be non-null by contract.
   *
   * @returns {UserFeedItem} the current article selected by the user.
   */
  get selectedItem(): UserFeedItem {
    if (this._selectedItemWithIndex == null) {
      throw new Error();
    }
    return this._selectedItemWithIndex.item;
  }

  /**
   * Returns whether the user can load more feed.
   *
   * @returns {boolean} whether the user can load more feed.
   */
  get canLoadMoreFeed(): boolean {
    const feed = this.dataService.data.feed;
    return feed.items.length % feed.limit === 0;
  }

  /**
   * Load more feed data to the client.
   */
  loadMoreFeed(): void {
    const ref = this.loadingService.open();
    this.dataService.loadMoreFeed().then(ref.close);
  }

  /**
   * Read an item at the specified index.
   *
   * @param {UserFeedItemWithIndex} itemWithIndex the item to read with its index.
   */
  readItem(itemWithIndex: UserFeedItemWithIndex) {
    this._selectedItemWithIndex = itemWithIndex;
    this.dataService.markAs(itemWithIndex, true);
  }

  /**
   * Mark the selected item with its index as unread.
   */
  markAsUnread() {
    if (this._selectedItemWithIndex == null) {
      return;
    }
    this.dataService.markAs(this._selectedItemWithIndex, false);
    this._selectedItemWithIndex = null;
  }

  /**
   * Go back to the item list.
   */
  goBackToItemList() {
    this._selectedItemWithIndex = null;
  }

}
