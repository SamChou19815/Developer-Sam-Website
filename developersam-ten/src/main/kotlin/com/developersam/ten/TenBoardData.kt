package com.developersam.ten

/**
 * A class that is responsible for holding the simplified version of the TEN
 * board, without tracking extra game status. The class is designed as
 * a transmission object.
 * - [board] describes the 9x9 board.
 * - [currentBigSquareLegalPosition] is used to determine the current game
 * status.
 * - [currentPlayerIdentity] is used to determine the identity of AI.
 */
class TenBoardData(
        internal val board: Array<IntArray>,
        internal val currentBigSquareLegalPosition: Int,
        internal val currentPlayerIdentity: Int
)