package ch.ethz.inf.dbproject.model;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Date;
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
		
			final boolean empty = !rs.next();
			T result = null;
			try {
				if (!empty)
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
		update("UPDATE cases SET open = 1 WHERE caseID = %s", caseID);
	}
	
	public final void closeCase(int caseID) {
		update("UPDATE cases SET open = 0 WHERE caseID = %s", caseID);
		update("insert into convictions (PersonID, Reason, Start, End) " +
				"select t1.PersonID, t2.CategoryName, Curdate(), DATE_ADD(curdate(), INTERVAL 1 YEAR) " +
				"from associatedWith t1, cases t2 where t1.caseID = %s and t1.isConvicted = 0", caseID);
		update("update associatedWith set isconvicted = 1 where caseID = %s",caseID);
	}
	
	public final void insertCaseComment(int caseID, String username, String comment) {
		update("Insert into casecomments (CaseID, Author, CommentID, Comment )" +
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
		return requestList(Case.class, "Select * FROM cases "+
				"WHERE Title LIKE '%%%s%%'",title);
	}
	public final List<Person> getPersonsByNames(String firstName, String lastName) {
		return requestList(Person.class, "Select * from persons where FirstName like '%%%s%%' and " + 
				"LastName like '%%%s%%'", firstName, lastName);
	}
	
	// for string values
	public void editPersonEntry(String personID, String attributeName, String newValue) {
		if (attributeName.equals("firstName") || attributeName.equals("lastName") || attributeName.equals("city")
				|| attributeName.equals("street") || attributeName.equals("country")) {
			update("UPDATE persons SET %s = '%s' where personID = %s",attributeName, newValue, personID);
		}
		else if(attributeName.equals("ZipCode") || attributeName.equals("StreetNo") || attributeName.equals("date")) {
			update("UPDATE persons SET %s = %s where personID = %s",attributeName, newValue, personID);
		}
	}
	
	public void insertPerson(String firstName,String lastName,String dateOfBirth, String zipCode,
			String streetNo,String street,String country,String city) {
		update("insert into persons (FirstName,LastName,DateOfBirth,Country,City,ZipCode,Street,StreetNo) values ('%s','%s','%s','%s','%s',%s,'%s',%s)",
				firstName,lastName,dateOfBirth,country,city,zipCode,street,streetNo);
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
	
	/**
	 * 1 subcategory
	 * @param category
	 * @return
	 */
	public final List<Case> getCasesPerCategory(String category) {
		return requestList(Case.class, "Select * from cases where CategoryName in" + 
				"(select distinct CategoryName from categories where categoryName = '%s' or " +
				"categories.containedIn = '%s')", category, category);
	}
	// Returns all open cases from the last 6 months ordered by newest first
	public final List<Case> searchRecent() {
		return requestList(Case.class, "SELECT *  FROM cases " + 
			"WHERE date BETWEEN DATE_SUB(CURDATE(), INTERVAL 6 MONTH) AND CURDATE() AND open = 1 "+
			"ORDER BY date DESC");
	}		
	// Returns 3 oldest unsolved cases ordered by oldest first
	public final List<Case> oldestUnsolved() {
		return requestList(Case.class, "SELECT *  FROM cases " + 
			"WHERE open = 1 ORDER BY date ASC LIMIT 3");
	}

	public List<Person> getAllPersons() {
		return requestList(Person.class, "Select * from persons");
	}

	public Person getPersonById(final int id) {
		return requestSingle(Person.class, "Select * from persons where PersonID = %s", id);
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

	public void insertCase(String title, String description, float latitude, float longitude, String categoryName) {
		update("Insert into cases (CaseID, Title, Description, Date, Time, Latitude, Longitude, Open, CategoryName)" +
				"values (%s, '%s', '%s', %s, %s, %s, %s, %s, '%s')", "null", title, description, "curdate()", "curtime()", latitude, longitude, 1, categoryName);
	}

	public boolean isCaseOpen(int caseID) {
		return requestSingle(Case.class, "Select * from cases where CaseID = %s", caseID).getOpen();
	}

	public List<Person> getAllSuspects(int caseID) {
		return requestList(Person.class, "Select * from persons where PersonID in" +
				"(select PersonID from associatedwith where CaseID = %s and isConvicted = 0)", caseID);
	}

	public List<Person> getAllConvicted(int caseID) {
		return requestList(Person.class, "Select * from persons where PersonID in" +
				"(select PersonID from associatedwith where CaseID = %s and isConvicted = 1)", caseID);
	}

	public List<Person> getLinkablePersons(int caseID) {
		return requestList(Person.class, "Select * from persons where PersonID not in" +
				"(select PersonID from associatedwith where CaseID = %s)", caseID);
	}

	public void linkPerson(int caseID, int personID) {
		update("insert into associatedwith (CaseID, PersonID, isConvicted) " +
				"values (%s, %s, 0)", caseID, personID);
	}

	public void unlinkPerson(int caseID, int personID) {
		update("delete from associatedwith where CaseID = %s and PersonID = %s", caseID, personID);
	}

	public List<Person> getPersonsByDateOfConviction(Date date) {
		return requestList(Person.class, "select * from persons where PersonID in " +
				"(select PersonID from convictions where Start = '%s')", date.toString());
	}

	public List<Person> getPersonsByReasonOfConviction(String reason) {
		return requestList(Person.class, "select * from persons where PersonID in " +
				"(select PersonID from convictions where Reason = '%s')", reason);
	}
}
