package com.developersam.web.model.ten;

/**
 * A data class that represents a client move.
 */
public class TenClientMove {
    
    /**
     * The board data before the human move.
     */
    final TenBoardData boardBeforeHumanMove;
    /**
     * The new human move.
     */
    final int[] humanMove;
    
    /**
     * The default no arg constructor for GSON.
     */
    private TenClientMove() {
        boardBeforeHumanMove = null;
        humanMove = null;
    }
    
}
