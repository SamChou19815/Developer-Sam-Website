package com.developersam.mcts;

/**
 * This interface represents any game board that supports MCTS AI framework.
 */
public interface Board {
    
    /**
     * Obtain identity of current player.
     *
     * @return identity
     */
    int getCurrentPlayerIdentity();
    
    /**
     * Obtain a deep copy of the board to allow different simulations on the
     * same board without interference.
     *
     * @return a deep copy of the board.
     */
    Board getCopy();
    
    /**
     * Make a move without any check, which can accelerate AI simulation.
     *
     * @param move a move.
     */
    void makeMoveWithoutCheck(int[] move);
    
    /**
     * Switch the identity of current player on the board.
     * The identity must be 1 or -1, so that checking game status can determine
     * who wins.
     */
    void switchIdentity();
    
    /**
     * Obtain the win value of a draw.
     * For example, a draw value of 0.5 means a draw is considered a half win.
     * The value should be between 0 to 1.
     *
     * @return a win value for draw.
     */
    default double getDrawValue() {
        return 0;
    }
    
    /**
     * Obtain the status of the game on current board.
     * This happens immediately after a player makes a move, before
     * switching identity.
     *
     * @return the status must be 1, -1, 2 (draw), or 0 (inconclusive).
     */
    int getGameStatus();
    
    /**
     * Obtain a list of all legal moves for AI.
     * DO NOT confuse: a legal move for human is not necessarily one for AI
     * because AI needs less moves to save time for computation.
     *
     * @return a list of moves represented by int arrays.
     */
    int[][] getAllLegalMovesForAI();
    
}