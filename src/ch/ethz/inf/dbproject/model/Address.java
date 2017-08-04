package ch.ethz.inf.dbproject.model;

/**
 * This class represents an Address 
 */
public final class Address {

	private final String country;
	private final String city;
	private final int zipCode;
	private final String street;
	private final int streetNo;
	
	public Address(String country, String city, int zipCode, String street,
			int streetNo) {
		this.country = country;
		this.city = city;
		this.zipCode = zipCode;
		this.street = street;
		this.streetNo = streetNo;
	}

	public String getCountry() {
		return country;
	}

	public String getCity() {
		return city;
	}

	public int getZipCode() {
		return zipCode;
	}

	public String getStreet() {
		return street;
	}

	public int getStreetNo() {
		return streetNo;
	}
}