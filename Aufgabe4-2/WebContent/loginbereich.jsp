<%@page import="java.util.Iterator"%>
<%@page import="application.Player"%>
<%@page import="java.util.Collection"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	   <h1>Loginfenster</h1>
       <p id="user">Login-Bereich: </p>
       <form method="POST" action="LoginServlet">
            <input type ="text" name="username" id=input></input>
       		<button id=button type="submit">Login</button>
       </form>
       <h2>User-Bereich</h2>
       <%
        Collection<Player> playerlist = (Collection<Player>) session.getAttribute("players");
        if(playerlist.isEmpty()) { %>
        	<h3>Keine User angemeldet!</h3>
       <% }
        else { %>
       <table>
       <% for (Player player : playerlist) { %>
    	   <tr><td><%=player.getName() %></td></tr>
       <% } %>
       </table>
       <% } %>
       <h2>Katalog-Bereich</h2>
       <%
       	java.util.Map<String, application.Catalog> catalogMap = (java.util.Map<String, application.Catalog>) session.getAttribute("catalogs");
       
       	if(catalogMap.isEmpty()){ %>
       		<h3>Keine Kataloge vorhanden!</h3>   
       <% }
       	else { 
       		%>
       		<table>
       		<% for(String cat : catalogMap.keySet()) { %>
		    	<tr><td><%= cat %></td></tr>
		    <% } %>
		    </table>
       <% } %>	
</body>
</html>