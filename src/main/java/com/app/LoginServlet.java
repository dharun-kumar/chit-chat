package com.app;

import com.Others.SessionKey;
import com.User.*;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/loginservlet"})
public class LoginServlet extends HttpServlet 
{	
	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String uname = request.getParameter("user");
		String password = request.getParameter("pass");
		
		RequestDispatcher call;

		UserOperation operation = FactoryUser.getInstance("SQL");
		operation.getConnection();

		if(operation.userLogin(uname, password))
		{
			HttpSession session = request.getSession();
			session.setAttribute("user", uname);

			String sessionkey = SessionKey.generateKey();
			session.setAttribute("sessionkey",sessionkey);
			operation.setKey(uname,sessionkey);

			out.println(" <h3> Hi " + uname + " You are Successfully Logged in! </h3>");
			call = request.getRequestDispatcher("/SelectChat.jsp");
		}

		else if(!operation.userExist(uname))
		{
			out.println("<h3> User Doesn't Exist ! Create New Account! </h3>");
			call = request.getRequestDispatcher("/NewUser.jsp");
		}

		else
		{
			out.println("<h3> Invalid Password </h3>");
			call = request.getRequestDispatcher("/Login.jsp");
		}

		call.include(request, response);
		out.close();
	}
}