
<h1>Create New Account</h1>

<form action="newuserservlet" method="post" autocomplete="off">
	User Name : <input type="text" name="user" required /><br/><br/>
	Password : <input type="password" name="pass1" required /><br/><br/>
	Confirm Password : <input type="password" name="pass2" required /><br/><br/>
	<input type="submit" value="Create Account"/>
</form>

<form action="Login.jsp" method="get">
	<input type="submit" value="Login into Existing Account"/>
</form>