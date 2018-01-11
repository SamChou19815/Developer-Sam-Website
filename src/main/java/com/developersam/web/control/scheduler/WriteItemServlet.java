package com.developersam.web.control.scheduler;

import com.developersam.web.model.scheduler.SchedulerItemData;
import com.developersam.web.util.GsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "WriteItemServlet", value = "/apis/scheduler/write")
public class WriteItemServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        SchedulerItemData data = GsonUtil.GSON.fromJson(
                req.getReader(), SchedulerItemData.class);
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(data.writeToDatabase());
    }
    
}
