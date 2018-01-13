package com.developersam.ten;

/**
 * A data class that represents a server response to the client move.
 */
public class TenServerResponse {
    
    /**
     * A standard placeholder AI move.
     */
    private static final int[] PLACEHOLDER_AI_MOVE = {-1, -1};
    /**
     * A standard response to an illegal move.
     */
    static final TenServerResponse ILLEGAL_MOVE_RESPONSE =
            new TenServerResponse();
    /**
     * The move from AI.
     */
    private int[] aiMove;
    /**
     * Current big square legal position after AI move.
     */
    private int currentBigSquareLegalPosition;
    /**
     * Status of the game: 1: black wins, -1: white wins,
     * 0: continue, 2: illegal move.
     */
    private int status;
    /**
     * The winning probability estimated after AI move.
     */
    private int aiWinningProbability;
    
    /**
     * Construct a response representing an illegal move.
     */
    private TenServerResponse() {
        aiMove = PLACEHOLDER_AI_MOVE;
        currentBigSquareLegalPosition = -1;
        status = 2;
        aiWinningProbability = 0;
    }
    
    /**
     * Construct a TEN server response when the player wins before the AI can
     * move.
     *
     * @param winnerPlayerIdentity the winner player's identity.
     */
    TenServerResponse(int winnerPlayerIdentity) {
        this(PLACEHOLDER_AI_MOVE, -1,
                winnerPlayerIdentity, 0);
    }
    
    /**
     * Construct a TEN server response from a range of arguments that can
     * help the client decide the status of the game after the transmission.
     *
     * @param aiMove the move of the AI.
     * @param currentBigSquareLegalPosition the current big square legal
     * position after AI move.
     * @param status the status of the game after the move.
     * @param aiWinningProbability the winning probability of AI.
     */
    TenServerResponse(int[] aiMove, int currentBigSquareLegalPosition,
                      int status, int aiWinningProbability) {
        this.aiMove = aiMove;
        this.currentBigSquareLegalPosition = currentBigSquareLegalPosition;
        this.status = status;
        this.aiWinningProbability = aiWinningProbability;
    }
    
}
