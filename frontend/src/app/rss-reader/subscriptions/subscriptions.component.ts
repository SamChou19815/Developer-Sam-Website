import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { AlertComponent } from '../../shared/alert/alert.component';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { asyncRun, shortDelay } from '../../shared/util';
import { Feed } from '../rss-reader-data';
import { RssReaderDataService } from '../rss-reader-data.service';

@Component({
  selector: 'app-rss-reader-subscriptions',
  templateUrl: './subscriptions.component.html',
  styleUrls: ['./subscriptions.component.css']
})
export class SubscriptionsComponent implements OnInit {

  /**
   * The input for RSS Feed URL.
   * @type {string}
   */
  rssFeedUrlInput = '';

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
   * Returns the subscriptions for display.
   *
   * @returns {RssReaderData} the subscriptions for display.
   */
  get subscriptions(): Feed[] {
    return this.dataService.data.subscriptions;
  }

  /**
   * Returns whether the add feed button should be disabled to ensure input correctness.
   *
   * @returns {boolean} whether the add feed button should be disabled to ensure input correctness.
   */
  get isAddFeedButtonDisabled(): boolean {
    const url = this.rssFeedUrlInput.trim();
    return url.length === 0 ||
      this.dataService.data.subscriptions.find(v => v.rssUrl === url) != null;
  }

  /**
   * Add a feed.
   *
   * @param {KeyboardEvent} event the optional keyboard event.
   */
  addFeed(event?: KeyboardEvent): void {
    if (event && event.code !== 'Enter') {
      return;
    }
    asyncRun(async () => {
      const ref = this.loadingService.open();
      const isSuccessful = await this.dataService.addFeed(this.rssFeedUrlInput);
      const msg = isSuccessful ? 'You have successfully added a feed.' : 'The feed URL is invalid.';
      ref.close();
      this.dialog.open(AlertComponent, { data: msg });
    });
  }

}
