<%@page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@page import="ch.ethz.inf.dbproject.model.User"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="Header.jsp" %>
<% final User user = UserManagement.getCurrentlyLoggedInUser(session);%>

<h1>Cases</h1>

<hr/>

<%= session.getAttribute("cases") %>

<hr/>

<%
if (user != null) {
	// User is logged in. He can add a comment and close/open case.
%>
	<form action="Cases" method="get">
		Title: <input type="text" name="title" /><br/>
		Description: <textarea rows="4" cols="50" name="description"></textarea><br/>
		Latitude: <input type="text" name="latitude" /><br/>
		Longitude: <input type="text" name="longitude" /><br/>
		Category: <input type="text" name="categoryName" /><br/>
		<input type="hidden" name="action" value="add" /><br/>
		<input type="submit" value="Add case" />
	</form>
<% } %>

<%@ include file="Footer.jsp" %>