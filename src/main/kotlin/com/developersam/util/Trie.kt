package com.developersam.util

import java.util.LinkedList
import java.util.Queue

/**
 * [Trie] enables fast word look-up given the first few characters of a word.
 * It is a mutable data structure.
 */
class Trie {

    /*
     * Class Invariant: No node can contain an empty map.
     */

    /**
     * [Node] stores the characters of strings in a tree structure.
     */
    private class Node(val charStored: Char, val parent: Node?) {
        /**
         * [map] is a mapping from a character to another [Trie].
         */
        val map: MutableMap<Char, Node> = hashMapOf()

        /**
         * Whether traversing down to this node will get a stored string.
         */
        var hasString: Boolean = false

        /**
         * Report whether the current node has no children, which is a sign that the node can be
         * removed safely.
         */
        val hasNoChildren get() = map.isEmpty()

        companion object {
            /**
             * [createRootFromString] creates a new instance of [Node] in [Trie]
             * that contains only the string [element]. The creation may fail
             * and return `null` if the given [element] is empty.
             */
            fun createRootFromString(element: String): Node? {
                if (element.isEmpty()) {
                    return null
                }
                val chars = element.toCharArray()
                val root = Node(charStored = '\u0000', parent = null)
                var node = root
                for (i in 0 until chars.size) {
                    val c = chars[i]
                    val newTrie = Node(charStored = c, parent = node)
                    node.map[c] = newTrie
                    node = newTrie
                }
                node.hasString = true
                return root
            }
        }
    }

    /**
     * The root of the [Trie]. It is initialized to `null` to represent the
     * empty [Trie].
     */
    private var root: Node? = null

    /**
     * [insert] will insert the given [element] to the [Trie]. If the given
     * [element] is already in the [Trie], no visible side-effect will occur.
     */
    fun insert(element: String) {
        if (root == null) {
            root = Node.createRootFromString(element = element)
            return
        }
        var node = root!!
        val chars = element.toCharArray()
        for (c in chars) {
            val trie = node.map[c]
            node = if (trie == null) {
                // Create node if not exist previously
                val newTrie = Node(charStored = c, parent = node)
                node.map[c] = newTrie
                newTrie
            } else {
                trie
            }
        }
        // At the end of traversal
        node.hasString = true
    }

    /**
     * [delete] will delete the given [element] from the [Trie]. If the given [element] is not in
     * the [Trie], no visible side-effect will occur.
     */
    fun delete(element: String) {
        var node = root ?: return
        val chars = element.toCharArray()
        for (c in chars) {
            // `null` means non-existence.
            val trie = node.map[c] ?: return
            node = trie
        }
        // At the end of traversal
        node.hasString = false
        // Optimize by deleting unused node
        while (node.hasNoChildren) {
            val nodeChar = node.charStored
            val nodeParent = node.parent
            if (nodeParent == null) {
                // At root, make root empty again
                root = null
                return
            }
            // Remove that empty child to optimize for memory
            val removed = nodeParent.map.remove(key = nodeChar, value = node)
            require(removed)
            node = nodeParent
        }
    }

    /**
     * [contains] reports whether the given [element] is in the [Trie].
     */
    operator fun contains(element: String): Boolean {
        var node = root ?: return false
        val chars = element.toCharArray()
        for (c in chars) {
            // `null` means non-existence.
            val trie = node.map[c] ?: return false
            node = trie
        }
        // At the end of traversal, simply check whether the string is stored.
        return node.hasString
    }

    /**
     * [closestWordToPrefix] returns a word contained in the trie of minimal length with the given
     * [prefix], which may or may not exist.
     */
    fun closestWordToPrefix(prefix: String): String? {
        var node = root ?: return null
        val chars = prefix.toCharArray()
        for (c in chars) {
            // `null` means non-existence.
            val trie = node.map[c] ?: return null
            node = trie
        }
        // Start to do BFS
        val bfsQueue: Queue<Node> = LinkedList()
        bfsQueue.add(element = node)
        while (bfsQueue.isNotEmpty()) {
            val n: Node = bfsQueue.poll()
            if (n.hasString) {
                // Back propagation
                val sb = StringBuilder()
                var backNode = n
                while (true) {
                    val parent = backNode.parent ?: return sb.reverse().toString()
                    sb.append(backNode.charStored)
                    backNode = parent
                }
            }
            for ((_, childNode) in n.map) {
                // Continue to search children
                bfsQueue.add(element = childNode)
            }
        }
        return null
    }

}