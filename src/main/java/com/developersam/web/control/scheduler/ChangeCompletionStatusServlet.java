package com.developersam.web.control.scheduler;

import com.developersam.web.model.scheduler.Scheduler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that delete an scheduler item.
 */
@WebServlet(name = "ChangeCompletionStatusServlet",
        value = "/apps/scheduler/changeCompletionStatus")
public class ChangeCompletionStatusServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        Scheduler scheduler = new Scheduler();
        String key = req.getParameter("key");
        boolean completed = Boolean.parseBoolean(
                req.getParameter("completed"));
        scheduler.changeCompletionStatus(key, completed);
    }
    
}