package com.developersam.mcts;

import java.util.Arrays;
import java.util.Comparator;

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
     * <node type>: <children>, <move>.
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
            boolean finalIsPlayer = isPlayer;
            // Find optimal move and loop down.
            root = Arrays.stream(root.children)
                    .parallel()
                    .unordered()
                    .max(Comparator.comparingDouble(
                            o -> o.getUpperConfidenceBound(finalIsPlayer)))
                    .orElseThrow(NoLegalMoveException::new);
            isPlayer = !isPlayer; // switch player identity
        }
        return root;
    }
    
    /**
     * Expand the selected move with all possible legal moves.
     */
    private void expansion(Node nodeToBeExpanded) {
        Board b = nodeToBeExpanded.getBoard();
        // Get all legal moves from a current board
        int[][] allLegalMoves = b.getAllLegalMovesForAI();
        if (allLegalMoves.length > 0) {
            // board no longer needed at parent level.
            nodeToBeExpanded.deferenceBoard();
        }
        nodeToBeExpanded.children = Arrays.stream(allLegalMoves)
                .parallel()
                .unordered()
                .map(move -> {
                    // Add a child for each possible move,
                    // copying the board to wait for later simulation.
                    Board b1 = b.getCopy();
                    b1.makeMoveWithoutCheck(move);
                    b1.switchIdentity();
                    return new Node(nodeToBeExpanded, move, b1);
                }).toArray(Node[]::new);
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
            if (moves.length == 0) {
                throw new NoLegalMoveException();
            }
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
            expansion(selectedNode);
            Arrays.stream(selectedNode.children).parallel().unordered()
                    .forEach(n -> n.winningStatisticsPlusOne(simulation(n)));
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
        if (tree.children.length == 0) {
            throw new NoLegalMoveException();
        }
        Node nodeToBeReturned = Arrays.stream(tree.children)
                .parallel()
                .unordered()
                .max(Comparator.comparingDouble(Node::getWinningProbability))
                .orElseThrow(NoLegalMoveException::new);
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