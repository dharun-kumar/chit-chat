
<%@page import="java.util.ArrayList"%>
<%@page import="com.Chat.*"%>

<%
    String sender = session.getAttribute("user").toString();
    String receiver = session.getAttribute("receiver").toString();
    String lasttime = session.getAttribute("lasttime").toString();

    ChatOperation operation = FactoryChat.getInstance("SQL");
    operation.getConnection();

    ArrayList<String[]> chats = operation.getChats(sender, receiver,lasttime);

    for(String chat[] : chats)
    {
	    if(chat[1].equals(sender))
		    out.println("<p align =\"right\" style=\"font-size:20px\">" + chat[2]+ "</p>");
		
	    else if(chat[1].equals(receiver))
		    out.println("<p align =\"left\" style=\"font-size:20px\">"+ chat[2]+ "</p>");

		session.setAttribute("lasttime", chat[3]);
    }

%>
