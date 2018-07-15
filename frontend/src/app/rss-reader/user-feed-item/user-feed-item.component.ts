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
    feedKey: '', title: '', link: '', description: '', publicationTime: 0, isRead: false
  };

  constructor() {
  }

  ngOnInit() {
  }

  /**
   * Returns the publication time in string.
   *
   * @returns {string} the publication time in string.
   */
  get publicationTime(): string {
    return new Date(this.item.publicationTime).toLocaleDateString();
  }

}
