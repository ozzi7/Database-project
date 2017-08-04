package ch.ethz.inf.dbproject.util.html;

import ch.ethz.inf.dbproject.model.Person;
import ch.ethz.inf.dbproject.util.html.ListHelper.ListHelperGeneratorFunction;

public class SelectHelper<T> extends ListHelper<T> {

	public SelectHelper(ListHelperGeneratorFunction<T> function) {
		super(function, "select name=\"personid\"");
	}

}
