package dataprocessing;

import java.text.NumberFormat;
import java.text.ParseException;

import javafx.util.StringConverter;

public class PriceStringConverter extends StringConverter<Number> {
	private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

	@Override
	public String toString(Number price) {
		return currencyFormat.format(price);
	}
	@Override
	public Number fromString(String string) {
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException e) {
			try {
				return currencyFormat.parse(string);
			} catch (ParseException ex) {
				return 0.0;
			}
		}
	}
}