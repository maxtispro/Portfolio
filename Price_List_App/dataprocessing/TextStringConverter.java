package dataprocessing;

import javafx.util.StringConverter;

public class TextStringConverter extends StringConverter<String> {
	@Override
	public String toString(String object) {
		return object;
	}
	@Override
	public String fromString(String string) {
		return string;
	}
}