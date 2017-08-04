package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a registered in user.
 */
public final class User extends StoreType {

	private final String password;
	private final String username;
	private final String firstname;
	private final String lastname;
	
	public User(final String password, final String username, final String firstname,final String lastname) {
		this.password = password;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public User(final ResultSet rs) throws SQLException {
		this.password = rs.getString("Password");
		this.username = rs.getString("Username");
		this.firstname = rs.getString("Firstname");
		this.lastname = rs.getString("Lastname");
	}

	public String getPassword() {
		return password;
	}
	
	public String getUsername() {
		return username;
	}

	public String getFirstname() {
		return firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
}
