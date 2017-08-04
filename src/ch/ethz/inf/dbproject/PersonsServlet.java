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
import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

/**
 * Servlet implementation class HomePage
 */
@WebServlet(description = "Page displaying all person of interests", urlPatterns = { "/Persons" })
public final class PersonsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	public static final String SESSION_PERSONS = "persons";
		
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PersonsServlet() {
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
		
		// Link column to the detail page of a person
		table.addLinkColumn("", "View Person", "Person?id=", "id");
		
		table.addBeanColumn("First name", "FirstName");
		table.addBeanColumn("Last name", "LastName");
		table.addBeanColumn("Date of Birth", "DateOfBirth");
		
		// Make table accessible
		session.setAttribute(SESSION_PERSONS, table);
		
		// The filter parameter defines what to show on the cases page
		final String filter = request.getParameter("filter");
		final String firstName = request.getParameter("firstName");
		final String lastName = request.getParameter("lastName");
		final String dateOfBirth = request.getParameter("dateOfBirth");
		final String zipCode = request.getParameter("zipcode");
		final String streetNo = request.getParameter("streetno");
		final String country = request.getParameter("country");
		final String street = request.getParameter("street");
		final String city = request.getParameter("city");

		if (filter != null) {
			dbInterface.insertPerson(firstName,lastName,dateOfBirth,zipCode,streetNo,street,country,city);
		}
				
				
		table.addObjects(this.dbInterface.getAllPersons());
		
        this.getServletContext().getRequestDispatcher("/Persons.jsp").forward(request, response);	        
	}
}