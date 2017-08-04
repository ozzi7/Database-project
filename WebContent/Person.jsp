<%@ page import="ch.ethz.inf.dbproject.PersonServlet"%>
<%@ page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<% final User user = UserManagement.getCurrentlyLoggedInUser(session);%>

<h1>Case Details</h1>

<%= session.getAttribute(PersonServlet.SESSION_PERSON) %>

<% 
if ((Boolean) session.getAttribute(PersonServlet.SESSION_HAS_COMMENTS)) {
	// Case has comments. Display comments:
%>
<h1>Person Comments</h1>
<%= session.getAttribute(PersonServlet.SESSION_PERSON_COMMENTS) %>

<%
}
if (user != null) {
	// User is logged in. He can add a comment and close/open case.
%>
	<br>
		<form action="Person" action="get">
		<input type="hidden" name="action" value="add_comment" />
		<input type="hidden" name="username" value="<%=user.getUsername() %> " />
		<h3>Add Comment</h3>
		<textarea rows = "4" cols="50" name ="comment"></textarea>
		<br/>
		<input type="submit" value="Submit" />
		</form>
		
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="firstName" />
		First name:
		<input type="text" name="firstName" />
		<input type="submit" value="Edit" title="firstName" />
		</form>
				
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="lastName" />
		Last name:
		<input type="text" name="lastName" />
		<input type="submit" value="Edit" title="lastName" />
		</form>
		
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="country" />
		Country:
		<input type="text" name="country" />
		<input type="submit" value="Edit" title="country" />
		</form>
		
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="city" />
		City:
		<input type="text" name="city" />
		<input type="submit" value="Edit" title="city" />
		</form>
		
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="zipcode" />
		Zipcode:
		<input type="text" name="zipcode" />
		<input type="submit" value="Edit" title="zipcode" />
		</form>
		
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="street" />
		Street:
		<input type="text" name="street" />
		<input type="submit" value="Edit" title="street" />
		</form>
		
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="streetno" />
		StreetNr:
		<input type="text" name="streetno" />
		<input type="submit" value="Edit" title="streetno" />
		</form>
			
		<form action="Person" action="get">
		<input type="hidden" name="filter" value="dateOfBirth" />
		Date Of Birth:
		<input type="text" name="dateOfBirth" />
		<input type="submit" value="Edit" title="dateOfBirth" />
		</form>
	

<hr/>

<%
}
%>