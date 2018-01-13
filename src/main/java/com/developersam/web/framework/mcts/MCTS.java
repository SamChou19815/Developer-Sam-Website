package com.developersam.web.framework.mcts;

import java.util.stream.IntStream;

/**
 * The name stands for Monte Carlo tree search.
 */
public final class MCTS {
    
    /**
     * The initial board.
     */
    private final Board board;
    /**
     * Time limit in milliseconds.
     */
    private final int timeLimitInMS;
    /**
     * Whether number of simulations should be printed.
     */
    private final boolean shouldPrintNumberOfSimulations;
    /**
     * The value of a draw of the game, should be between 0 and 1.
     */
    private final double drawValue;
    /**
     * The tree has the following structure described in the format of:
     * [node type]: [children], [move].
     * normal node: normal node, normal move.
     * last level node: empty, move.
     * root node: normal node, null.
     */
    private final Node tree;
    
    /**
     * Construct the MCTS framework by board and time limit.
     * It will not print number of simulations by default.
     *
     * @param b the initial board.
     * @param timeLimitInMS time limit in MS
     */
    public MCTS(Board b, int timeLimitInMS) {
        this(b, timeLimitInMS, false);
    }
    
    /**
     * Construct the MCTS framework.
     *
     * @param b the initial board.
     * @param timeLimitInMS time limit in MS
     * @param printNumSim whether to print number of simulations.
     */
    public MCTS(Board b, int timeLimitInMS, boolean printNumSim) {
        board = b;
        this.timeLimitInMS = timeLimitInMS;
        tree = new Node(board);
        drawValue = board.getDrawValue();
        shouldPrintNumberOfSimulations = printNumSim;
    }
    
    /**
     * Select a node starting from parent.
     *
     * @return a node to be expanded.
     */
    private Node selection() {
        Node root = tree;
        boolean isPlayer = true;
        while (root.getNumberOfLegalMoves() > 0) {
            // Find optimal move and loop down.
            Node[] children = root.children;
            int len = children.length;
            if (len == 0) {
                throw new NoLegalMoveException();
            }
            Node n = children[0];
            double max = n.getUpperConfidenceBound(isPlayer);
            for (int i = 1; i < len; i++) {
                Node node = children[i];
                double ucb = node.getUpperConfidenceBound(isPlayer);
                if (ucb > max) {
                    max = ucb;
                    n = node;
                }
            }
            isPlayer = !isPlayer; // switch player identity
            root = n;
        }
        return root;
    }
    
    /**
     * Perform simulation for a specific node.
     *
     * @param nodeToBeSimulated the node to be simulated.
     * @return win value.
     */
    private double simulation(Node nodeToBeSimulated) {
        Board boardBeforeSimulation = nodeToBeSimulated.getBoard();
        Board b1 = boardBeforeSimulation.getCopy();
        int status = b1.getGameStatus();
        while (status == 0) {
            int[][] moves = b1.getAllLegalMovesForAI();
            int[] move = moves[(int) (Math.random() * moves.length)];
            b1.makeMoveWithoutCheck(move);
            status = b1.getGameStatus();
            b1.switchIdentity();
        }
        if (status == board.getCurrentPlayerIdentity()) {
            return 1;
        } else if (status == -1 * board.getCurrentPlayerIdentity()) {
            return 0;
        } else {
            // draw
            return drawValue;
        }
    }
    
    /**
     * A method that connected all parts of of MCTS to build an evaluation tree.
     */
    private void think() {
        long tStart = System.currentTimeMillis();
        int simulationCounter = 0;
        while (System.currentTimeMillis() - tStart < timeLimitInMS) {
            Node selectedNode = selection();
            Board b = selectedNode.getBoard();
            // Expansion: Get all legal moves from a current board
            int[][] allLegalMoves = b.getAllLegalMovesForAI();
            int len = allLegalMoves.length;
            if (len > 0) {
                // board no longer needed at parent level.
                selectedNode.dereferenceBoard();
            }
            Node[] newNodes = new Node[len];
            IntStream.range(0, len)
                    .parallel()
                    .unordered()
                    .forEach(i -> {
                        // Simulation Setup
                        int[] move = allLegalMoves[i];
                        Board b1 = b.getCopy();
                        b1.makeMoveWithoutCheck(move);
                        b1.switchIdentity();
                        Node n = newNodes[i] = new Node(selectedNode, move, b1);
                        // Simulate and back propagate.
                        n.winningStatisticsPlusOne(simulation(n));
                    });
            selectedNode.children = newNodes;
            simulationCounter += selectedNode.children.length;
        }
        if (shouldPrintNumberOfSimulations) {
            System.out.println("# of simulations: " + simulationCounter);
        }
    }
    
    /**
     * Give the final move chosen by AI.
     *
     * @return [...decided move, winning probability percentage by that move].
     */
    public int[] selectMove() {
        think();
        Node[] children = tree.children;
        int len = children.length;
        if (len == 0) {
            throw new NoLegalMoveException();
        }
        Node nodeToBeReturned = children[0];
        double max = nodeToBeReturned.getWinningProbability();
        for (int i = 1; i < len; i++) {
            Node n = children[i];
            double value = n.getWinningProbability();
            if (value > max) {
                max = value;
                nodeToBeReturned = n;
            }
        }
        int[] move = nodeToBeReturned.getMove();
        int winningProbPercentage =
                nodeToBeReturned.getWinningProbabilityInPercentage();
        int[] moveWithWinningProb = new int[move.length + 1];
        System.arraycopy(move, 0,
                moveWithWinningProb, 0, move.length);
        moveWithWinningProb[move.length] = winningProbPercentage;
        return moveWithWinningProb;
    }
    
}