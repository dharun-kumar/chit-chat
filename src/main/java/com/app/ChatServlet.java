package com.app;
import com.Chat.*;
import com.Others.SessionKey;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = {"/chatservlet"}, asyncSupported=true)
public class ChatServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String message = request.getParameter("message");

		HttpSession session = request.getSession();

		String sender = (String) session.getAttribute("user");
		String receiver = (String) session.getAttribute("receiver");

		String sessionkey = session.getAttribute("sessionkey").toString();

		if(!SessionKey.checkSession(sender, sessionkey))
			request.getRequestDispatcher("/Login.jsp").forward(request, response);

		ChatOperation operation = FactoryChat.getInstance("SQL");
		operation.getConnection();

		if (!message.isEmpty())
			operation.sendMessage(sender, receiver, message);
    }
}