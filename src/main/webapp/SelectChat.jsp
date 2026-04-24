
<%@page import="java.util.ArrayList"%>
<%@page import="com.Chat.*"%>
<%@page import="com.Others.*"%>

<%
    String user = session.getAttribute("user").toString();
    String sessionkey = session.getAttribute("sessionkey").toString();

	if (user == null || !SessionKey.checkSession(user, sessionkey))
		request.getRequestDispatcher("/Login.jsp");
%>

<h1>Select User to Chat</h1>

<form action="selectchatservlet" method="post" autocomplete="off">
	<select name="receiver">

		<%
		    ChatOperation operation = FactoryChat.getInstance("SQL");
            operation.getConnection();

		    ArrayList<String> users = operation.getAllUsers(user);

			for(String receiver : users)
			{   %>
					<option value = <%= receiver %>> <%= receiver %> </option>
		<%  }  %>

	</select>
	<input type="submit" value = "start chat">
</form>

<form action="logoutservlet" method="post">
	<input type="submit" value="Log out"/>
</form>