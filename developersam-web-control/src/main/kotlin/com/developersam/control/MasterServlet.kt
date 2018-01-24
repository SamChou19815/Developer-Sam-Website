package com.developersam.control

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * [MasterServlet] is the entry point of the web application, which is used as
 * a dispatcher servlet.
 * It should be overridden to provide a [serviceRunner] to handle the request
 * under different circumstances.
 */
abstract class MasterServlet : HttpServlet() {

    /**
     * The service runner registered with all the services supplied by the
     * user.
     */
    abstract val serviceRunner: ServiceRunner

    /**
     * A helper method to add CORS header for [resp] for a given [req].
     */
    private fun addCORSHeader(req: HttpServletRequest,
                              resp: HttpServletResponse) {
        val origin: String? = req.getHeader("origin")
        val allowedOrigin: String
        allowedOrigin = if (origin == "https://www.developersam.com"
                || origin == "https://developersam.com"
                || origin == "http://localhost:4200") {
            origin;
        } else {
            "https://www.developersam.com"
        }
        resp.addHeader("Access-Control-Allow-Origin", allowedOrigin)
        resp.addHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization, X-Requested-With")
        resp.addHeader("Access-Control-Allow-Methods",
                "GET, POST, DELETE, OPTIONS")
        resp.addHeader("Access-Control-Allow-Credentials", "true")
        resp.addHeader("Vary", "Origin")
    }

    final override fun doGet(req: HttpServletRequest,
                             resp: HttpServletResponse) {
        serviceRunner.serve(req = req, resp = resp)
        addCORSHeader(req = req, resp = resp)
    }

    final override fun doPost(req: HttpServletRequest,
                              resp: HttpServletResponse) {
        serviceRunner.serve(req = req, resp = resp)
        addCORSHeader(req = req, resp = resp)
    }

    final override fun doDelete(req: HttpServletRequest,
                                resp: HttpServletResponse) {
        serviceRunner.serve(req = req, resp = resp)
        addCORSHeader(req = req, resp = resp)
    }

    final override fun doOptions(req: HttpServletRequest,
                                 resp: HttpServletResponse) {
        addCORSHeader(req = req, resp = resp)
    }

}