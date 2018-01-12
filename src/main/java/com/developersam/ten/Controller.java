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
     * Respond to human move with parsed input.
     *
     * @param prevB previous board representation.
     * @param prevBigSqrLegalPos previous big square legal position.
     * @param idAI id of AI.
     * @param move move of human.
     * @return format [move[0], move[1], legalPos, status, winningProbability].
     */
    private static int[] respondToHumanMove(int[][] prevB,
                                            int prevBigSqrLegalPos,
                                            int idAI, int[] move) {
        TenBoard board = new TenBoard(prevB, prevBigSqrLegalPos, idAI);
        board.switchIdentity();
        if (board.makeMove(move)) {
            int status = board.getGameStatus();
            if (status == 1) {
                return new int[]{1, -1, 0, 1, 0}; // Black wins before AI move
            } else if (status == -1) {
                return new int[]{-1, 1, 0, -1, 0}; // White wins before AI move
            } else {
                board.switchIdentity();
            }
            MCTS decision = new MCTS(board, 1500);
            int[] aiMove = decision.selectMove();
            board.makeMove(aiMove);
            status = board.getGameStatus();
            return new int[]{aiMove[0], aiMove[1],
                    board.currentBigSquareLegalPosition, status, aiMove[2]};
        } else {
            return new int[]{-1, -1, 0, 2, 0}; // Illegal move
        }
    }
    
    /**
     * A helper method to convert board string to board int 2d array ready for
     * use.
     *
     * @param boardString a compact string representation of the board.
     * @return the resultant int 2d array board.
     */
    private static int[][] stringToIntBoard(String boardString) {
        String[] prevBoardBigSquareParts = boardString.split(";");
        int[][] board = new int[9][9];
        for (int i = 0; i < 9; i++) {
            String[] smallSquares = prevBoardBigSquareParts[i].split(",");
            for (int j = 0; j < 9; j++) {
                board[i][j] = Integer.parseInt(smallSquares[j]);
            }
        }
        return board;
    }
    
    /**
     * Respond to a coded human move with format:
     * [string] := [prevBoard] [prevBigSquareLegalPosition] [idOfAI] [move]
     * [prevBoard] := a,a,a,a,a,a,a,a,a;a,a,a,a,a,a,a,a;....;a,a,a,a,a,a,a,a,a
     * [prevBigSquareLegalPosition] := a number
     * [idOfAI] := 1 or -1
     * [move] := a,b
     *
     * @param clientInfo a human move with all necessary context coded in a
     * string.
     * @return ai move or other messages
     */
    public static int[] respondToHumanMove(String clientInfo) {
        String[] parts = clientInfo.split(" ");
        int[][] prevBoard = stringToIntBoard(parts[0]);
        int prevBigSquareLegalPosition = Integer.parseInt(parts[1]);
        int idOfAI = Integer.parseInt(parts[2]);
        String[] moveParts = parts[3].split(",");
        int[] move = new int[]{Integer.parseInt(moveParts[0]),
                Integer.parseInt(moveParts[1])};
        return respondToHumanMove(prevBoard, prevBigSquareLegalPosition,
                idOfAI, move);
    }
    
}