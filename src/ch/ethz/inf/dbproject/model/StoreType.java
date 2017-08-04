package ch.ethz.inf.dbproject.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class StoreType {
	
	public StoreType() {}
	
	public StoreType(ResultSet rs) throws SQLException { }	
}
