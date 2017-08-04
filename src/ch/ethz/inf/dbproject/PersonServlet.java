package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.model.Case;
import ch.ethz.inf.dbproject.model.Comment;
import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class HomePage
 */
@WebServlet(description = "Page displaying a detail page to a person", urlPatterns = { "/Person" })
public final class PersonServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	public static final String SESSION_PERSON = "person";
	public static final String SESSION_PERSON_COMMENTS = "caseComments";
	public static final String SESSION_IDSTRING = "idstring";
	public static final String SESSION_HAS_COMMENTS = "hasComment";
		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PersonServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession();
		
		 // Construct a table to present all properties of a p
		final BeanTableHelper<Person> table = new BeanTableHelper<Person>(
				"cases" 		/* The table html id property */,
				"casesTable" /* The table html class property */,
				Person.class 	/* The class of the objects (rows) that will be displayed */
		);
		table.setVertical(true);
		
		table.addBeanColumn("First name", "FirstName");
		table.addBeanColumn("Last name", "LastName");
		table.addBeanColumn("Date of Birth", "DateOfBirth");
		table.addBeanColumn("Country", "country");
		table.addBeanColumn("City", "city");
		table.addBeanColumn("ZipCode", "zipcode");
		table.addBeanColumn("Street", "street");
		table.addBeanColumn("StreetNr", "streetNr");
		
		 // Construct a table to for the comments of a case
		final BeanTableHelper<Comment> personComments = new BeanTableHelper<Comment>(
				"comment" 		/* The table html id property */,
				"casesTable" /* The table html class property */,
				Comment.class 	/* The class of the objects (rows) that will be displayed */
		);
		// Add columns to the new table
		personComments.addBeanColumn("Author", "username");
		personComments.addBeanColumn("Comment", "comment");

		final String idString = request.getParameter("id");
		final String comment = request.getParameter("comment");
		final String username = request.getParameter("username");
		final String action = request.getParameter("action");
		final String filter = request.getParameter("filter");
		
		session.setAttribute(SESSION_HAS_COMMENTS, false);
		int personID = -1;
		try {
			if (idString == null) {
				personID = Integer.parseInt((String) session.getAttribute(SESSION_IDSTRING));
				if (action.equals("add_comment")) {
					session.setAttribute(SESSION_HAS_COMMENTS, true);
					dbInterface.insertPersonComment(personID, username, comment);
				}
				else if (filter != null) {
					final String name = request.getParameter(filter);
						dbInterface.editPersonEntry(String.valueOf(personID),filter,name);
				}
			}
			else {
				personID = Integer.parseInt(idString);
				session.setAttribute(SESSION_IDSTRING, idString);
			}
		} catch (Exception e) {
			e.printStackTrace();
	        this.getServletContext().getRequestDispatcher("/Person.jsp").forward(request, response);	        
		}

		
		// Add person
		table.addObject(dbInterface.getPersonById(personID));
		// Add comments to person
		personComments.addObjects(dbInterface.getCommentsByPersonId(personID));
		if (dbInterface.personHasComment(Integer.parseInt((String) session.getAttribute(SESSION_IDSTRING)))) {
			session.setAttribute(SESSION_HAS_COMMENTS, true);
		}
		
		// Make tables accessible
		session.setAttribute(SESSION_PERSON, table);
		session.setAttribute(SESSION_PERSON_COMMENTS, personComments);
		
        this.getServletContext().getRequestDispatcher("/Person.jsp").forward(request, response);	        
	}
}