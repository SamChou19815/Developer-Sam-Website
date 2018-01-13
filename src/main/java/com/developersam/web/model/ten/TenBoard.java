package com.developersam.web.model.ten;

import com.developersam.web.framework.mcts.Board;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The board of the game ten.
 */
final class TenBoard implements Board {
    
    /**
     * In variable names, a big square refers to a 3*3 square;
     * a tile refers to a 1*1 square.
     * Each tile is either 1, -1 or 0 (black, white, empty).
     */
    private final int[][] board;
    /**
     * keep track of winning progress on big squares
     * 1, -1, 0, 2 mean black wins, white wins, inconclusive, and all occupied.
     */
    private final int[] bigSquaresStatus;
    /**
     * The current legal big square to pick as next move. If it's value is -1,
     * that means the user can place the move everywhere.
     */
    private int currentBigSquareLegalPosition;
    /**
     * The identity of the current player.
     */
    private int currentPlayerIdentity;
    /**
     * Pre-computed initial legal moves for AI.
     */
    private static final int[][] INITIAL_AI_LEGAL_MOVES = new int[][]{
            {0, 0}, {0, 1}, {0, 2}, {0, 4}, {0, 5}, {0, 8},
            {1, 0}, {1, 1}, {1, 3}, {1, 4}, {1, 6}, {1, 7},
            {4, 0}, {4, 1}, {4, 4}
    };
    
    /**
     * Construct a fresh new TEN board.
     */
    TenBoard() {
        board = new int[9][9];
        bigSquaresStatus = new int[9];
        // Black can choose any position initially
        currentBigSquareLegalPosition = -1;
        // Black plays first.
        currentPlayerIdentity = 1;
    }
    
    /**
     * Initialize the board from a data class with all the necessary info.
     *
     * @param data the data that holds all the necessary info to reestablish the
     * game, but leaving out the big square legal positions out for server side
     * computation.
     */
    TenBoard(TenBoardData data) {
        board = data.board;
        bigSquaresStatus = new int[9];
        for (int i = 0; i < 9; i++) {
            updateBigSquareStatus(i);
        }
        this.currentBigSquareLegalPosition = data.currentBigSquareLegalPosition;
        this.currentPlayerIdentity = data.currentPlayerIdentity;
    }
    
    /**
     * Initialize the board from an old board.
     *
     * @param oldBoard the old board.
     */
    private TenBoard(TenBoard oldBoard) {
        board = new int[9][0];
        bigSquaresStatus = new int[9];
        for (int i = 0; i < 9; i++) {
            // Copy to maintain value safety.
            board[i] = Arrays.copyOf(oldBoard.board[i], 9);
            // Just copy value to speed up without another around of
            // calculation.
            bigSquaresStatus[i] = oldBoard.bigSquaresStatus[i];
        }
        currentBigSquareLegalPosition = oldBoard.currentBigSquareLegalPosition;
        currentPlayerIdentity = oldBoard.currentPlayerIdentity;
    }
    
    @Override
    public int getCurrentPlayerIdentity() {
        return currentPlayerIdentity;
    }
    
    /**
     * Obtain the current big square legal position.
     * This method is necessary for maintaining a stateless game between server
     * and client.
     *
     * @return the current big square legal position.
     */
    int getCurrentBigSquareLegalPosition() {
        return currentBigSquareLegalPosition;
    }
    
