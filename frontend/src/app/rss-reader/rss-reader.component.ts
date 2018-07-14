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

}
