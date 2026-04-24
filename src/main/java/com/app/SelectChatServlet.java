package com.app;

import com.Others.*;

import java.io.IOException;
import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/selectchatservlet"})
public class SelectChatServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		HttpSession session = request.getSession();

		String receiver = request.getParameter("receiver");
		session.setAttribute("receiver", receiver);

		String user = session.getAttribute("user").toString();
		String sessionkey = session.getAttribute("sessionkey").toString();

		if(!SessionKey.checkSession(user, sessionkey))
			request.getRequestDispatcher("/Login.jsp").forward(request, response);

		else
			request.getRequestDispatcher("Chat.jsp").include(request, response);
	}
}
