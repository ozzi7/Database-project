package ch.ethz.inf.dbproject.model;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.dbproject.database.MySQLConnection;

/**
 * This class should be the interface between the web application
 * and the database. Keeping all the data-access methods here
 * will be very helpful for part 2 of the project.
 */
public final class DatastoreInterface {

	private final Connection sqlConnection;

	public DatastoreInterface() {
		this.sqlConnection = MySQLConnection.getInstance().getConnection();
	}
	
	private final <T extends StoreType> List<T> requestList(Class<T> clazz, String requestFormatString, Object... arguments) {
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery(String.format(requestFormatString, arguments));
		
			final List<T> types = new ArrayList<T>();
			try {
				while (rs.next()) {
					types.add(
						clazz.getConstructor(ResultSet.class).newInstance(rs)
					);
				}
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			rs.close();
			stmt.close();

			return types;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return null;			
		}
	}
	
	private final <T extends StoreType> T requestSingle(Class<T> clazz, String requestFormatString, Object... arguments) {
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery(String.format(requestFormatString, arguments));
		
			rs.next();
			T result = null;
			try {
				result = clazz.getConstructor(ResultSet.class).newInstance(rs);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			
			rs.close();
			stmt.close();

			return result;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return null;			
		}
	}
	
	private final void update(String updateFormatString, Object... arguments) {
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			stmt.executeUpdate(String.format(updateFormatString, arguments));
			stmt.close();
		} catch (final SQLException ex) {			
			ex.printStackTrace();
		}
	}
	
	public final Case getCaseById(final int id)  {
		return requestSingle(Case.class, "Select * from cases where CaseID = %s", id);
	}

	public final List<Comment> getCommentsByCaseId(final int id) {
		return requestList(Comment.class, "Select * from casecomments where caseID = %s", id);
	}
	
	public final List<Case> getAllCases() {
		return requestList(Case.class, "Select * from cases");
	}
	
	public final void register(String username, String password, String firstname, String lastname) {
		update("Insert into users (username, password, firstname, lastname) " +
				"values ('%s', '%s', '%s', '%s')", username, password, firstname, lastname);
	}	
	
	public final void openCase(int caseID) {
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			stmt.executeUpdate("UPDATE cases SET open = 1 WHERE caseID = " + String.valueOf(caseID));
			stmt.close();

		} catch (final SQLException ex) {			
			ex.printStackTrace();		
		}
	}
	
	public final void closeCase(int caseID) {
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			stmt.executeUpdate("UPDATE cases SET open = 0 WHERE caseID = " + String.valueOf(caseID));
			stmt.close();

		} catch (final SQLException ex) {			
			ex.printStackTrace();		
		}
	}
	
	public final void insertCaseComment(int caseID, String username, String comment) {
		update("Insert into casecomments (caseID, Author, CommentID, Comment )" +
				"values ('%s', '%s', %s, '%s')", caseID, username, "null", comment);
	}
	
	public final boolean userExists(String username, String password) {
		try {
			boolean userFound = false;
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * " +
					"FROM users " +
					"WHERE Username = '"+username+"' AND Password = '"+password+"'");
		
			if (rs.next()) {
				userFound = true;				
			}
			
			rs.close();
			stmt.close();

			return userFound;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return false;			
		}
	}
	
	public final boolean caseHasComment(int id) {
		return (null != requestSingle(Comment.class, "Select * from casecomments where CaseID = %s", id)); 
	}
	
	public final boolean usernameExists(String username) {
		try {
			boolean userFound = false;
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * " +
					"FROM users " +
					"WHERE Username = '"+username+"'");
		
			if (rs.next()) {
				userFound = true;				
			}
			
			rs.close();
			stmt.close();

			return userFound;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return false;			
		}
	}
	
	public final User getUser(String username, String password) {
		try {
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT * " +
					"FROM users " +
					"WHERE Username = '"+username+"' AND Password = '"+password+"'");
		
			rs.next();
			User user = new User(rs);
			
			rs.close();
			stmt.close();

			return user;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return null;			
		}
	}

	public final List<Case> searchByTitle(String title) {

		try {
			
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("Select * " +
				"FROM cases "+
				"WHERE Title LIKE '%" + title + "%'");
		
			final List<Case> cases = new ArrayList<Case>(); 
			while (rs.next()) {
				cases.add(new Case(rs));
			}
			
			rs.close();
			stmt.close();

			return cases;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return null;			
		}
	}
	
	public final List<Case> searchByStatus(String open) {

		try {
			final Statement stmt = this.sqlConnection.createStatement();
			if (open.equalsIgnoreCase("open")) {open = "true";}
			else {open = "false";}
			final ResultSet rs = stmt.executeQuery("Select * " +
				"FROM cases "+
				"WHERE Open = " + open);
		
			final List<Case> cases = new ArrayList<Case>(); 
			while (rs.next()) {
				cases.add(new Case(rs));
			}
			
			rs.close();
			stmt.close();

			return cases;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return null;			
		}
	}
	
	public final List<Case> searchRecent() {
		try {
			// Returns all open cases from the last 6 months ordered by newest first
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT *  FROM cases " + 
			"WHERE date BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 MONTH) AND CURDATE() AND open = 1 "+
			"ORDER BY date DESC");
		
			final List<Case> cases = new ArrayList<Case>(); 
			while (rs.next()) {
				cases.add(new Case(rs));
			}
			
			rs.close();
			stmt.close();

			return cases;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return null;			
		}
	}
	public final List<Case> oldestUnsolved() {
		try {
			// Returns 3 oldest unsolved cases ordered by oldest first
			final Statement stmt = this.sqlConnection.createStatement();
			final ResultSet rs = stmt.executeQuery("SELECT *  FROM cases " + 
			"WHERE open = 1 "+
			"ORDER BY date ASC LIMIT 3");
		
			final List<Case> cases = new ArrayList<Case>(); 
			while (rs.next()) {
				cases.add(new Case(rs));
			}
			
			rs.close();
			stmt.close();

			return cases;
			
		} catch (final SQLException ex) {			
			ex.printStackTrace();
			return null;			
		}
	}

	public List<Person> getAllPersons() {
		return requestList(Person.class, "Select * from personofinterest");
	}

	public Person getPersonById(final int id) {
		return requestSingle(Person.class, "Select * from personofinterest where PersonID = %s", id);
	}

	public List<Comment> getCommentsByPersonId(final int id) {
		return requestList(Comment.class, "Select * from personcomments where PersonID = %s", id);
	}

	public void insertPersonComment(int personID, String username,
			String comment) {
		update("Insert into personcomments (personID, Author, CommentID, Comment )" +
				"values ('%s', '%s', %s, '%s')", personID, username, "null", comment);
	}

	public boolean personHasComment(int id) {
		return (null != requestSingle(Comment.class, "Select * from personcomments where PersonID = %s", id)); 
	}

	public void insertCase(String title, String description, double latitude,
			double longitude) {
		update("Insert into cases (CaseID, Title, Description, Date, Time, Open, Latitude, Longitude)" +
				"values (%s, '%s', '%s', %s, %s, %s, %s, %s)", "null", title, description, "curdate()", "curtime()", 1, latitude, longitude);
	}
}
