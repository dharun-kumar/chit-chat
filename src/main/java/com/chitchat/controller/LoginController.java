package com.chitchat.controller;
import com.chitchat.config.security.SessionKey;
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

@WebServlet(urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(LoginController.class.getName());

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String uname = request.getParameter("user");
        String password = request.getParameter("pass");
        RequestDispatcher call;

        try {
            UserRepo operation = FactoryUser.getInstance();
            operation.getConnection();

            if (operation.userLogin(uname, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("user", uname);
                String sessionkey = SessionKey.generateKey();
                session.setAttribute("sessionkey", sessionkey);
                operation.setKey(uname, sessionkey);
                out.println(" <h3> Hi " + uname + " You are Successfully Logged in! </h3>");
                call = request.getRequestDispatcher("/Contacts.jsp");
            } else if (!operation.userExist(uname)) {
                out.println("<h3> User Doesn't Exist ! Create New Account! </h3>");
                call = request.getRequestDispatcher("/AddUser.jsp");
            } else {
                out.println("<h3> Invalid Password </h3>");
                call = request.getRequestDispatcher("/Login.jsp");
            }

            call.include(request, response);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Error during login for user: " + uname, e);
            throw new ServletException("Login operation failed", e);
        } finally {
            out.close();
        }
    }
}
