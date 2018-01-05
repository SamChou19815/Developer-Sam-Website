package com.developersam.web.control.scheduler;

import com.developersam.web.model.scheduler.Scheduler;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet that add a new item to the database.
 */
@WebServlet(name = "AddItemServlet", value = "/apps/scheduler/add")
public class AddItemServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String description = req.getParameter("description");
        String deadline = req.getParameter("deadline");
        boolean success = Scheduler.INSTANCE.addItem(description, deadline);
        resp.getWriter().print(success);
        resp.setCharacterEncoding("UTF-8");
    }
    
}