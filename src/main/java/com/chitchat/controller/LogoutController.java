package com.chitchat.controller;
import com.chitchat.repo.user.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/LogoutController"})
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LogoutController.class.getName());

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();

        try {
            UserRepo operation = FactoryUser.getInstance();
            operation.getConnection();

            String uname = session.getAttribute("user").toString();
            operation.setKey(uname, null);

            session.removeAttribute("user");
            session.removeAttribute("receiver");
            session.removeAttribute("sessionkey");
            session.invalidate();

            out.println(" <h3> You are Successfully Logged Out! </h3>");
            RequestDispatcher call = request.getRequestDispatcher("/Login.jsp");
            call.include(request, response);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Error during logout", e);
            throw new ServletException("Logout operation failed", e);
        } finally {
            out.close();
        }
    }
}
