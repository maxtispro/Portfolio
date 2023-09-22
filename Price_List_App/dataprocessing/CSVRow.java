package dataprocessing;

import java.util.ArrayList;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CSVRow {
	private final ArrayList<StringProperty> properties = new ArrayList<>();
	
	public ArrayList<String> getValues() {
		ArrayList<String> values = new ArrayList<>();
		for (StringProperty sp : properties)
			values.add(sp.getValue());
		return values;
	}
	
	public StringProperty getProperty(int index) {
		return index < properties.size() ? properties.get(index) : null;
	}
	
	public int getPropertyCount() {
		return properties.size();
	}
	
	public CSVRow(String row) {
		int counter = -1;
		StringProperty property;
		for (String s : row.split(Data.SEPARATOR)) {
			property = new SimpleStringProperty(this, "property%d".formatted(++counter));
			property.set(s);
			properties.add(property);
		}
	}
}
