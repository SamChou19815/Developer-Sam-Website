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
@WebServlet(name = "LegalGameResponseServlet",
        value = "/apps/ten/responseLegal")
public class LegalGameResponseServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        String clientInfo = request.getParameter("clientInfo");
        int[] move = Controller.respondToLegalHumanMove(clientInfo);
        String responseMove = String.format("%d,%d", move[0], move[1]);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().print(responseMove);
        response.setCharacterEncoding("UTF-8");
    }
    
}