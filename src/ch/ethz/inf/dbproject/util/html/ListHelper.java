package ch.ethz.inf.dbproject.util.html;

import java.util.ArrayList;
import java.util.List;

public class ListHelper<T> extends HtmlHelperIface {

	public interface ListHelperGeneratorFunction<T> {
		String generate(T item);
	}
	
	protected ListHelper() {
		this(null, null);
	}
	
	public ListHelper(ListHelperGeneratorFunction<T> function, String htmlTag) {
		this.function = function;
		this.htmlTag = htmlTag;
		this.items = new ArrayList<T>();
	}
	
	protected final ListHelperGeneratorFunction<T> function;
	protected final String htmlTag;
	protected final List<T> items;
	
	public void addItem(T item) {
		this.items.add(item);
	}
	
	public void addItems(List<T> items) {
		this.items.addAll(items);
	}
	
	@Override
	public String generateHtmlCode() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("<%s>\n", htmlTag));
		for (T item : items) {
			builder.append(function.generate(item));
		}
		builder.append(String.format("</%s>\n", htmlTag));
		return builder.toString();
	}
}
