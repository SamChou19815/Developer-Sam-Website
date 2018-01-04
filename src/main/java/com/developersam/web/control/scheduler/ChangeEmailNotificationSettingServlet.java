package com.developersam.web.control.scheduler;

import com.developersam.web.model.scheduler.SchedulerUser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that delete an scheduler item.
 */
@WebServlet(name = "ChangeEmailNotificationSettingServlet",
        value = "/apps/scheduler/changeEmailNotificationSetting")
public class ChangeEmailNotificationSettingServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String enabledStr = req.getParameter("emailNotificationEnabled");
        boolean enabled = enabledStr.equals("true");
        SchedulerUser schedulerUser = new SchedulerUser();
        schedulerUser.setEmailNotificationEnabled(enabled);
    }
    
}