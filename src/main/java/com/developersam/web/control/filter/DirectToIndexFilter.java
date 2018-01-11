package com.developersam.web.control.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "DirectToIndexFilter")
public class DirectToIndexFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) { }
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain)
            throws ServletException, IOException {
        req.getRequestDispatcher("index.html").forward(req, resp);
    }
    
    @Override
    public void destroy() {}
    
}
