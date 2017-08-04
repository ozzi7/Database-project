package ch.ethz.inf.dbproject;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.model.Case;
import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class Search
 */
@WebServlet(description = "The search page for cases", urlPatterns = { "/Search" })
public final class SearchServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	public static final String SESSION_CASE_RESULTS_FOUND = "caseResultsFound";
	public static final String SESSION_PERSON_RESULTS_FOUND = "personResultsFound";
	public static final String SESSION_CASE_RESULTS = "caseResults";
	public static final String SESSION_PERSON_RESULTS = "personResults";
	private final DatastoreInterface dbInterface = new DatastoreInterface();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		final HttpSession session = request.getSession(true);
		
		/*******************************************************
		 * Construct a table to present all our search results
		 *******************************************************/
		final BeanTableHelper<Case> casetable = new BeanTableHelper<Case>(
				"cases" 		/* The table html id property */,
				"casesTable" /* The table html class property */,
				Case.class 	/* The class of the objects (rows) that will be displayed */
		);

		// Add columns to the new table
		casetable.addLinkColumn(""	/* The header. We will leave it empty */,
				"View Case" 	/* What should be displayed in every row */,
				"Case?id=" 	/* This is the base url. The final url will be composed from the concatenation of this and the parameter below */, 
				"id" 			/* For every case displayed, the ID will be retrieved and will be attached to the url base above */);
		// Add columns to the new table
		casetable.addBeanColumn("Title", "Title");
		casetable.addBeanColumn("Description", "Description");

		/*******************************************************
		 * Construct a table for person results
		 *******************************************************/
		final BeanTableHelper<Person> personTable = new BeanTableHelper<Person>(
				"cases" 		/* The table html id property */,
				"casesTable" /* The table html class property */,
				Person.class 	/* The class of the objects (rows) that will be displayed */
		);

		// Add columns to the new table
		personTable.addLinkColumn(""	/* The header. We will leave it empty */,
				"View Person" 	/* What should be displayed in every row */,
				"Person?id=" 	/* This is the base url. The final url will be composed from the concatenation of this and the parameter below */, 
				"id" 			/* For every case displayed, the ID will be retrieved and will be attached to the url base above */);
		// Add columns to the new table
		personTable.addBeanColumn("First Name", "firstName");
		personTable.addBeanColumn("Last Name", "lastName");
		
		// Pass the table to the session. This will allow the respective jsp page to display the table.
		session.setAttribute(SESSION_CASE_RESULTS_FOUND, false);
		session.setAttribute(SESSION_PERSON_RESULTS_FOUND, false);

		// The filter parameter defines what to show on the cases page
		final String filter = request.getParameter("filter");

		if (filter != null) {
			List<Case> cases = null;
			List<Person> persons = null;
			if (filter.equals("title")) {
				final String name = request.getParameter("title");
				cases = this.dbInterface.searchByTitle(name);
				if (!cases.isEmpty()) {
					casetable.addObjects(cases);
					session.setAttribute(SESSION_CASE_RESULTS_FOUND, true);
				}
			} else if (filter.equals("category")) {
				final String category = request.getParameter("category");
				cases = this.dbInterface.getCasesPerCategory(category);
				
			} else if (filter.equals("name")) {
				final String firstName = request.getParameter("firstName");
				final String lastName = request.getParameter("lastName");
				persons = this.dbInterface.getPersonsByNames(firstName, lastName);
			} else if (filter.equals("conviction_date")) {
				final Date date = Date.valueOf(request.getParameter("date"));
				persons = this.dbInterface.getPersonsByDateOfConviction(date);				
			} else if (filter.equals("conviction_reason")) {
				final String reason = request.getParameter("reason");
				persons = this.dbInterface.getPersonsByReasonOfConviction(reason);
			}
			if (cases != null && !cases.isEmpty()) {
				casetable.addObjects(cases);
				session.setAttribute(SESSION_CASE_RESULTS_FOUND, true);
			}
			if (persons != null && !persons.isEmpty()) {
				personTable.addObjects(persons);
				session.setAttribute(SESSION_PERSON_RESULTS_FOUND, true);
			}
		}
		
		session.setAttribute(SESSION_CASE_RESULTS, casetable);
		session.setAttribute(SESSION_PERSON_RESULTS, personTable);

		// Finally, proceed to the Search.jsp page which will render the search results
        this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);	        
	}
}