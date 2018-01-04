package com.developersam.web.control.scheduler;

import com.developersam.web.model.scheduler.SchedulerUsers;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that sends daily report for scheduler users who signed for
 * email notification.
 */
@WebServlet(name = "SendDailyReportServlet",
        value = "/apps/scheduler/sendDailyReport")
public class SendDailyReportServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
        SchedulerUsers.sendEmailNotifications();
    }
    
}