import { Component, Input, OnInit } from '@angular/core';
import { UserFeedItem } from '../rss-reader-data';

@Component({
  selector: 'app-rss-reader-user-feed-item',
  templateUrl: './user-feed-item.component.html',
  styleUrls: ['./user-feed-item.component.css']
})
export class UserFeedItemComponent implements OnInit {

  /**
   * The item to display.
   * @type {UserFeedItem}
   */
  @Input() item: UserFeedItem = <UserFeedItem>{
    feedKey: '', title: '', link: '', description: '', isRead: false, lastUpdatedTime: 0
  };

  constructor() {
  }

  ngOnInit() {
  }

  /**
   * Returns the last updated time in string.
   *
   * @returns {string} the last updated time in string.
   */
  get lastUpdatedTime(): string {
    return new Date(this.item.lastUpdatedTime).toLocaleDateString();
  }

}
