package com.developersam.framework.mcts

/**
 * An exception indicating that a method gives back no legal moves when some
 * legal moves are expected.
 */
class NoLegalMoveException : RuntimeException("There is no legal move found. " +
        "Check your getAllLegalMovesForAI() method.")