    /**
     * Whether the board is empty.
     *
     * @return empty or not.
     */
    private boolean isEmpty() {
        for (int[] smallSquare : board) {
            for (int tile : smallSquare) {
                if (tile != 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Create a new copy of board for AI
     *
     * @return a new copy of board
     */
    @Override
    public TenBoard getCopy() {
        return new TenBoard(this);
    }
    
    /**
     * Decode int to player name.
     *
     * @param i int form.
     * @return "b", "w", or "0".
     */
    private static String decode(int i) {
        if (i == 0) {
            return "0";
        }
        return i == 1? "b": "w";
    }
    
    /**
     * Print the board
     */
    public void print() {
        System.out.println("Current Player: " +
                (currentPlayerIdentity == 1? "Black": "White"));
        System.out.println("Printing the board:");
        System.out.println("-----------------");
        for (int row = 0; row < 3; row++) {
            for (int innerRow = 0; innerRow < 3; innerRow++) {
                System.out.print(decode(board[row * 3][innerRow * 3]) + " "
                        + decode(board[row * 3][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3][innerRow * 3 + 2]) + "|");
                System.out.print(decode(board[row * 3 + 1][innerRow * 3]) + " "
                        + decode(board[row * 3 + 1][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3 + 1][innerRow * 3 + 2]) + "|");
                System.out.print(decode(board[row * 3 + 2][innerRow * 3]) + " "
                        + decode(board[row * 3 + 2][innerRow * 3 + 1]) + " "
                        + decode(board[row * 3 + 2][innerRow * 3 + 2]));
                System.out.print('\n');
            }
            if (row != 2) {
                System.out.println("- - -*- - -*- - -");
            }
        }
        System.out.println("-----------------");
    }
    
    /**
     * Check whether a move is legal, where move is given by [a, b]
     *
     * @param a a.
     * @param b b.
     * @return legality of a move.
     */
    private boolean isLegalMove(int a, int b) {
        if (a < 0 || a > 8 || b < 0 || b > 8) {
            // out of boundary values
            return false;
        }
        if (currentBigSquareLegalPosition != -1
                && currentBigSquareLegalPosition != a) {
            // in the wrong big square when it cannot have a free move
            return false;
        }
        // not in the occupied big square and on an empty tile
        return bigSquaresStatus[a] == 0 && board[a][b] == 0;
    }
    
    /**
     * Check whether a move is legal.
     *
     * @param move a move represented by [a, b].
     * @return legality of a move.
     */
    private boolean isLegalMove(int[] move) {
        return isLegalMove(move[0], move[1]);
    }
    
    @Override
    public void makeMoveWithoutCheck(int[] move) {
        board[move[0]][move[1]] = currentPlayerIdentity;
        updateBigSquareStatus(move[0]);
        if (bigSquaresStatus[move[1]] == 0) {
            currentBigSquareLegalPosition = move[1];
        } else {
            currentBigSquareLegalPosition = -1;
        }
    }
    
    /**
     * Make a move with legality check.
     *
     * @param move a move represented by [a, b].
     * @return whether the move is successful (aka legal).
     */
    boolean makeMove(int[] move) {
        if (!isLegalMove(move)) {
            return false;
        }
        makeMoveWithoutCheck(move);
        return true;
    }
    
    @Override
    public int[][] getAllLegalMovesForAI() {
        List<int[]> list = new LinkedList<>();
        int[][] template = new int[0][0];
        if (isEmpty()) {
            // for symmetry
            return INITIAL_AI_LEGAL_MOVES;
        }
        if (currentBigSquareLegalPosition == -1) {
            for (int i = 0; i < 9; i++) {
                if (bigSquaresStatus[i] == 0) {
                    for (int j = 0; j < 9; j++) {
                        if (isLegalMove(i, j)) {
                            list.add(new int[]{i, j});
                        }
                    }
                }
            }
            return list.toArray(template);
        }
        for (int j = 0; j < 9; j++) {
            if (isLegalMove(currentBigSquareLegalPosition, j)) {
                list.add(new int[]{currentBigSquareLegalPosition, j});
            }
        }
        return list.toArray(template);
    }
    
    /**
     * Perform a naive check about whether the player specified win the square.
     *
     * @param s the square to check.
     * @param id player identity.
     * @return whether the player simply and directly wins the square.
     */
    private boolean playerSimplyWinSquare(int[] s, int id) {
        return s[0] == id && s[1] == id && s[2] == id
                || s[3] == id && s[4] == id && s[5] == id
                || s[6] == id && s[7] == id && s[8] == id
                || s[0] == id && s[3] == id && s[6] == id
                || s[1] == id && s[4] == id && s[7] == id
                || s[2] == id && s[5] == id && s[8] == id
                || s[0] == id && s[4] == id && s[8] == id
                || s[2] == id && s[4] == id && s[6] == id;
    }
    
    /**
     * A function that helps to determine whether a square belongs to black or
     * white.
     * If there is no direct victory, it will return zero.
     *
     * @param square a square whose status must be determined.
     * @return status
     */
    private int getSimpleStatusFromSquare(int[] square) {
        return playerSimplyWinSquare(square, 1)? 1
                : ((playerSimplyWinSquare(square, -1))? -1: 0);
    }
    
    /**
     * update the big square status for ONE big square
     *
     * @param bigSquareID the id of the big square
     */
    private void updateBigSquareStatus(int bigSquareID) {
        int[] bigSquare = board[bigSquareID];
        int bigSquareStatus = getSimpleStatusFromSquare(bigSquare);
        if (bigSquareStatus == 1 || bigSquareStatus == -1) {
            bigSquaresStatus[bigSquareID] = bigSquareStatus;
            return;
            // already won by a player
        }
        for (int i = 0; i < 9; i++) {
            if (bigSquare[i] == 0) {
                // there is a space left.
                bigSquaresStatus[bigSquareID] = 0;
                return;
            }
        }
        bigSquaresStatus[bigSquareID] = 2; // no space left.
    }
    
    @Override
    public int getGameStatus() {
        // update the big square status for all big squares
        for (int i = 0; i < 9; i++) {
            updateBigSquareStatus(i);
        }
        int simpleStatus = getSimpleStatusFromSquare(bigSquaresStatus);
        if (simpleStatus == 1 || simpleStatus == -1) {
            return simpleStatus; // Game has been won by a player.
        }
        for (int i = 0; i < 9; i++) {
            if (bigSquaresStatus[i] == 0) {
                return 0; // The game has not finished yet.
            }
        }
        // no more legal moves
        int blackBigSquareCounter = 0;
        int whiteBigSquareCounter = 0;
        for (int i = 0; i < 9; i++) {
            int status = bigSquaresStatus[i];
            if (status == 1) {
                blackBigSquareCounter++;
            } else if (status == -1) {
                whiteBigSquareCounter++;
            }
        }
        // determine the result by counting big squares
        return (blackBigSquareCounter > whiteBigSquareCounter)? 1: -1;
    }
    
    @Override
    public void switchIdentity() {
        currentPlayerIdentity = ~currentPlayerIdentity + 1;
    }
    
}