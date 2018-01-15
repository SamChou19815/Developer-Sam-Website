package com.developersam.util.concurrency

import com.google.appengine.api.ThreadManager
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.collections.ArrayList

/**
 * The [BlockingCompletionService] class is responsible for executing all
 * the given [tasks] in parallel, blocking the thread until all the results are
 * back and return the result.
 */
class BlockingCompletionService<out V>(
        private val tasks: Collection<Future<V>>
) {

    /**
     * Block the thread until all the results are back and returns the results
     * in a [List].
     */
    val executionResults: List<V>?
        get() {
            return try {
                val len = tasks.size
                val executorService = Executors.newFixedThreadPool(len,
                        ThreadManager.currentRequestThreadFactory())
                val completionService =
                        ExecutorCompletionService<V>(executorService)
                val list = ArrayList<V>(len)
                for (i in 0 until len) {
                    list.add(completionService.take().get())
                }
                list
            } catch (e: ExecutionException) {
                null
            } catch (e: InterruptedException) {
                null
            }
        }

}