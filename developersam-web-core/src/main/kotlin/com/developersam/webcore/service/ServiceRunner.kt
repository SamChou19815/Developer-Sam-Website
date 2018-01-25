package com.developersam.webcore.service

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * The central class used to register services and run them when needed.
 * It is constructed from an array of [Service] object.
 * Once constructed, no more services can be added.
 */
class ServiceRunner(services: Array<Service>) {

    /**
     * The table that records all the registered services.
     */
    private val serviceTable: Table<HttpMethod, String, Service> =
            HashBasedTable.create()

    init {
        for (service in services) {
            this.serviceTable.put(service.method, service.uri, service)
        }
    }

    /**
     * Serve a specific [req] and write the response to a given [resp].
     */
    internal fun serve(req: HttpServletRequest, resp: HttpServletResponse) {
        // Check validity of method
        val method: HttpMethod
        try {
            method = HttpMethod.valueOf(value = req.method)
        } catch (e: IllegalArgumentException) {
            resp.status = 404
            return
        }
        // Check validity of method and URI
        val uri: String = req.requestURI
        val service: Service? = serviceTable[method, uri]
        if (service == null) {
            resp.status = 404
            return
        }
        // Ready to serve
        service.serve(req = req, resp = resp)
    }

}