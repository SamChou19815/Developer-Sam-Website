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

    final override fun doGet(req: HttpServletRequest,
                             resp: HttpServletResponse) {
        serviceRunner.serve(req = req, resp = resp)
    }

    final override fun doPost(req: HttpServletRequest,
                              resp: HttpServletResponse) {
        serviceRunner.serve(req = req, resp = resp)
    }

    final override fun doDelete(req: HttpServletRequest,
                                resp: HttpServletResponse) {
        serviceRunner.serve(req = req, resp = resp)
    }

}