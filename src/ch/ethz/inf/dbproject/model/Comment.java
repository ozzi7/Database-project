package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a user comment.
 */
public class Comment extends StoreType {

	private final String username;
	private final String comment;
	
	public Comment(final String username, final String comment) {
		this.username = username;
		this.comment = comment;
	}

	public Comment(final ResultSet rs) throws SQLException {
		this.username = rs.getString("Author");
		this.comment = rs.getString("Comment");
	}

	public String getUsername() {
		return username;
	}

	public String getComment() {
		return comment;
	}	
}
