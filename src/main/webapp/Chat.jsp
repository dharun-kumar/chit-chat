<%@page import="java.time.Instant"%>
<%@page import="com.chitchat.config.security.*"%>
<%
    String user = (String) session.getAttribute("user");
    String sessionkey = (String) session.getAttribute("sessionkey");

    if (user == null || sessionkey == null || !SessionKey.checkSession(user, sessionkey)) {
        request.getRequestDispatcher("/Login.jsp").forward(request, response);
        return;
    }

    session.setAttribute("lasttime", Instant.EPOCH);
%>

<h1> Chat Application </h1>

<h2>
<%= session.getAttribute("receiver").toString() %>
</h2>

<style>
    div {
        width: 350px;
        height: 400px;
        overflow: auto;
        border: 5px solid gray;
        padding: 10px;
    }
</style>

<div id = "content"> </div>

<br/><br/>

<form autocomplete="off">
    Message <input type="text" id="message" name="message" />
    <input type="button" onclick="postMessage();" value="send" />
</form>

<form action="Contacts.jsp" method="post">
    <input type="submit" value="Back"/>
</form>

<script>
    function postMessage() {
        var sendmsg = new XMLHttpRequest();
        sendmsg.open("POST", "ChatController", false);
        sendmsg.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        var message = escape(document.getElementById("message").value);
        document.getElementById("message").value = "";
        sendmsg.send("message=" + message);
    }

    setInterval(function loadDoc() {
        var receivemsg = new XMLHttpRequest();
        receivemsg.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                var message = document.getElementById("content");
                message.innerHTML = message.innerHTML + this.responseText;
            }
        };
        receivemsg.open("GET", "FetchChat.jsp", true);
        receivemsg.send();
    }, 1000);
</script>
