package com.developersam.web.model.ten

/**
 * A data class that represents a client move.
 * - [boardBeforeHumanMove] specifies the board before the human move.
 * - [humanMove] completes the picture by providing human's move in a tuple.
 */
class TenClientMove(
        internal val boardBeforeHumanMove: TenBoardData,
        internal val humanMove: IntArray
)

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