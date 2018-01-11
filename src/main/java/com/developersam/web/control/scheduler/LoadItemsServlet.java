package com.developersam.web.control.scheduler;

import com.developersam.web.model.scheduler.Scheduler;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.developersam.web.util.GsonUtil.GSON;

/**
 * A servlet that loads list of scheduler items onto /apps/scheduler/.
 */
@WebServlet(name = "LoadItemsServlet", value = "/apis/scheduler/load")
public class LoadItemsServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        if (userService.isUserLoggedIn()) {
            GSON.toJson(Scheduler.INSTANCE.getAllSchedulerItems(),
                    resp.getWriter());
        } else {
            resp.getWriter().print("url: " +
                    userService.createLoginURL("/scheduler"));
        }
    }
    
}