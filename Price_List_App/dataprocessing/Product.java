package dataprocessing;

import java.util.ArrayList;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Product implements Comparable<Product> {

    /* *************************************************************************
     *                                                                         *
     * Static Methods                                                          *
     *                                                                         *
     **************************************************************************/
	private static boolean isUnitValid(String unit) {
		for (String s : Data.VALIDUNITS)
			if (s.equalsIgnoreCase(unit))
				return true;
		return false;
	}

//	public static Product fromProductLine(String productLine) throws InvalidProductException {
//		String[] info = productLine.split(Data.SEPARATOR);
//		try {
//			String manufacturer = info[0];
//			String series = info[1];
//			String productName = info[2];
//			String colorInfo = info[3];
//			String packingInfo = info[4];
//			Double listPrice = Double.parseDouble(info[5]);
//			String unit = info[6];
//			return new Product(manufacturer, series, productName, colorInfo, packingInfo, listPrice, unit);
//		} catch (NumberFormatException e) {
//			throw new InvalidProductException("Invalid price format for product %s".formatted(productLine));
//		} catch (IndexOutOfBoundsException e) {
//			throw new InvalidProductException("Invalid product line format for product %s".formatted(productLine));
//		}
//	}

	private static final ArrayList<String> propertyNames;
	@SuppressWarnings("rawtypes")
	public static ArrayList<String> getPropertyNames() {
		return propertyNames;
	}
	
	public static int getPropertyCount() {
		return propertyNames.size();
	}
	
	static {
		Product p = new Product();
		propertyNames = new ArrayList<String>();
		for (ObservableValue property : p.properties)
			propertyNames.add(((Property) property).getName());
	}

    /* *************************************************************************
     *                                                                         *
     * Public Interface                                                        *
     *                                                                         *
     **************************************************************************/
	public boolean hasKeyWord(String word) {
		word = word.toLowerCase();
		String manufacturer = getManufacturer().toLowerCase();
		String series = getSeries().toLowerCase();
		String name = getName().toLowerCase();
		String colors = getColors().toLowerCase();
		if (manufacturer.contains(word) || series.contains(word) || name.contains(word) || colors.contains(word))
			return true;
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setPropertyValue(String propertyName, Object value) {
		for (ObservableValue property : properties) {
			if (((Property) property).getName().equals(propertyName))
				((Property) property).setValue(value);
		}
	}

	@SuppressWarnings("rawtypes")
	public Object getPropertyValue(String propertyName) {
		for (ObservableValue property : properties) {
			if (((Property) property).getName().equals(propertyName))
				return property.getValue();
		}
		return null;
	}


	public StringProperty manufacturerProperty() {
		return manufacturer;
	}
	public String getManufacturer() {
		return manufacturer.getValue();
	}
	public void setManufacturer(String s) {
		manufacturer.set(s);
	}


	public StringProperty seriesProperty() {
		return series;
	}
	public String getSeries() {
		return series.getValue();
	}
	public void setSeries(String s) {
		series.set(s);
	}


	public StringProperty nameProperty() {
		return name;
	}
	public String getName() {
		return name.getValue();
	}
	public void setName(String s) {
		name.set(s);
	}


	public StringProperty colorsProperty() {
		return colors;
	}
	public String getColors() {
		return colors.getValue();
	}
	public void setColors(String s) {
		colors.set(s);
	}


	public StringProperty unitProperty() {
		return unit;
	}
	public String getUnit() {
		return unit.getValue();
	}
	public void setUnit(String s) throws InvalidProductException {
		if (!isUnitValid(s))
			throw new InvalidProductException("Invalid unit %s".formatted(s));
		unit.set(s);
	}


	public DoubleProperty listPriceProperty() {
		return listPrice;
	}
	public double getListPrice() {
		return listPrice.getValue();
	}
	public void setListPrice(double price) {
		listPrice.set(price);
	}


	public DoubleProperty salesPriceProperty() {
		return salesPrice;
	}
	public double getSalesPrice() {
		return salesPrice.getValue();
	}
	public void salesPriceBinding(DoubleProperty property) {
		salesPrice.bind(property.multiply(1.7));
	}


	public DoubleProperty contractorPriceProperty() {
		return contractorPrice;
	}
	public double getContractorPrice() {
		return contractorPrice.getValue();
	}
	public void contractorPriceBinding(DoubleProperty property) {
		contractorPrice.bind(property.multiply(1.5));
	}
	
	public StringProperty packingInfoProperty() {
		return packingInfo;
	}
	public String getPackingInfo() {
		return packingInfo.getValue();
	}
	public void setPackingInfo(String s) {
		packingInfo.set(s);
	}

	@Override
	public int compareTo(Product p) {
		int m = this.getManufacturer().compareTo(p.getManufacturer());
		if (m == 0) {
			int n = this.getSeries().compareTo(p.getSeries());
			if (n == 0)
				return this.getName().compareTo(p.getName());
			return n;
		}
		return m;
	}
	
	@Override
	public boolean equals(Object o) {
		return hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		return (getManufacturer() + getSeries() + getName() + getColors() + getUnit() + getPackingInfo()).hashCode();
	}

    /* *************************************************************************
     *                                                                         *
     * Private Field Binding Properties                                        *
     *                                                                         *
     **************************************************************************/
	private StringProperty manufacturer = new SimpleStringProperty(this, "Manufacturer");
	private StringProperty series = new SimpleStringProperty(this, "Series");
	private StringProperty name = new SimpleStringProperty(this, "Name");
	private StringProperty colors = new SimpleStringProperty(this, "Colors");
	private StringProperty unit = new SimpleStringProperty(this, "Unit") {
		@Override
		public void setValue(String s) {
			if (isUnitValid(s))
				set(s);
		}
	};
	private DoubleProperty listPrice = new SimpleDoubleProperty(this, "List Price");
	private DoubleProperty salesPrice = new SimpleDoubleProperty(this, "Sales Price");
	private DoubleProperty contractorPrice = new SimpleDoubleProperty(this, "Contractor Price");
	private StringProperty packingInfo = new SimpleStringProperty(this, "Packing Info");
	
	@SuppressWarnings("rawtypes")
	private ObservableList<ObservableValue> properties;

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
	public Product() {
		setManufacturer("");
		setSeries("");
		setName("");
		setColors("");
		try {
			setUnit("");
		} catch (InvalidProductException e) {
			System.err.println("Warning: empty string must be part of the valid units array");
		}
		setListPrice(0.0);
		salesPriceBinding(listPriceProperty());
		contractorPriceBinding(listPriceProperty());
		setPackingInfo("");
		
		properties = FXCollections.observableArrayList(manufacturer, series, name, colors, listPrice, unit, packingInfo);
	}

	public Product(String manufacturer, String series, String name, String colors, String packingInfo, double listPrice, String unit)
			throws InvalidProductException {
		this();
		setManufacturer(manufacturer);
		setSeries(series);
		setName(name);
		setColors(colors);
		setPackingInfo(packingInfo);
		setUnit(unit);
		setListPrice(listPrice);
		salesPriceBinding(listPriceProperty());
		contractorPriceBinding(listPriceProperty());
	}
	
	public Product (String productLine) throws InvalidProductException {
		this();
		String[] info = productLine.split(Data.SEPARATOR);
		try {
			setManufacturer(info[0]);
			setSeries(info[1]);
			setName(info[2]);
			setColors(info[3]);
			setPackingInfo(info[4]);
			if (info[5].contains("$"))
				setListPrice(Double.parseDouble(info[5].substring(info[5].indexOf("$")+1)));
			else
				setListPrice(Double.parseDouble(info[5]));
			setUnit(info[6]);
			salesPriceBinding(listPriceProperty());
			contractorPriceBinding(listPriceProperty());
		} catch (NumberFormatException e) {
			throw new InvalidProductException("Invalid price format for product %s".formatted(productLine));
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidProductException("Invalid product line format for product %s".formatted(productLine));
		}
	}
	
	public Product(ArrayList<String> info) throws InvalidProductException {
		this();
		try {
			setManufacturer(info.get(0));
			setSeries(info.get(1));
			setName(info.get(2));
			setColors(info.get(3));
			if (info.get(4).contains("$"))
				setListPrice(Double.parseDouble(info.get(4).substring(info.get(4).indexOf("$")+1)));
			else
				setListPrice(Double.parseDouble(info.get(4)));
			setUnit(info.get(5));
			setPackingInfo(info.get(6));
			salesPriceBinding(listPriceProperty());
			contractorPriceBinding(listPriceProperty());
		} catch (NumberFormatException e) {
			throw new InvalidProductException("Invalid price format for product %s".formatted(info));
		} catch (IndexOutOfBoundsException e) {
			throw new InvalidProductException("Invalid product info for product %s".formatted(info));
		}
	}
}
