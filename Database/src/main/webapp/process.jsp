<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Processed Data</title>
</head>
<body>
    <h3>Button Click Processed</h3>
    <% 
        // Retrieve the clicked button data from the request
        String buttonData = request.getParameter("buttonData");
    %>
    <p>You clicked: <strong><%= buttonData != null ? buttonData : "No Data" %></strong></p>
    <a href="index.jsp">Go Back</a>
</body>
</html>