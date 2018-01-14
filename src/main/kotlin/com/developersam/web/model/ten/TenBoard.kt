package com.developersam.web.model.ten

import com.developersam.web.framework.mcts.Board
import com.developersam.web.framework.mcts.MCTS

import java.util.Arrays
import java.util.LinkedList

/**
 * The board of the game ten. It implements the [Board] interface from the MCTS
 * framework so that there is an AI for it.
 */
class TenBoard : Board {

    /**
     * In variable names, a big square refers to a 3*3 square;
     * a tile refers to a 1*1 square.
     * Each tile is either 1, -1 or 0 (black, white, empty).
     */
    private val board: Array<IntArray>
    /**
     * keep track of winning progress on big squares
     * 1, -1, 0, 2 mean black wins, white wins, inconclusive, and all occupied.
     */
    private val bigSquaresStatus: IntArray
    /**
     * The current legal big square to pick as next move. If it's value is -1,
     * that means the user can place the move everywhere.
     * This variable is important for maintaining the current game state.
     */
    var currentBigSquareLegalPosition: Int
        private set
    /**
     * The identity of the current player. Must be 1 or -1.
     */
    private var _currentPlayerIdentity: Int

    override val currentPlayerIdentity: Int
        get() = _currentPlayerIdentity;

    /**
     * Whether the board is empty.
     *
     * @return empty or not.
     */
    private val isEmpty: Boolean
        get() {
            return board.none { smallSquare -> smallSquare.any { it != 0 } }
        }

    override val copy: TenBoard
        get() = TenBoard(this)

    override val allLegalMovesForAI: Array<IntArray>
        get() {
            if (isEmpty) {
                return initialAILegalMoves
            }
            val list = LinkedList<IntArray>()
            if (currentBigSquareLegalPosition == -1) {
                for (i in 0..8) {
                    for (j in 0..8) {
                        if (isLegalMove(i, j)) {
                            list.add(intArrayOf(i, j))
                        }
                    }
                }
            } else {
                for (j in 0..8) {
                    if (isLegalMove(currentBigSquareLegalPosition, j)) {
                        list.add(intArrayOf(currentBigSquareLegalPosition, j))
                    }
                }
            }
            return list.toTypedArray()
        }

    override val gameStatus: Int
        get() {
            for (i in 0..8) {
                updateBigSquareStatus(i)
            }
            val simpleStatus = getSimpleStatusFromSquare(bigSquaresStatus)
            if (simpleStatus == 1 || simpleStatus == -1) {
                return simpleStatus
            }
            for (i in 0..8) {
                if (bigSquaresStatus[i] == 0) {
                    return 0
                }
            }
            var blackBigSquareCounter = 0
            var whiteBigSquareCounter = 0
            for (i in 0..8) {
                val status = bigSquaresStatus[i]
                if (status == 1) {
                    blackBigSquareCounter++
                } else if (status == -1) {
                    whiteBigSquareCounter++
                }
            }
            return if (blackBigSquareCounter > whiteBigSquareCounter) 1 else -1
        }

    /**
     * Construct a fresh new TEN board.
     */
    constructor() {
        board = Array(9) { IntArray(9) }
        bigSquaresStatus = IntArray(9)
        // Black can choose any position initially
        currentBigSquareLegalPosition = -1
        // Black plays first.
        _currentPlayerIdentity = 1
    }

    /**
     * Initialize the board from a data class [TenBoardData] with all the
     * necessary info in [data]. Note that the big square legal positions array
     * is not presented because it can be computed at the server side without
     * extra information.
     */
    internal constructor(data: TenBoardData) {
        board = data.board
        bigSquaresStatus = IntArray(9)
        for (i in 0..8) {
            updateBigSquareStatus(i)
        }
        currentBigSquareLegalPosition = data.currentBigSquareLegalPosition
        _currentPlayerIdentity = data.currentPlayerIdentity
    }

    /**
     * Initialize the board from an old board [oldBoard].
     */
    private constructor(oldBoard: TenBoard) {
        board = Array(9) { IntArray(0) }
        bigSquaresStatus = IntArray(9)
        for (i in 0..8) {
            // Copy to maintain value safety.
            board[i] = Arrays.copyOf(oldBoard.board[i], 9)
            // Just copy value to speed up without another around of
            // calculation.
            bigSquaresStatus[i] = oldBoard.bigSquaresStatus[i]
        }
        currentBigSquareLegalPosition = oldBoard.currentBigSquareLegalPosition
        _currentPlayerIdentity = oldBoard._currentPlayerIdentity
    }

    /**
     * Decode int [i] stored internally in data structure to player name.
     */
    private fun decode(i: Int): String {
        return when (i) {
            0 -> "0"
            1 -> "b"
            -1 -> "w"
            else -> throw Error("Bad Data in Board!")
        }
    }

    /**
     * Print the board.
     */
    fun print() {
        println("Current Player: " +
                if (currentPlayerIdentity == 1) "Black" else "White")
        println("Printing the board:")
        println("-----------------")
        for (row in 0..2) {
            for (innerRow in 0..2) {
                print(decode(board[row * 3][innerRow * 3]) + " "
                        + decode(board[row * 3][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3][innerRow * 3 + 2]) + "|")
                print(decode(board[row * 3 + 1][innerRow * 3]) + " "
                        + decode(board[row * 3 + 1][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3 + 1][innerRow * 3 + 2]) + "|")
                print(decode(board[row * 3 + 2][innerRow * 3]) + " "
                        + decode(board[row * 3 + 2][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3 + 2][innerRow * 3 + 2]))
                print('\n')
            }
            if (row != 2) {
                println("- - -*- - -*- - -")
            }
        }
        println("-----------------")
    }

