<%@page import="java.time.Instant"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.chitchat.model.Chat"%>
<%@page import="com.chitchat.repo.chat.*"%>
<%
    String sender = (String) session.getAttribute("user");
    String receiver = (String) session.getAttribute("receiver");
    Instant lasttime = (Instant) session.getAttribute("lasttime");

    if (sender == null || receiver == null || lasttime == null) {
        response.sendRedirect("/Login.jsp");
        return;
    }

    try {
        ChatRepo operation = FactoryChat.getInstance();
        operation.getConnection();

        ArrayList<Chat> chats = operation.getChats(sender, receiver, lasttime);

        Instant newLasttime = lasttime;
        for (Chat chat : chats) {
            String message = (chat.getMessage() != null) ? chat.getMessage() : "";
            if (chat.getSender().equals(sender)) {
                out.println("<p align=\"right\" style=\"font-size:20px\">" + message + "</p>");
            } else if (chat.getSender().equals(receiver)) {
                out.println("<p align=\"left\" style=\"font-size:20px\">" + message + "</p>");
            }
            newLasttime = chat.getDelivertime();
        }
        session.setAttribute("lasttime", newLasttime);
    } catch (RuntimeException e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to fetch messages");
    }
%>
