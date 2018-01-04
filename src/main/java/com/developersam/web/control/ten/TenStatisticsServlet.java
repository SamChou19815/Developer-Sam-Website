package com.developersam.web.control.ten;

import com.developersam.web.model.statistics.UserStatistics;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet that returns the statistics of the game.
 */
@WebServlet(name = "TenStatisticsServlet", value = "/apps/ten/statistics")
public class TenStatisticsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().print(new UserStatistics("ten").getTotalUsage());
        resp.setCharacterEncoding("UTF-8");
    }
    
}
