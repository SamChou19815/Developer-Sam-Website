package com.developersam.web.control.scheduler;

import com.developersam.web.model.scheduler.Scheduler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that delete an scheduler item.
 */
@WebServlet(name = "DeleteItemServlet", value = "/apis/scheduler/delete")
public class DeleteItemServlet extends HttpServlet {
    
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        Scheduler.INSTANCE.delete(req.getParameter("key"));
    }
    
}