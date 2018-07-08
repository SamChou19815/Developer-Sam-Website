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
}

export namespace FriendData {

  /**
   * The dummy data for [FriendData].
   * @type {FriendData}
   */
  export const dummyData: FriendData = { list: [], requests: [] };

}

