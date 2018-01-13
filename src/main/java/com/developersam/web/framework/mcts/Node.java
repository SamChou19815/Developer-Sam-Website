package com.developersam.web.framework.mcts;

/**
 * A node in the simulation tree.
 */
final class Node {
    
    /**
     * The parent node, used to track back to update winning probability.
     */
    private final Node parent;
    /**
     * Children node, which will be initialized later.
     */
    Node[] children;
    /**
     * The move stored on the node.
     */
    private final int[] move;
    /**
     * The game board stored on the node.
     */
    private Board board;
    // stored as an array {a, b}; a/b is the actual probability
    private double[] winningProbability = new double[2];
    
    /**
     * Construct a node without a parent and without a move, with only a
     * starting board.
     * This node can only be root node.
     *
     * @param board the starting board.
     */
    Node(Board board) {
        this(null, null, board);
    }
    
    /**
     * Initialize a node with a parent node linked to it and a required move.
     *
     * @param parent a parent node.
     * @param move a move on the board.
     * @param board the current board with the {@code move}.
     */
    Node(Node parent, int[] move, Board board) {
        this.parent = parent;
        this.move = move;
        this.board = board;
    }
    
    /**
     * Get number of legal moves.
     *
     * @return number of legal moves
     */
    int getNumberOfLegalMoves() {
        return children == null? 0: children.length;
    }
    
    int[] getMove() {
        return move;
    }
    
    Board getBoard() {
        return board;
    }
    
    /**
     * Obtain winning probability in double.
     *
     * @return a number between 0 and 1
     */
    double getWinningProbability() {
        return winningProbability[0] / winningProbability[1];
    }
    
    /**
     * Obtain winning probability in percentage.
     *
     * @return a number between 0 and 99, inclusive
     */
    int getWinningProbabilityInPercentage() {
        return (int) (getWinningProbability() * 100);
    }
    
    /**
     * Plus one for winning probability numerator;
     * if win, plus additional one for denominator.
     *
     * @param winValue win --> 1, lose --> 0, draw --> a value decided by board
     */
    synchronized void winningStatisticsPlusOne(double winValue) {
        Node n = this;
        while (n != null) {
            n.winningProbability[0] += winValue;
            n.winningProbability[1] += 1;
            n = n.parent;
        }
    }
    
    /**
     * Get upper confidence bound in MCTS.
     *
     * @param isPlayer whether the selection is for or against the player
     * @return upper confidence bound
     */
    double getUpperConfidenceBound(boolean isPlayer) {
        double lnt = Math.log(parent.winningProbability[1]);
        double winningProb = isPlayer? getWinningProbability():
                (1 - getWinningProbability());
        double c = 1.0;
        return winningProb + Math.sqrt(2 * lnt / winningProbability[1]) * c;
    }
    
    /**
     * Remove the board from the node to allow it to be garbage collected.
     */
    void dereferenceBoard() {
        this.board = null;
    }
    
}