    /**
     * Check whether a move is legal, where move is given by ([a], [b]).
     */
    private fun isLegalMove(a: Int, b: Int): Boolean {
        if (a < 0 || a > 8 || b < 0 || b > 8) {
            // Out of boundary values
            return false
        }
        return if (currentBigSquareLegalPosition != -1
                && currentBigSquareLegalPosition != a) {
            // in the wrong big square when it cannot have a free move
            false
        } else {
            // not in the occupied big square and on an empty tile
            bigSquaresStatus[a] == 0 && board[a][b] == 0
        }
    }

    /**
     * Check whether a move [move], represented by int array tuple [a, b],
     * is legal.
     */
    private fun isLegalMove(move: IntArray): Boolean {
        return isLegalMove(move[0], move[1])
    }

    override fun makeMoveWithoutCheck(move: IntArray) {
        board[move[0]][move[1]] = _currentPlayerIdentity
        updateBigSquareStatus(move[0])
        currentBigSquareLegalPosition = when {
            bigSquaresStatus[move[1]] == 0 -> move[1]
            else -> -1
        }
    }

    /**
     * Make a move [move] with legality check and tells whether the move is
     * legal/successful.
     */
    internal fun makeMove(move: IntArray): Boolean {
        if (!isLegalMove(move)) {
            return false
        }
        makeMoveWithoutCheck(move)
        return true
    }

    /**
     * A function that helps to determine whether a square [square] belongs
     * to black (1) or white (-1).
     * If there is no direct victory, it will return 0.
     */
    private fun getSimpleStatusFromSquare(square: IntArray): Int {
        /**
         * Perform a naive check on the square [s] about whether the player with
         * identity [id] win the square. It only checks according to the
         * primitive tic-tac-toe rule.
         */
        fun playerSimplyWinSquare(s: IntArray, id: Int): Boolean {
            return s[0] == id && s[1] == id && s[2] == id
                    || s[3] == id && s[4] == id && s[5] == id
                    || s[6] == id && s[7] == id && s[8] == id
                    || s[0] == id && s[3] == id && s[6] == id
                    || s[1] == id && s[4] == id && s[7] == id
                    || s[2] == id && s[5] == id && s[8] == id
                    || s[0] == id && s[4] == id && s[8] == id
                    || s[2] == id && s[4] == id && s[6] == id
        }
        return when {
            playerSimplyWinSquare(square, 1) -> 1
            playerSimplyWinSquare(square, -1) -> -1
            else -> 0
        }
    }

    /**
     * Update the big square status for ONE big square of id [bigSquareID].
     */
    private fun updateBigSquareStatus(bigSquareID: Int) {
        val bigSquare = board[bigSquareID]
        val bigSquareStatus = getSimpleStatusFromSquare(bigSquare)
        if (bigSquareStatus == 1 || bigSquareStatus == -1) {
            bigSquaresStatus[bigSquareID] = bigSquareStatus
            return
            // already won by a player
        }
        for (i in 0..8) {
            if (bigSquare[i] == 0) {
                // there is a space left.
                bigSquaresStatus[bigSquareID] = 0
                return
            }
        }
        bigSquaresStatus[bigSquareID] = 2 // no space left.
    }

    override fun switchIdentity() {
        _currentPlayerIdentity = _currentPlayerIdentity.inv() + 1
    }

    companion object {
        /**
         * Pre-computed initial legal moves for AI. Due to symmetry, many values
         * can be omitted.
         */
        private val initialAILegalMoves = arrayOf(
                intArrayOf(0, 0), intArrayOf(0, 1), intArrayOf(0, 2),
                intArrayOf(0, 4), intArrayOf(0, 5), intArrayOf(0, 8),
                intArrayOf(1, 0), intArrayOf(1, 1), intArrayOf(1, 3),
                intArrayOf(1, 4), intArrayOf(1, 6), intArrayOf(1, 7),
                intArrayOf(4, 0), intArrayOf(4, 1), intArrayOf(4, 4))

        /**
         * Respond to a [clientMove] represented by a [TenClientMove] object and
         * gives back the formatted [TenServerResponse].
         */
        fun respond(clientMove: TenClientMove): TenServerResponse {
            val board = TenBoard(clientMove.boardBeforeHumanMove)
            board.switchIdentity()
            if (!board.makeMove(clientMove.humanMove)) {
                // Stop illegal move from corrupting game data.
                return TenServerResponse.illegalMoveResponse
            }
            var status = board.gameStatus
            when (status) {
                1, -1 -> // Black/White wins before AI move
                    return TenServerResponse.whenPlayerWin(status)
                else -> board.switchIdentity()
            }
            // Let AI think
            val decision = MCTS(board, 1500)
            val aiMove = decision.selectMove()
            board.makeMove(aiMove)
            status = board.gameStatus
            // A full response.
            return TenServerResponse(intArrayOf(aiMove[0], aiMove[1]),
                    board.currentBigSquareLegalPosition, status, aiMove[2])
        }
    }

}
