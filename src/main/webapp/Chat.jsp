<%@page import="com.Database.*"%>
<%@page import="com.Others.*"%>

<%
	String user = session.getAttribute("user").toString();
    String sessionkey = session.getAttribute("sessionkey").toString();

    if (user == null || !SessionKey.checkSession(user, sessionkey))
    	request.getRequestDispatcher("/Login.jsp");

	session.setAttribute("lasttime", "2021-01-01 00:00:00");
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

<form action="SelectChat.jsp" method="post">
	<input type="submit" value="Back"/>
</form>
        
<script>
	function postMessage()
	{
		var sendmsg = new XMLHttpRequest();
		sendmsg.open("POST", "chatservlet", false);
		sendmsg.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
           
    	var message = escape(document.getElementById("message").value);
    	document.getElementById("message").value = "";
    	sendmsg.send("message="+message);
	}
	
	setInterval(function loadDoc() 
	{
		  var receivemsg = new XMLHttpRequest();
		  receivemsg.onreadystatechange = function()
		  {
		    if (this.readyState == 4 && this.status == 200)
		  {
		      var message = document.getElementById("content");
		      message.innerHTML = message.innerHTML + this.responseText;
		    }
		  };
		  receivemsg.open("GET", "DisplayChat.jsp", true);
		  receivemsg.send();
		}, 1000);
          
</script>
