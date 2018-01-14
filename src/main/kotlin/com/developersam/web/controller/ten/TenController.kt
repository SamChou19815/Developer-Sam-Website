package com.developersam.web.controller.ten

import com.developersam.web.framework.mcts.MCTS
import com.developersam.web.model.ten.TenBoard
import com.developersam.web.model.ten.TenClientMove
import com.developersam.web.model.ten.TenServerResponse

/**
 * Controls the game.
 */
internal object TenController {

    /**
     * A game between two AIs.
     */
    private fun newGameAIVSAI() {
        val board = TenBoard()
        var moveCounter = 1
        var status = 0
        while (status == 0) {
            board.print()
            val decision = MCTS(board, 1500)
            val move = decision.selectMove()
            board.makeMoveWithoutCheck(move)
            status = board.gameStatus
            board.switchIdentity()
            println("Move $moveCounter finished.")
            val player = if (moveCounter % 2 == 0) "White" else "Black"
            val winningProb = move[2]
            val statement = "Winning Probability for $player is $winningProb%."
            System.out.println(statement)
            moveCounter++
        }
        board.print()
        println((if (status == 1) "Black" else "White") + " wins.")
    }

    /**
     * Run an game between two AI.
     *
     * @param args useless arguments.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        newGameAIVSAI()
    }

    /**
     * Respond to a [clientMove] represented by a [TenClientMove] object and
     * gives back the formatted [TenServerResponse].
     */
    internal fun respond(clientMove: TenClientMove): TenServerResponse {
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