package com.developersam.web.control.ten;

import com.developersam.ten.Controller;

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
        String clientInfo = req.getParameter("clientInfo");
        int[] move = Controller.respondToHumanMove(clientInfo);
        String responseMove = String.valueOf(move[0]) + "," +
                String.valueOf(move[1]) + "," + String.valueOf(move[2])
                + "," + String.valueOf(move[3] + "," + String.valueOf(move[4]));
        resp.setContentType("text/plain;charset=UTF-8");
        resp.getWriter().print(responseMove);
        resp.setCharacterEncoding("UTF-8");
    }
    
}
