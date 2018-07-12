import { GoogleUser } from '../shared/google-user';

/**
 * [FriendData] is the collection of friend related data].
 */
export interface FriendData {
  /**
   * A list of friends.
   */
  list: GoogleUser[];
  /**
   * A list of friend requests.
   */
  requests: GoogleUser[];
  /**
   * A marker to tell whether the data set has been initialized.
   */
  isNotInitialized?: boolean;
}

/**
 * The dummy friend data.
 * @type {FriendData}
 */
export const dummyFriendData: FriendData = <FriendData>{
  list: [], requests: [], isNotInitialized: true
};
