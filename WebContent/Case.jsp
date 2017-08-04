<%@ page import="ch.ethz.inf.dbproject.CaseServlet"%>
<%@ page import="ch.ethz.inf.dbproject.model.User"%>
<%@ page import="ch.ethz.inf.dbproject.util.UserManagement"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>
<% final User user = UserManagement.getCurrentlyLoggedInUser(session);%>

<h1>Case Details</h1>

<%= session.getAttribute(CaseServlet.SESSION_CASE) %>

<% 
if ((Boolean) session.getAttribute(CaseServlet.SESSION_HAS_COMMENTS)) {
	// Case has comments. Display comments:
%>
<h1>Case Comments</h1>
<%= session.getAttribute(CaseServlet.SESSION_CASE_COMMENTS) %>


<%
}
if (user != null) {
	// User is logged in. He can add a comment and close/open case.
	boolean open =  (Boolean) session.getAttribute(CaseServlet.SESSION_CASE_OPEN);
	String value = open ? "close" : "open";
	String button = open ? "Close case" : "Open case"; 
%>
	<br>
	<form action="Case" method="get">
		<input type="hidden" name="action" value="add_comment" />
		<input type="hidden" name="username" value="<%= user.getUsername() %>" />
		<h3>Add Comment:</h3>
		<textarea rows="4" cols="50" name="comment"></textarea>
		<br />
		<input type="submit" value="Submit" />
	</form>
	<br/>
	<form action="Case" method="get">
		<input type="hidden" name="action" value="<%= value %>" />
		<input type="submit" value="<%= button %>" />
	</form>
<%	if (open) { %>
		<form action="Case" method="get">
			Link person:
			<%= session.getAttribute(CaseServlet.SESSION_LINKABLE_PERSONS) %>
			<input type="hidden" name="action" value="link" />
			<input type="submit" value="Link" />
		</form>
		<form action="Case" method="get">
			Unlink person:
			<%= session.getAttribute(CaseServlet.SESSION_SUSPECTS) %>
			<input type="hidden" name="action" value="unlink" />
			<input type="submit" value="Unlink" />
		</form>
<% 	} %>

<%
} else {} %>

<%@ include file="Footer.jsp"%>