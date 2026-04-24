
<%@page import="java.util.ArrayList"%>
<%@page import="com.chitchat.repo.chat.*"%>
<%@page import="com.chitchat.config.security.*"%>

<%
    String user = session.getAttribute("user").toString();
    String sessionkey = session.getAttribute("sessionkey").toString();

	if (user == null || !SessionKey.checkSession(user, sessionkey))
		request.getRequestDispatcher("/Login.jsp");
%>

<h1>Select User to Chat</h1>

<form action="ContactsController" method="post" autocomplete="off">
	<select name="receiver">

		<%
		    ChatRepo operation = FactoryChat.getInstance();
            operation.getConnection();

		    ArrayList<String> users = operation.getAllUsers(user);

			for(String receiver : users)
			{   %>
					<option value = <%= receiver %>> <%= receiver %> </option>
		<%  }  %>

	</select>
	<input type="submit" value = "start chat">
</form>

<form action="LogoutController" method="post">
	<input type="submit" value="Log out"/>
</form>