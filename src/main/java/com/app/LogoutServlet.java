package com.app;

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

@WebServlet(urlPatterns = {"/logoutservlet"})
public class LogoutServlet extends HttpServlet 
{	
	private static final long serialVersionUID = 1L;
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		RequestDispatcher call;
		HttpSession session = request.getSession();

		UserOperation operation = FactoryUser.getInstance("SQL");
		operation.getConnection();

		String uname = session.getAttribute("user").toString();
		operation.setKey(uname,null);

		session.removeAttribute("user");
		session.removeAttribute("receiver");
		session.removeAttribute("sessionkey");
		session.invalidate();
		
		out.println(" <h3> You are Successfully Logged Out! </h3>");
		call = request.getRequestDispatcher("/Login.jsp");
		call.include(request, response);
		
		out.close();
	}
}