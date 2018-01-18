package com.developersam.control

import com.google.gson.Gson
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * The [Service] sealed class defines a set of all supported type of classes.
 * It specifies how application should work to handle user requests.
 * Note that this class does not handle the authentication at all, neither does
 * its direct subclasses. Since the authentication system varies a lot, it
 * should better be handled by a specialized system.
 */
sealed class Service {

    /**
     * The [Gson] used by all the subclasses.
     */
    protected abstract val gson: Gson
    /**
     * The [uri] that can be handled by the service.
     */
    abstract val uri: String
    /**
     * The method that can be handled by the service.
     */
    abstract val method: HttpMethod

    /**
     * To serve a user with a specified [HttpServletRequest] and write the
     * response to a [HttpServletResponse].
     * Subclass should put an output attribute to [req], call this method
     * as the last statement, and then declare the method as final.
     */
    internal open fun serve(req: HttpServletRequest,
                            resp: HttpServletResponse) {
        resp.characterEncoding = "UTF-8" // For convenience
        /**
         * If the output is string, then print; else, treat as JSON
         */
        val output: Any? = req.getAttribute("output")
        if (output is String) {
            resp.contentType = "text/plain"
            print("hey")
            resp.writer.print(output)
        } else {
            resp.contentType = "application/json"
            gson.toJson(output, resp.writer)
        }
    }

}

/**
 * The service that does not involve any user given input.
 * It only supports the HTTP GET method.
 */
abstract class NoArgService protected constructor() : Service() {

    /**
     * Only GET is supported to comply with HTTP convention.
     */
    final override val method = HttpMethod.GET

    /**
     * Compute the output from nothing.
     */
    protected abstract val output: Any?

    final override fun serve(req: HttpServletRequest,
                             resp: HttpServletResponse) {
        req.setAttribute("output", output)
        super.serve(req = req, resp = resp)
    }

}

/**
 * A service that needs only one argument from the request parameter to give
 * some output, which is given by [parameterName].
 * It only supports the HTTP GET method.
 */
abstract class OneArgService protected constructor(
        private val parameterName: String
) : Service() {

    /**
     * Only GET is supported to comply with HTTP convention.
     */
    final override val method = HttpMethod.GET

    /**
     * Compute the output from one [String] [argument].
     */
    protected abstract fun output(argument: String): Any?

    final override fun serve(req: HttpServletRequest,
                             resp: HttpServletResponse) {
        val argument: String = req.getParameter(parameterName) ?: return
        req.setAttribute("output", output(argument = argument))
        super.serve(req, resp)
    }

}

/**
 * A service that needs only two arguments from the request parameter to give
 * some output, which is given by [parameter1Name] and [parameter2Name].
 */
abstract class TwoArgsService protected constructor(
        private val parameter1Name: String,
        private val parameter2Name: String
) : Service() {

    /**
     * Compute the output from two [String] arguments
     */
    protected abstract fun output(argument1: String, argument2: String): Any?

    final override fun serve(req: HttpServletRequest,
                             resp: HttpServletResponse) {
        val argument1: String = req.getParameter(parameter1Name) ?: return
        val argument2: String = req.getParameter(parameter2Name) ?: return
        val out = output(argument1 = argument1, argument2 = argument2)
        req.setAttribute("output", out)
        super.serve(req, resp)
    }

}

/**
 * The service that needs a structured user input to give some output.
 * The type of the user input should be specified by [inType] for
 * deserialization.
 */
abstract class StructuredInputService<in I> protected constructor(
        private val inType: Class<I>
) : Service() {

    protected abstract fun output(input: I): Any?

    final override fun serve(req: HttpServletRequest,
                             resp: HttpServletResponse) {
        val input: I = gson.fromJson(req.reader, inType)
        req.setAttribute("output", output(input = input))
        super.serve(req, resp)
    }

}
