<%@ page import="ch.ethz.inf.dbproject.PersonsServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ include file="Header.jsp" %>
<% final User user = UserManagement.getCurrentlyLoggedInUser(session);%>

<h1>Persons</h1>

<hr/>

<%= session.getAttribute(PersonsServlet.SESSION_PERSONS) %>

<hr/>
<%

if (user != null) {
	// User is logged in. He can add person.
%>
	
		<form action="Persons" action="get">
		<input type="hidden" name="filter" value="addUser" />
		First name:
		<input type="text" name="firstName" />	<br/>			
		Last name:
		<input type="text" name="lastName" /><br/>	
		Country:
		<input type="text" name="country" />	<br/>	
		City:
		<input type="text" name="city" /><br/>	
		Zipcode:
		<input type="text" name="zipcode" /><br/>	
		Street:
		<input type="text" name="street" /><br/>		
		StreetNr:
		<input type="text" name="streetno" /><br/>	
		Date Of Birth:
		<input type="text" name="dateOfBirth" />		
		<input type="submit" value="Add" title="addUser" />
		</form>

<%
}
%>

<%@ include file="Footer.jsp" %>