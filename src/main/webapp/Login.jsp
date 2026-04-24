
<h1>User Login</h1>

<form action="LoginController" method="post" autocomplete="off">
	User Name : <input type="text" name="user" required /><br/><br/>
	Password : <input type="password" name="pass" required /><br/><br/>
	<input type="submit" value="login"/>
</form>

<form action="AddUser.jsp" method="get">
	<input type="submit" value="Create New Account"/>
</form>