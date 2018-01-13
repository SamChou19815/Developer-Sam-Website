package com.developersam.web.control.ten;

import com.developersam.web.model.ten.TenClientMove;
import com.developersam.web.model.ten.Controller;
import com.developersam.web.model.ten.TenServerResponse;
import com.developersam.web.util.GsonUtil;
import com.google.gson.Gson;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet that returns AI's response to human's move in board game TEN.
 */
@WebServlet(name = "GameResponseServlet", value = "/apis/ten/response")
public class GameResponseServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Gson gson = GsonUtil.GSON;
        TenClientMove clientMove =
                gson.fromJson(req.getReader(), TenClientMove.class);
        TenServerResponse response = Controller.respond(clientMove);
        gson.toJson(response, resp.getWriter());
        /*
        String responseMove = String.valueOf(move[0]) + "," +
                String.valueOf(move[1]) + "," + String.valueOf(move[2])
                + "," + String.valueOf(move[3]) + "," + String.valueOf(move[4]);
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().print(responseMove);
        resp.setCharacterEncoding("UTF-8");
        */
    }
    
}
