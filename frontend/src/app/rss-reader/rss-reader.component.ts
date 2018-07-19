import { Component, OnInit } from '@angular/core';
import { LoadingOverlayService } from '../shared/overlay/loading-overlay.service';
import { shortDelay } from '../shared/util';
import { RssReaderData, UserFeedItem } from './rss-reader-data';
import { RssReaderDataService } from './rss-reader-data.service';

@Component({
  selector: 'app-rss-reader',
  templateUrl: './rss-reader.component.html',
  styleUrls: ['./rss-reader.component.css']
})
export class RssReaderComponent implements OnInit {

  /**
   * Selected item is the current article selected by the user and its index, which can
   * be null to indicate that the user is not reading any one now.
   * @type {UserFeedItem | null}
   */
  private _selectedItem: UserFeedItem | null = null;
  /**
   * Reports whether the system is loading more data.
   * @type {boolean}
   */
  private _isLoadingMore = false;
  /**
   * Whether to show all articles.
   * @type {boolean}
   */
  doesShowAllArticles = true;

  constructor(private dataService: RssReaderDataService,
              private loadingService: LoadingOverlayService) {
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
   * Returns a list of starred articles for display.
   *
   * @returns {UserFeedItem[]} a list of starred articles for display.
   */
  get starredArticles(): UserFeedItem[] {
    return this.dataService.data.starredItems;
  }

  /**
   * Returns whether we have a selected item.
   *
   * @returns {boolean} whether we have a selected item.
   */
  get hasSelectedItem(): boolean {
    return this._selectedItem != null;
  }

  /**
   * Returns the current article selected by the user, which must be be non-null by contract.
   *
   * @returns {UserFeedItem} the current article selected by the user.
   */
  get selectedItem(): UserFeedItem {
    if (this._selectedItem == null) {
      throw new Error();
    }
    return this._selectedItem;
  }

  /**
   * Returns whether the system is loading more data.
   *
   * @returns {boolean} whether the system is loading more data.
   */
  get isLoadingMore(): boolean {
    return this._isLoadingMore;
  }

  /**
   * Returns whether the user can load more feed.
   *
   * @returns {boolean} whether the user can load more feed.
   */
  get canLoadMoreFeed(): boolean {
    const feed = this.dataService.data.feed;
    const len = feed.items.length;
    return len !== 0 && len % feed.limit === 0;
  }

  onScroll(event: Event) {
    const target = event.target;
    if (target == null) {
      return;
    }
    const htmlElement = target as HTMLElement;
    console.log(htmlElement.offsetHeight);
    console.log(htmlElement.scrollTop);
    console.log(htmlElement.scrollHeight);
    if (htmlElement.offsetHeight + htmlElement.scrollTop >= htmlElement.scrollHeight - 50) {
      this.loadMoreFeed();
    }
  }

  /**
   * Load more feed data to the client.
   */
  loadMoreFeed(): void {
    if (!this.canLoadMoreFeed || this._isLoadingMore) {
      return;
    }
    this._isLoadingMore = true;
    this.dataService.loadMoreFeed().then(() => this._isLoadingMore = false);
  }

  /**
   * Read an item.
   *
   * @param {UserFeedItem} item the item to read.
   */
  readItem(item: UserFeedItem) {
    this._selectedItem = item;
    this.dataService.markAs(item, true);
  }

  /**
   * Returns whether all items are read.
   *
   * @returns {boolean} whether all items are read.
   */
  get isAllRead(): boolean {
    const feed = this.feed;
    if (feed.length === 0) {
      // Reject this vacuously true case.
      return false;
    }
    return feed.every(i => i.isRead);
  }

  /**
   * Mark the selected item as unread.
   */
  markAsUnread() {
    if (this._selectedItem == null) {
      return;
    }
    this.dataService.markAs(this._selectedItem, false);
    this.goBackToItemList();
  }

  /**
   * Mark all as read.
   */
  markAllAsRead() {
    this.dataService.markAllAs(true);
  }

  /**
   * Mark all as unread.
   */
  markAllAsUnread() {
    this.dataService.markAllAs(false);
  }

  /**
   * Toggle star status of the selected item.
   */
  toggleStar() {
    if (this._selectedItem == null) {
      throw new Error();
    }
    this.dataService.toggleStar(this._selectedItem);
  }

  /**
   * Go back to the item list.
   */
  goBackToItemList() {
    this._selectedItem = null;
  }

}
