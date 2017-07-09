package com.developersam.web.control.ten;

import com.developersam.ten.game.Controller;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 * A servlet that returns AI's response to human's move in board game TEN
 */
public class GameResponseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String clientInfo = request.getParameter("clientInfo");
        byte[] move = Controller.respondToHumanMove(clientInfo);
        String responseMove = String.valueOf(move[0]) + "," + String.valueOf(move[1]) + ","
                + String.valueOf(move[2]) + "," + String.valueOf(move[3] + "," + String.valueOf(move[4]));
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(responseMove);
        response.setCharacterEncoding("UTF-8");
    }
}
