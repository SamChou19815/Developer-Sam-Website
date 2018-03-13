package com.developersam.ten

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
        TenBoard.runAGameBetweenTwoAIs(
                aiThinkingTime = 100, printGameStatus = false)
    }

}