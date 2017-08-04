package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class Case extends StoreType {
	
	private final int id;
	private final String title;
	private final String description;
	private final java.sql.Date date;
	private final java.sql.Time time;
	private final float longitude;
	private final float latitude;
	private final boolean open;
	private final String categoryName;
	
	public Case(final ResultSet rs) throws SQLException {
		this.id = rs.getInt("CaseID");
		this.description = rs.getString("Description");
		this.title = rs.getString("Title");
		this.date = rs.getDate("Date");
		this.time = rs.getTime("Time");
		this.longitude = rs.getFloat("Longitude");
		this.latitude = rs.getFloat("Latitude");
		this.open = rs.getBoolean("Open");
		this.categoryName = rs.getString("CategoryName");
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public java.sql.Date getDate() {
		return date;
	}

	public java.sql.Time getTime() {
		return time;
	}

	public float getLongitude() {
		return longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public boolean getOpen() {
		return open;
	}

	public String getCategoryName() {
		return categoryName;
	}
}