package com.app;
import com.User.*;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/newuserservlet"})
public class NewUserServlet extends HttpServlet 
{	
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String uname = request.getParameter("user");
		String password = request.getParameter("pass1");
		String password2 = request.getParameter("pass2");
		
		RequestDispatcher call;
			
		if(password.equals(password2))
		{
			UserOperation operation = FactoryUser.getInstance("SQL");
			operation.getConnection();

			if(!operation.userExist(uname))
			{
				if(operation.addUser(uname, password))
				{
					out.println("<h3> You are Successfully Created New Account! Thanks for using our Product! </h3>");
					call = request.getRequestDispatcher("/Login.jsp");
				}

				else
				{
					out.println("Error! Try Again!");
					call = request.getRequestDispatcher("/NewUser.jsp");
				}
			}

			else
			{
				out.println("User Name Exist! Try using Different User Name!");
				call = request.getRequestDispatcher("/NewUser.jsp");
			}
		}
		else
		{
			out.println("Password Doesn't Match! Try Again!");
			call = request.getRequestDispatcher("/NewUser.jsp");
		}

		call.include(request, response);
		out.close();
	}
	
}