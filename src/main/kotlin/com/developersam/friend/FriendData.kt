package com.developersam.friend

import com.developersam.auth.GoogleUser

/**
 * [FriendData] is a combination of friend [list] and [requests] for the given user.
 */
class FriendData private constructor(
        private val list: List<GoogleUser>, private val requests: List<GoogleUser>
) {

    /**
     * Construct the [FriendData] for the given [user].
     */
    constructor(user: GoogleUser): this(list = FriendPair[user], requests = FriendRequest[user])

}
