package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Case;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class of Case list page
 */
@WebServlet(description = "The home page of the project", urlPatterns = { "/Cases" })
public final class CasesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CasesServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) 
			throws ServletException, IOException {

		final HttpSession session = request.getSession(true);

		/*******************************************************
		 * Construct a table to present all our results
		 *******************************************************/
		final BeanTableHelper<Case> table = new BeanTableHelper<Case>(
				"cases" 		/* The table html id property */,
				"casesTable" /* The table html class property */,
				Case.class 	/* The class of the objects (rows) that will be displayed */
		);
		
		
		table.addLinkColumn(""	/* The header. We will leave it empty */,
				"View Case" 	/* What should be displayed in every row */,
				"Case?id=" 	/* This is the base url. The final url will be composed from the concatenation of this and the parameter below */, 
				"id" 			/* For every case displayed, the ID will be retrieved and will be attached to the url base above */);
		// Add columns to the new table
		table.addBeanColumn("Title", "title");
		table.addBeanColumn("Date", "date");
		table.addBeanColumn("Open", "open");
		
		// Pass the table to the session. This will allow the respective jsp page to display the table.
		session.setAttribute("cases", table);

		// The filter parameter defines what to show on the Projects page
		final String filter = request.getParameter("filter");
		final String category = request.getParameter("category");


		 if (category != null) {
			table.addObjects(this.dbInterface.getCasesPerCategory(category));
		} else if (filter != null) {
			if (filter.equals("open")) {
				table.addObjects(this.dbInterface.searchByStatus("open"));
			} else if (filter.equals("closed")) {
				table.addObjects(this.dbInterface.searchByStatus("closed"));
			} else if (filter.equals("recent")) {
				// cases from the last 6 months
				table.addObjects(this.dbInterface.searchRecent());
			}
			else if (filter.equals("oldest")) {
				// the 3 oldest cases
				table.addObjects(this.dbInterface.oldestUnsolved());
			}
			
		}
		
		// Add case
		final String action = request.getParameter("action");
		if (action != null && action.equals("add")) {
			final String title = request.getParameter("title");
			final String description = request.getParameter("description");
			final float latitude = Float.parseFloat(request.getParameter("latitude"));
			final float longitude = Float.parseFloat(request.getParameter("longitude"));
			final String categoryName = request.getParameter("categoryName");
			dbInterface.insertCase(title, description, latitude, longitude, categoryName);
		}
		
		if (filter == null && category == null) {
			// If no filter is specified, then we display all the cases!
			table.addObjects(this.dbInterface.getAllCases());
		}

		// Finally, proceed to the Projects.jsp page which will render the Projects
		this.getServletContext().getRequestDispatcher("/Cases.jsp").forward(request, response);
	}
}