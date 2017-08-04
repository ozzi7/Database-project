package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Person extends StoreType {

	private final int id;
	private final String firstName;
	private final String lastName;
	private final Date dateOfBirth;
	private final String city;
	private final String country;	
	private final int zipcode;
	private final String street;
	private final int streetNr;
	
	public Person(int id, String firstName, String lastName, Date dateOfBirth,
			String city, String country, int zipcode, String street,
			int streetNr) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.dateOfBirth = dateOfBirth;
		this.city = city;
		this.country = country;
		this.zipcode = zipcode;
		this.street = street;
		this.streetNr = streetNr;
	}

	public Person(ResultSet rs) throws SQLException {
		this.id = rs.getInt("PersonID");
		this.firstName = rs.getString("FirstName");
		this.lastName = rs.getString("LastName");
		this.dateOfBirth = rs.getDate("DateOfBirth");
		this.city = rs.getString("City");
		this.country = rs.getString("Country");
		this.zipcode = rs.getInt("ZipCode");
		this.street = rs.getString("Street");
		this.streetNr = rs.getInt("StreetNo");
	}
	
	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public int getZipcode() {
		return zipcode;
	}

	public String getStreet() {
		return street;
	}

	public int getStreetNr() {
		return streetNr;
	}
}
