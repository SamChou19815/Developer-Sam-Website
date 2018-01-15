package com.developersam.framework.mcts

import java.util.logging.Logger
import java.util.stream.IntStream

/**
 * [MCTS] stands for Monte Carlo tree search.
 * It's constructed with a initial [board] and the [timeLimit] in milliseconds.
 */
class MCTS(private val board: Board, private val timeLimit: Int) {
    /**
     * The value of a draw of the game, should be between 0 and 1.
     */
    private val drawValue: Double = board.drawValue
    /**
     * The tree has the following structure described in the format of:
     * (node type): (children), (move).
     * - normal node: normal node, normal move.
     * - last level node: empty, move.
     * - root node: normal node, null.
     */
    private val tree: Node = Node(board)

    /**
     * Select and return a node starting from parent, according to selection
     * rule in MCTS.
     */
    private fun selection(): Node {
        var root = tree
        var isPlayer = true
        while (root.numberOfLegalMoves > 0) {
            // Find optimal move and loop down.
            val children = root.children
            val len = children?.size ?: throw NoLegalMoveException()
            var n = children[0]
            var max = n.getUpperConfidenceBound(isPlayer)
            for (i in 1 until len) {
                val node = children[i]
                val ucb = node.getUpperConfidenceBound(isPlayer)
                if (ucb > max) {
                    max = ucb
                    n = node
                }
            }
            isPlayer = !isPlayer // switch player identity
            root = n
        }
        return root
    }

    /**
     * Perform simulation for a specific node [nodeToBeSimulated] and gives back
     * a win value between 0 and 1.
     */
    private fun simulation(nodeToBeSimulated: Node): Double {
        val boardBeforeSimulation = nodeToBeSimulated.board
        val b1 = boardBeforeSimulation!!.copy
        var status = b1.gameStatus
        while (status == 0) {
            val moves = b1.allLegalMovesForAI
            val move = moves[(Math.random() * moves.size).toInt()]
            b1.makeMoveWithoutCheck(move)
            status = b1.gameStatus
            b1.switchIdentity()
        }
        return when (status) {
            board.currentPlayerIdentity -> 1.0
            -1 * board.currentPlayerIdentity -> 0.0
            else -> drawValue
        }
    }

    /**
     * A method that connected all parts of of MCTS to build an evaluation tree.
     */
    private fun think() {
        val tStart = System.currentTimeMillis()
        var simulationCounter = 0
        while (System.currentTimeMillis() - tStart < timeLimit) {
            val selectedNode = selection()
            val b = selectedNode.board
            // Expansion: Get all legal moves from a current board
            val allLegalMoves = b!!.allLegalMovesForAI
            val len = allLegalMoves.size
            if (len > 0) {
                // board no longer needed at parent level.
                selectedNode.dereferenceBoard()
            }
            selectedNode.children = IntStream.range(0, len)
                    .parallel()
                    .unordered()
                    .mapToObj {
                        // Simulation Setup
                        val move = allLegalMoves[it]
                        val b1 = b.copy
                        b1.makeMoveWithoutCheck(move)
                        b1.switchIdentity()
                        val n = Node(selectedNode, move, b1)
                        // Simulate and back propagate.
                        val winValue = simulation(n)
                        synchronized(tree) {
                            n.winningStatisticsPlusOne(winValue)
                        }
                        n
                    }.toArray({ size -> arrayOfNulls<Node>(size) })
            simulationCounter += len
        }
        Logger.getGlobal().info("# of simulations: " + simulationCounter)
    }

    /**
     * Give the final move chosen by AI with the format
     * (...decided move, winning probability percentage by that move).
     */
    fun selectMove(): IntArray {
        think()
        val children: Array<Node>? = tree.children
        val len = children?.size ?: throw NoLegalMoveException()
        var nodeChosen: Node = children[0]
        var maxWinningProbability: Double = nodeChosen.winningProbability
        for (i in 1 until len) {
            val n = children[i]
            val value = n.winningProbability
            if (value > maxWinningProbability) {
                maxWinningProbability = value
                nodeChosen = n
            }
        }
        val move: IntArray = nodeChosen.move ?: throw NoLegalMoveException()
        val winningProbPercentage: Int =
                nodeChosen.winningProbabilityInPercentage
        val moveWithWinningProb = IntArray(move.size + 1)
        System.arraycopy(move, 0,
                moveWithWinningProb, 0, move.size)
        moveWithWinningProb[move.size] = winningProbPercentage
        return moveWithWinningProb
    }

}

/**
 * A node in the simulation tree.
 *
 * It is initialized by an optional parent [parent] (which is used to track back
 * to update winning probability), a move [move] of the user or AI, the board
 * [board] bind on the node.
 */
private class Node(private val parent: Node?, internal val move: IntArray?,
                   board: Board) {
    /**
     * Children node, which will be initialized later.
     */
    internal var children: Array<Node>? = null
    /**
     * The game board stored on the node.
     * It is the backing field of the property.
     */
    private var _board: Board? = null
    /**
     * Stored as an array {a, b}; a/b is the actual probability
     */
    private val winningProbArray = DoubleArray(2)

    /**
     * Obtain the [board] associated with the node.
     */
    internal val board: Board? get() = _board

    /**
     * Get number of legal moves.
     *
     * @return number of legal moves
     */
    internal val numberOfLegalMoves: Int get() = children?.size ?: 0

    /**
     * Obtain winning probability in percentage.
     */
    internal val winningProbabilityInPercentage: Int
        get() = (winningProbability * 100).toInt()

    /**
     * Construct a node without a parent and without a move, with only a
     * starting board.
     * This node can only be root node.
     *
     * @param board the starting board.
     */
    internal constructor(board: Board) : this(null, null, board)

    init {
        this._board = board
    }

    /**
     * Obtain [winningProbability] between 0 and 1.
     */
    internal val winningProbability: Double
        get() = winningProbArray[0] / winningProbArray[1]

    /**
     * Plus one for winning probability denominator and plus the [winValue] for
     * the numerator. This method does this iteratively until reaching the root.
     */
    internal fun winningStatisticsPlusOne(winValue: Double) {
        var n: Node? = this
        while (n != null) {
            n.winningProbArray[0] += winValue
            n.winningProbArray[1] += 1.0
            n = n.parent
        }
    }

    /**
     * Get upper confidence bound in MCTS, which needs a [isPlayer] parameter
     * to tell whether to calculate in favor or against the player.
     */
    internal fun getUpperConfidenceBound(isPlayer: Boolean): Double {
        val lnt = Math.log(parent!!.winningProbArray[1])
        val winningProb =
                if (isPlayer) winningProbability else 1 - winningProbability
        val c = 1.0
        return winningProb + Math.sqrt(2 * lnt / winningProbArray[1]) * c
    }

    /**
     * Remove the board from the node to allow it to be garbage collected.
     */
    internal fun dereferenceBoard() {
        this._board = null
    }

}