package com.developersam.mcts

/**
 * This interface represents any game board that supports MCTS AI framework.
 */
interface Board {

    /**
     * Obtain identity of current player.
     */
    val currentPlayerIdentity: Int

    /**
     * Obtain a deep copy of the board to allow different simulations on the
     * same board without interference.
     */
    val copy: Board

    /**
     * Obtain the win value of a draw.
     * For example, a draw value of 0.5 means a draw is considered a half win.
     * The value should be between 0 to 1.
     */
    val drawValue: Double
        get() = 0.0

    /**
     * Obtain the [gameStatus] on current board.
     * This happens immediately after a player makes a move, before
     * switching identity.
     * The status must be 1, -1, 2 (draw), or 0 (inconclusive).
     */
    val gameStatus: Int

    /**
     * Obtain a list of all legal moves for AI.
     * DO NOT confuse: a legal move for human is not necessarily one for AI
     * because AI needs less moves to save time for computation.
     */
    val allLegalMovesForAI: Array<IntArray>

    /**
     * Make a [move] without any check, which can accelerate AI simulation.
     */
    fun makeMoveWithoutCheck(move: IntArray)

    /**
     * Switch the identity of current player on the board.
     * The identity must be 1 or -1, so that checking game status can determine
     * who wins.
     */
    fun switchIdentity()

}