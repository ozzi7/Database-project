package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Object that represents a category of project (i.e. Theft, Assault...) 
 */
public final class Category extends StoreType {

	private final String name;
	private final String containedIn;

	public Category(final String name, final String containedIn) {
		super();
		this.name = name;
		this.containedIn = containedIn;
	}
	
	public Category(ResultSet rs) throws SQLException {
		this.name = rs.getString("CategoryName");
		this.containedIn = rs.getString("ContainedIn");
	}

	public final String getName() {
		return name;
	}
	
	public final String getContainedIn() {
		return containedIn;
	}
}
