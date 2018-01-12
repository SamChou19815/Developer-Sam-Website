package com.developersam.mcts;

/**
 * An exception indicating that a method gives back no legal moves when some
 * legal moves are expected.
 */
public final class NoLegalMoveException extends RuntimeException {
    
    /**
     * Construct itself with no arguments.
     */
    public NoLegalMoveException() {
        super("There is no legal move found. " +
                "Check your getAllLegalMovesForAI() method.");
    }
    
}
