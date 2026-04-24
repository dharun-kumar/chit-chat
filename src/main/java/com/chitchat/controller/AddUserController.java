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

@WebServlet(urlPatterns = {"/AddUserController"})
public class AddUserController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(AddUserController.class.getName());

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String uname = request.getParameter("user");
        String password = request.getParameter("pass1");
        String password2 = request.getParameter("pass2");
        RequestDispatcher call;

        try {
            if (password.equals(password2)) {
                UserRepo operation = FactoryUser.getInstance();
                operation.getConnection();

                if (!operation.userExist(uname)) {
                    if (operation.addUser(uname, password)) {
                        out.println("<h3> You are Successfully Created New Account! Thanks for using our Product! </h3>");
                        call = request.getRequestDispatcher("/Login.jsp");
                    } else {
                        out.println("Error! Try Again!");
                        call = request.getRequestDispatcher("/AddUser.jsp");
                    }
                } else {
                    out.println("User Name Exist! Try using Different User Name!");
                    call = request.getRequestDispatcher("/AddUser.jsp");
                }
            } else {
                out.println("Password Doesn't Match! Try Again!");
                call = request.getRequestDispatcher("/AddUser.jsp");
            }

            call.include(request, response);
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "Error creating user: " + uname, e);
            throw new ServletException("User creation failed", e);
        } finally {
            out.close();
        }
    }
}
