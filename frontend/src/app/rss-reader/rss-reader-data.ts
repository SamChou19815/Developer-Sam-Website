/**
 * [Feed] defines an RSS feed's most general information.
 */
export interface Feed {
  /**
   * Key of the feed.
   */
  readonly key: string;
  /**
   * URL of the feed.
   */
  readonly rssUrl: string;
  /**
   * Title of the feed.
   */
  readonly title: string;
  /**
   * Link of the feed.
   */
  readonly link: string;
  /**
   * Description of the feed.
   */
  readonly description: string;
}

/**
 * [FeedItem] defines an item belongs to a feed.
 */
export interface FeedItem {
  /**
   * Key of the parent feed.
   */
  readonly feedKey: string;
  /**
   * Title of the item.
   */
  readonly title: string;
  /**
   * Link of the item.
   */
  readonly link: string;
  /**
   * Description of the item.
   */
  readonly description: string;
}

/**
 * [UserFeedItem] defines a user information annotated RSS feed item.
 */
export interface UserFeedItem extends FeedItem {
  /**
   * Whether the item is read.
   */
  readonly isRead: boolean;
  /**
   * Last updated time of the item.
   */
  readonly lastUpdatedTime: number;
}

/**
 * [CursoredUserFeed] defines a collection feed with a cursor for pagination fetch.
 */
export interface CursoredUserFeed {
  /**
   * The collection of items.
   */
  readonly items: UserFeedItem[];
  /**
   * The cursor for pagination fetch.
   */
  cursor: string;
}

/**
 * [RssReaderData] defines the collection of user data in RSS Reader.
 */
export interface RssReaderData {
  /**
   * Feed data.
   */
  readonly feed: CursoredUserFeed;
  /**
   * Subscription data.
   */
  readonly subscriptions: Feed[];
  /**
   * A marker to tell whether the data set has been initialized.
   */
  readonly isNotInitialized?: boolean;
}

/**
 * The dummy data as placeholder.
 * @type {RssReaderData}
 */
export const dummyRssReaderData: RssReaderData = <RssReaderData>{
  feed: <CursoredUserFeed>{ items: [], cursor: '' }, subscriptions: [], isNotInitialized: true
};
