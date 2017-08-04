package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.model.Conviction;
import ch.ethz.inf.dbproject.model.Comment;
import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Case;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;
import ch.ethz.inf.dbproject.util.html.ListHelper;
import ch.ethz.inf.dbproject.util.html.ListHelper.ListHelperGeneratorFunction;

/**
 * Servlet implementation class of Case Details Page
 */
@WebServlet(description = "Displays a specific case.", urlPatterns = { "/Case" })
public final class CaseServlet extends HttpServlet {

	public static final String SESSION_CASE = "case";
	public static final String SESSION_CASE_COMMENTS = "caseComments";
	public static final String SESSION_IDSTRING = "idstring";
	public static final String SESSION_HAS_COMMENTS = "hasComment";
	public static final String SESSION_CASE_OPEN = "caseOpen";
	
	public static final String SESSION_SUSPECTS = "suspects";
	public static final String SESSION_LINKABLE_PERSONS = "personsWithoutSuspects";
	
	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CaseServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		final HttpSession session = request.getSession(true);
		
		/*******************************************************
		 * Construct a table to present all properties of a case
		 *******************************************************/
		final BeanTableHelper<Case> table = new BeanTableHelper<Case>(
				"cases" 		/* The table html id property */,
				"casesTable" /* The table html class property */,
				Case.class 	/* The class of the objects (rows) that will be displayed */
		);
		// Add columns to the new table
		table.addBeanColumn("Title", "title");
		table.addBeanColumn("Description", "description");
		table.addBeanColumn("Date", "date");
		table.addBeanColumn("Time", "time");
		table.addBeanColumn("Longitdue", "longitude");
		table.addBeanColumn("Latitude", "latitude");
		table.addBeanColumn("Open", "open");
		table.addBeanColumn("Category name", "categoryName");
		//table.addLinkColumn("Category", "View Category", "Category?id=", "id");
		
		/*******************************************************
		 * Construct a table to for the comments of a case
		 *******************************************************/
		final BeanTableHelper<Comment> caseComments = new BeanTableHelper<Comment>(
				"comment" 		/* The table html id property */,
				"casesTable" /* The table html class property */,
				Comment.class 	/* The class of the objects (rows) that will be displayed */
		);
		// Add columns to the new table
		caseComments.addBeanColumn("Author", "username");
		caseComments.addBeanColumn("Comment", "comment");
		
		session.setAttribute(SESSION_HAS_COMMENTS,false);
		
		final String idString = request.getParameter("id");
		final String comment = request.getParameter("comment");
		final String username = request.getParameter("username");
		final String action = request.getParameter("action");
		
		int caseID = -1;
		try {
			if (idString == null) {
				caseID = Integer.parseInt((String) session.getAttribute(SESSION_IDSTRING));
				// Perform link/unlink
				final String personid = request.getParameter("personid");
				int personID = -1;
				if (personid != null) {
					personID = Integer.parseInt(personid);
				}
				if (action.equals("add_comment")) {
					session.setAttribute(SESSION_HAS_COMMENTS, true);
					// insert the new comment
					dbInterface.insertCaseComment(caseID, username, comment);
				}
				else if (action.equals("open")) {
					this.dbInterface.openCase(caseID);
				}
				else if (action.equals("close")) {
					this.dbInterface.closeCase(caseID);
				}
				else if (action.equals("link")) {
					dbInterface.linkPerson(caseID, personID);
				}
				else if (action.equals("unlink")) {
					dbInterface.unlinkPerson(caseID, personID);
				}
			}
			else {
				caseID = Integer.parseInt(idString);
				session.setAttribute(SESSION_IDSTRING, idString);
			}
		} catch (final Exception ex) {
			ex.printStackTrace();
			this.getServletContext().getRequestDispatcher("/Cases.jsp").forward(request, response);
		}
		table.addObject(dbInterface.getCaseById(caseID)); // add case to table
		
		table.setVertical(true);
		if (this.dbInterface.caseHasComment(Integer.parseInt((String) session.getAttribute(SESSION_IDSTRING)))){
			session.setAttribute(SESSION_HAS_COMMENTS, true);
			caseComments.addObjects(this.dbInterface.getCommentsByCaseId(caseID));
		}
		session.setAttribute(SESSION_CASE, table);
		session.setAttribute(SESSION_CASE_COMMENTS, caseComments);
		session.setAttribute(SESSION_CASE_OPEN, dbInterface.isCaseOpen(caseID));
		
		
		/** Link/Unlink ***********************/
		// Get list of subjects
		ListHelperGeneratorFunction<Person> function = new ListHelperGeneratorFunction<Person>() {
			
			private final String formatString = "<option value=\"%s\">%s</option>\n";  

			@Override
			public String generate(Person item) {
				return String.format(formatString, item.getId(), item.getFirstName() + " " + item.getLastName());
			}
		};
		String selectTag = "select name=\"personid\"";
		final ListHelper<Person> suspectsOptions = new ListHelper<Person>(function, selectTag);
		List<Person> suspects = dbInterface.getAllSuspects(caseID);
		suspectsOptions.addItems(suspects);
		
		final ListHelper<Person> linkablePersonsOptions = new ListHelper<Person>(function, selectTag);
		final List<Person> linkablePersons = dbInterface.getLinkablePersons(caseID);
		linkablePersonsOptions.addItems(linkablePersons);
		
		session.setAttribute(SESSION_LINKABLE_PERSONS, linkablePersonsOptions);
		session.setAttribute(SESSION_SUSPECTS, suspectsOptions);
		
		this.getServletContext().getRequestDispatcher("/Case.jsp").forward(request, response);
	}
}