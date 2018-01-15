package com.developersam.model.ten

import com.developersam.framework.mcts.MCTS
import org.junit.Test

/**
 * Run a performance test on TEN.
 */
class TenPerformanceTest {

    /**
     * Test the performance of the game by run a game between two MCTS AI.
     */
    @Test
    fun testPerformance() {
        TenBoard.runAGameBetweenTwoAIs(aiMoveSupplier = {
            board -> MCTS(board = board, timeLimit = 1500).selectMove()
        }, printGameStatus = false)
    }

}