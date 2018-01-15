package com.developersam.controller.filter

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.annotation.WebFilter

/**
 * A filter that works with Angular to redirect some sources to index.html
 * so that Angular's routing system can be properly used.
 */
@WebFilter(filterName = "DirectToIndexFilter")
class DirectToIndexFilter : Filter {

    override fun init(filterConfig: FilterConfig) {}

    override fun doFilter(req: ServletRequest, resp: ServletResponse,
                          chain: FilterChain) {
        req.getRequestDispatcher("index.html").forward(req, resp)
    }

    override fun destroy() {}

}
