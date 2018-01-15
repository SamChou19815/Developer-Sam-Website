package com.developersam.model.ten

import com.developersam.testcommon.NUM_TESTS
import org.junit.Assert.*
import org.junit.Test

class TenBoardTest {

    @Test
    fun legalityTest() {
        for (i in 0 until NUM_TESTS) {
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