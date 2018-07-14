import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { LoadingOverlayService } from '../../shared/overlay/loading-overlay.service';
import { shortDelay } from '../../shared/util';
import { Feed } from '../rss-reader-data';
import { RssReaderDataService } from '../rss-reader-data.service';

@Component({
  selector: 'app-rss-reader-subscriptions',
  templateUrl: './subscriptions.component.html',
  styleUrls: ['./subscriptions.component.css']
})
export class SubscriptionsComponent implements OnInit {

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

}
