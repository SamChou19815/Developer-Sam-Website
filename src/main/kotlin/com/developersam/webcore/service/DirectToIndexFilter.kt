package com.developersam.webcore.service

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * A list of URIs to be redirected.
 */
private val redirectedURIs = arrayOf(
        "/projects",
        "/ten",
        "/scheduler",
        "/chunkreader"
)

/**
 * A filter that works with Angular to redirect some sources to index.html
 * so that Angular's routing system can be properly used.
 */
class DirectToIndexFilter : Filter {

    override fun init(filterConfig: FilterConfig) {}

    override fun doFilter(req: ServletRequest, resp: ServletResponse,
                          chain: FilterChain) {
        if ((req as HttpServletRequest).requestURI in redirectedURIs) {
            req.getRequestDispatcher("/index.html").forward(req, resp)
        } else {
            chain.doFilter(req, resp)
        }
    }

    override fun destroy() {}

}