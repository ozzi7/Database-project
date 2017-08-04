package ch.ethz.inf.dbproject;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ch.ethz.inf.dbproject.model.DatastoreInterface;
import ch.ethz.inf.dbproject.model.User;
import ch.ethz.inf.dbproject.util.UserManagement;
import ch.ethz.inf.dbproject.util.html.BeanTableHelper;

@WebServlet(description = "Page that displays the user login / logout options.", urlPatterns = { "/User" })
public final class UserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final DatastoreInterface dbInterface = new DatastoreInterface();

	public final static String SESSION_USER_LOGGED_IN = "userLoggedIn";
	public final static String SESSION_USER_DETAILS = "userDetails";
	public final static String SESSION_USER = "loggedInUser";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected final void doGet(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		final HttpSession session = request.getSession(true);
		final BeanTableHelper<User> userDetails = new BeanTableHelper<User>("userDetails", "casesTable", User.class);
		userDetails.addBeanColumn("Username", "username");
		userDetails.addBeanColumn("First Name", "firstname");
		userDetails.addBeanColumn("Last Name", "lastname");
		
		// should be final to remember..
		User loggedUser = UserManagement.getCurrentlyLoggedInUser(session);

		if (loggedUser == null) {
			// Not logged in!
			session.setAttribute(SESSION_USER_LOGGED_IN, false);
			session.setAttribute(SESSION_USER, loggedUser);
		} else {
			// Logged in
			userDetails.addObject(loggedUser);

			session.setAttribute(SESSION_USER_LOGGED_IN, true);
		}

		final String action = request.getParameter("action");
		if (action != null && action.trim().equals("login") && loggedUser == null) {

			final String username = request.getParameter("username");
			final String password = request.getParameter("password");

			// Ask the data store interface if it knows this user
			if(dbInterface.userExists(username, password)) {
				// Retrieve User & store this user into the session
				loggedUser = dbInterface.getUser(username, password);
				
				userDetails.addObject(loggedUser);

				session.setAttribute(SESSION_USER_LOGGED_IN, true);
				session.setAttribute(SESSION_USER_DETAILS, userDetails);
				session.setAttribute(SESSION_USER, loggedUser);
			}
		}

		if (action != null && action.trim().equals("register") && loggedUser == null) {

			final String username = request.getParameter("username");
			final String password = request.getParameter("password");
			final String firstname = request.getParameter("firstname");
			final String lastname = request.getParameter("lastname");

			// Check if username available
			if (dbInterface.usernameExists(username)) {
				// TODO warn user
			}
			else {
				// Store user and log in
				dbInterface.register(username,password,firstname,lastname);
				loggedUser = dbInterface.getUser(username, password);
				
				userDetails.addObject(loggedUser);

				session.setAttribute(SESSION_USER_LOGGED_IN, true);
				session.setAttribute(SESSION_USER_DETAILS, userDetails);
				session.setAttribute(SESSION_USER, loggedUser);
			}
		}
		// Finally, proceed to the User.jsp page which will render the profile
		this.getServletContext().getRequestDispatcher("/User.jsp").forward(request, response);

	}

}
