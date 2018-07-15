import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
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
   * Selected item is the current article selected by the user, which can be null to indicate that
   * the user is not reading any one now.
   * @type {UserFeedItem | null}
   */
  private _selectedItem: UserFeedItem | null = null;

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
   * Read an item at the specified index.
   *
   * @param {UserFeedItem} item the item to read.
   * @param {number} index the index of the item.
   */
  readItem(item: UserFeedItem, index: number) {
    this._selectedItem = item;
  }

  /**
   * Go back to the item list.
   */
  goBackToItemList() {
    this._selectedItem = null;
  }

}
