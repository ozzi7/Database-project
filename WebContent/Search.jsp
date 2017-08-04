<%@page import="ch.ethz.inf.dbproject.SearchServlet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="Header.jsp" %>

<% 
if ((Boolean) session.getAttribute(SearchServlet.SESSION_CASE_RESULTS_FOUND)) {
	// Case results were found. Display the details:
%>
	<%= session.getAttribute(SearchServlet.SESSION_CASE_RESULTS) %>
<%
}
else if ((Boolean) session.getAttribute(SearchServlet.SESSION_PERSON_RESULTS_FOUND)) {
	// Person results were found. Display the details:
%>
	<%= session.getAttribute(SearchServlet.SESSION_PERSON_RESULTS) %>
<% } %>

<h1>Search Cases</h1>

<hr/>

<form method="get" action="Search">
	<div>
		<input type="hidden" name="filter" value="title" />
		<p>Search By (Partial) Title:</p>
		<input type="text" name="title" />
		<input type="submit" value="Search" title="Search by title of case" />
	</div>
</form>

<hr/>

<form method="get" action="Search">
	<div>
		<input type="hidden" name="filter" value="category" />
		<p>Search By Category:</p>
		<input type="text" name="category" />
		<input type="submit" value="Search" title="Search by Category" />
	</div>
</form>

<form method="get" action="Search">
	<div>
		<input type="hidden" name="filter" value="name" />
		<p>Search By (Partial) First Name/Last Name:</p>
		<input type="text" name="firstName" />
		<input type="text" name="lastName" />
		<input type="submit" value="Search" title="Search by Name of Person" />
	</div>
</form>

<form method="get" action="Search">
	<div>
		<input type="hidden" name="filter" value="conviction_date" />
		<p>Search by date of conviction:</p>
		<input type="text" name="date" />
		<input type="submit" value="Search" title="Search by date of conviction" />
	</div>
</form>

<form method="get" action="Search">
	<div>
		<input type="hidden" name="filter" value="conviction_reason" />
		<p>Search by reason of conviction:</p>
		<input type="text" name="reason" />
		<input type="submit" value="Search" title="Search by reason of conviction" />
	</div>
</form>

<%@ include file="Footer.jsp" %>
