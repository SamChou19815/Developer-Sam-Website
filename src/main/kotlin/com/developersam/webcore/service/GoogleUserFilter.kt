package com.developersam.webcore.service

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * A filter that extracts a google user from the request.
 */
class GoogleUserFilter : Filter {

    override fun init(filterConfig: FilterConfig?) {}

    override fun doFilter(request: ServletRequest, response: ServletResponse,
                          chain: FilterChain) {
        try {
            val req = request as HttpServletRequest
            val token: String? = req.getParameter("token")
            GoogleUserService.currentUser =
                    FirebaseService.getUser(idToken = token)
            chain.doFilter(request, response)
        } finally {
            GoogleUserService.reset()
        }
    }

    override fun destroy() {}

}