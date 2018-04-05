package com.developersam.util

import com.developersam.generateRandomString
import org.junit.Before
import org.junit.Test

import java.util.ArrayList

import org.junit.Assert.*

class TrieTest {

    /**
     * Testing provided examples.
     */
    private lateinit var officialTrie: Trie
    /**
     * Testing with random examples.
     */
    private lateinit var randomTrie: Trie
    /**
     * Lists of random strings to be tested on the trie.
     */
    private lateinit var listOfRandomStrings: MutableList<String>

    /**
     * Initialize the [officialTrie] and [randomTrie] for different testings.
     * It also tests the insert method for trie.
     */
    @Before
    fun initialize() {
        // Official data trie construction.
        officialTrie = Trie()
        officialTrie.insert("COW")
        officialTrie.insert("CS2112")
        officialTrie.insert("CS2110")
        officialTrie.insert("CS")
        // Random data trie construction.
        randomTrie = Trie()
        listOfRandomStrings = ArrayList(NUM_TEST)
        repeat(times = NUM_TEST) {
            val randomString = generateRandomString()
            randomTrie.insert(randomString)
            listOfRandomStrings.add(randomString)
        }
    }

    @Test
    fun insert() {
        // Insert random stuff.
        repeat(times = NUM_TEST) {
            randomTrie.insert(generateRandomString())
        }
    }

    @Test
    fun delete() {
        // Test with random deleting
        repeat(times = NUM_TEST) {
            randomTrie.delete(generateRandomString())
        }
        // Try to delete all the original strings
        for (randomString in listOfRandomStrings) {
            randomTrie.delete(randomString)
        }
    }

    @Test
    fun contains() {
        // contains official data
        officialTrie.insert("")
        assertTrue("Wrong with empty string (not-exist)",
                "" in officialTrie)
        officialTrie.delete("")
        assertFalse("Wrong with empty string (exist)",
                "" in officialTrie)
        assertTrue("'COW' not found",
                "COW" in officialTrie)
        assertTrue("'CS2112' not found",
                "CS2112" in officialTrie)
        assertTrue("'CS2110' not found",
                "CS2110" in officialTrie)
        assertTrue("'CS' not found",
                "CS" in officialTrie)
        // random testing
        var containsAllRandomlyGeneratedString = true
        for (randomString in listOfRandomStrings) {
            if (randomString !in randomTrie) {
                containsAllRandomlyGeneratedString = false
                break
            }
        }
        assertTrue("Missing some values", containsAllRandomlyGeneratedString)
    }

    /**
     * Test whether closestWordToPrefix() method works as specified.
     */
    @Test
    fun closestWordToPrefix() {
        // Test with official data.
        assertEquals("Wrong with 'CS2113'",
                officialTrie.closestWordToPrefix("CS2113"), null)
        assertEquals("Wrong with 'C'",
                officialTrie.closestWordToPrefix("C"), "CS")
        assertEquals("Wrong with 'CS'",
                officialTrie.closestWordToPrefix("CS"), "CS")
        assertEquals("Wrong with 'CO'",
                officialTrie.closestWordToPrefix("CO"), "COW")
        assertEquals("Wrong with 'COW'",
                officialTrie.closestWordToPrefix("COW"), "COW")
        val cs211PrefixClosestWord =
                officialTrie.closestWordToPrefix("CS211")
        assertTrue("Wrong with 'CS211'",
                cs211PrefixClosestWord == "CS2112" || cs211PrefixClosestWord == "CS2110")
        val cannotBeFound = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        // very unlikely to appear.
        assertEquals("Wrong with not found",
                randomTrie.closestWordToPrefix(cannotBeFound), null)
        // Test with random data.
        for (randomString in listOfRandomStrings) {
            assertEquals("Wrong with itself",
                    randomTrie.closestWordToPrefix(randomString), randomString)
            val randomLengthOfPrefix = (Math.random() * randomString.length).toInt()
            val randomPrefix = randomString.substring(0, randomLengthOfPrefix)
            assertEquals("Wrong with prefix",
                    randomTrie.closestWordToPrefix(randomPrefix)
                            ?.substring(0, randomLengthOfPrefix), randomPrefix)
        }
    }

    companion object {
        /**
         * Number of tests to run.
         */
        private const val NUM_TEST = 10000
    }

}