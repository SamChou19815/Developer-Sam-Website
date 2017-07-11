package com.developersam.web.control.botsciv;

import com.developersam.botsciv.model.compiler.parser.ProgramSyntaxError;
import com.developersam.botsciv.model.game.World;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 * A servlet that parses user programs, uses them, and sends back the entire game process for game Bots Civ.
 */
@WebServlet(name = "LoadEntireGameServlet", value="/apps/botsciv/loadEntireGame")
public class LoadEntireGameServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        String blackProgram = request.getParameter("blackProgram");
        String whiteProgram = request.getParameter("whiteProgram");
        World world = new World(5);
        try {
            world.addPrograms(blackProgram, whiteProgram);
            response.getWriter().print(world.execute().toString());
        }catch (ProgramSyntaxError e) {
            response.getWriter().print("Syntax Error in your Programs!");
        }
        response.setCharacterEncoding("UTF-8");
    }
}