package com.developersam.ten

/**
 * A data class that represents a server response to the client move.
 *
 * Construct a [TenServerResponse] from a range of arguments that can
 * help the client decide the status of the game after the transmission:
 * - [aiMove] specifies the move of the AI, which can be a place holder value.
 * - [currentBigSquareLegalPosition] specifies the current big square legal
 * position after AI move.
 * - [status] specifies the status of the game after the move.
 * - [aiWinningProbability] specifies the winning probability of AI.
 */
class TenServerResponse(
        private var aiMove: IntArray,
        private var currentBigSquareLegalPosition: Int,
        private var status: Int,
        private var aiWinningProbability: Int
) {

    companion object Factory {
        /**
         * A standard placeholder AI move.
         */
        private val placeholderMove = intArrayOf(-1, -1)
        /**
         * A standard response to an illegal move.
         */
        internal val illegalMoveResponse = TenServerResponse(placeholderMove,
                -1, 2, 0)

        /**
         * Create a [TenServerResponse] when the player wins before the AI can
         * move.
         */
        internal fun whenPlayerWin(winnerIdentity: Int): TenServerResponse =
                TenServerResponse(placeholderMove, -1,
                        winnerIdentity, 0)
    }

}