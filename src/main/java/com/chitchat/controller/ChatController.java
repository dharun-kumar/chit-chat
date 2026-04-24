package com.chitchat.controller;
import com.chitchat.repo.chat.*;
import com.chitchat.config.security.SessionKey;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/ChatController"}, asyncSupported=true)
public class ChatController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(ChatController.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = request.getParameter("message");
        HttpSession session = request.getSession();
        String sender = (String) session.getAttribute("user");
        String receiver = (String) session.getAttribute("receiver");
        String sessionkey = session.getAttribute("sessionkey").toString();

        try {
            if (!SessionKey.checkSession(sender, sessionkey)) {
                request.getRequestDispatcher("/Login.jsp").forward(request, response);
                return;
            }

            ChatRepo operation = FactoryChat.getInstance();
            operation.getConnection();

            if (!message.isEmpty()) {
                operation.sendMessage(sender, receiver, message);
            }
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Error processing chat message from: " + sender, e);
            throw new ServletException("Chat operation failed", e);
        }
    }
}
