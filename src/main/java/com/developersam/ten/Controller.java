package com.developersam.ten;

import com.developersam.mcts.MCTS;

/**
 * Controls the game.
 */
public final class Controller {
    
    /**
     * So that nobody can create a controller instance.
     */
    private Controller() {}
    
    /**
     * A game between two AIs.
     */
    private static void newGameAIVSAI() {
        TenBoard board = new TenBoard();
        int moveCounter = 1;
        int status = 0;
        while (status == 0) {
            board.print();
            MCTS decision = new MCTS(board, 1500, true);
            int[] move = decision.selectMove();
            board.makeMoveWithoutCheck(move);
            status = board.getGameStatus();
            board.switchIdentity();
            System.out.println("Move " + moveCounter + " finished.");
            System.out.format("Winning Probability for %s is %d%%.\n",
                    moveCounter % 2 == 0? "White": "Black", move[2]);
            moveCounter++;
        }
        board.print();
        System.out.println((status == 1? "Black": "White") + " wins.");
    }
    
    /**
     * Run an game between two AI.
     *
     * @param args useless arguments.
     */
    public static void main(String[] args) {
        newGameAIVSAI();
    }
    
    /**
     * Respond to a human move represented by a client move object.
     *
     * @param clientMove the formatted client move object.
     * @return the move response from the server.
     */
    public static TenServerResponse respond(TenClientMove clientMove) {
        if (clientMove.boardBeforeHumanMove == null
                || clientMove.humanMove == null) {
            // Refuse to process illegal data.
            return null;
        }
        TenBoard board = new TenBoard(clientMove.boardBeforeHumanMove);
        board.switchIdentity();
        if (!board.makeMove(clientMove.humanMove)) {
            return TenServerResponse.ILLEGAL_MOVE_RESPONSE;
        }
        int status = board.getGameStatus();
        if (status == 1) {
            // Black wins before AI move
            return new TenServerResponse(1);
        } else if (status == -1) {
            // White wins before AI move
            return new TenServerResponse(-1);
        } else {
            board.switchIdentity();
        }
        MCTS decision = new MCTS(board, 1500);
        int[] aiMove = decision.selectMove();
        board.makeMove(aiMove);
        status = board.getGameStatus();
        return new TenServerResponse(new int[]{aiMove[0], aiMove[1]},
                board.getCurrentBigSquareLegalPosition(), status, aiMove[2]);
    }
    
}