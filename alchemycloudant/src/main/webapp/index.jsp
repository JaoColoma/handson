<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="Bean.AlchemyConnector" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>AlchemyAPI - Feature Extraction and Image Analysis</title>
    </head>
    <body>
         <form action="IServlet" method="GET">
                        <input type="text" name="gurl" size="80">
                        <input type="submit" value="Extract Information">
    	</form> <br/>
					<%
						if (request.getAttribute("face") != null){
							out.println("<h3>" + request.getAttribute("face") + "</h3>");
						}
					%>
    </body>
</html>