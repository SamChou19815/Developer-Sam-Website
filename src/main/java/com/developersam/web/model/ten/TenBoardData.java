package com.developersam.web.model.ten;

/**
 * A data class that is responsible for holding the simplified version of the
 * ten board, without tracking extra game status. The class is designed as
 * a transmission object.
 */
final class TenBoardData {
    
    /**
     * The 9 * 9 board representation.
     */
    final int[][] board;
    /**
     * Current big square legal positions. Same as that in {@code TenBoard}.
     */
    final int currentBigSquareLegalPosition;
    /**
     * Current player identity.
     */
    final int currentPlayerIdentity;
    
    /**
     * A default constructor for GSON.
     */
    private TenBoardData() {
        board = null;
        currentBigSquareLegalPosition = -1;
        currentPlayerIdentity = -1;
    }
    
}
