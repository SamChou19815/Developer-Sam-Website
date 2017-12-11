package com.developersam.web.control.ten;

import com.developersam.ten.game.Controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A servlet that returns AI's response to human's move in board game TEN.
 * This servlet assumes human moves are legal and already on the board.
 */
@WebServlet(name = "GameResponseServlet", value = "/apps/ten/response")
public class LegalGameResponseServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String clientInfo = request.getParameter("clientInfo");
        int[] move = Controller.respondToLegalHumanMove(clientInfo);
        String responseMove = String.valueOf(move[0]) + "," +
                String.valueOf(move[1]) + "," + String.valueOf(move[2]);
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().print(responseMove);
        response.setCharacterEncoding("UTF-8");
    }
    
}