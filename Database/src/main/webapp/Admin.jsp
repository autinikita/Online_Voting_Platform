<%
String username1=request.getParameter("username");
String password1=request.getParameter("password");

if(username1.equalsIgnoreCase("admin") && password1.equalsIgnoreCase("admin"))
{
	response.sendRedirect("add.html");
}
else
    response.sendRedirect("errorAdminLogin.html");
	

%>