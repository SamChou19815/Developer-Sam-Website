package com.developersam.model.ten

import com.developersam.framework.mcts.MCTS

/**
 * Runs the game.
 */
object TenRunner {

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
            println("Winning Probability for $player is ${move[2]}%.")
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

}