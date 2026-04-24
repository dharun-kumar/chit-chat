package com.chitchat.controller;
import com.chitchat.config.security.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/ContactsController"})
public class ContactsController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ContactsController.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String receiver = request.getParameter("receiver");
        session.setAttribute("receiver", receiver);
        String user = session.getAttribute("user").toString();
        String sessionkey = session.getAttribute("sessionkey").toString();

        try {
            if (!SessionKey.checkSession(user, sessionkey)) {
                request.getRequestDispatcher("/Login.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/Chat.jsp").include(request, response);
            }
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Error during opening conversion for user: " + user, e);
            throw new ServletException("contact failed to open", e);
        }
    }
}
