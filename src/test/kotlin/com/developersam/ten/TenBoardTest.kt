package com.developersam.ten

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Run tests on the TEN board to test its legality.
 */
class TenBoardTest {

    @Test
    fun legalityTest() {
        for (i in 0 until (1 shl 16)) {
            val board = TenBoard()
            while (board.gameStatus == 0) {
                val legalMoves = board.allLegalMovesForAI
                assertTrue(legalMoves.isNotEmpty())
                val randomIndex = (Math.random() * legalMoves.size).toInt()
                board.makeMove(legalMoves[randomIndex])
            }
        }
    }

}