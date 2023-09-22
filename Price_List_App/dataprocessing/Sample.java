package dataprocessing;

import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

public class Sample extends VBox {

	private String name;
	private int[] sampleIDs;
	private final ObservableList<Product> products;

    /* *************************************************************************
     *                                                                         *
     * Public INterface                                                        *
     *                                                                         *
     **************************************************************************/
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void addProducts(Product...products) {
		for (Product p : products)
			this.products.add(p);
	}
	
	public void removeProducts(Product...products) {
		for (Product p : products)
			this.products.remove(p);
	}
	
	public void setProducts(Product...products) {
		this.products.clear();
		for (Product p : products)
			this.products.add(p);
	}
	
	public void setProducts(ObservableList<Product> products) {
		this.products.clear();
		for (Product p : products)
			this.products.add(p);
	}

    /* *************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
	public Sample() {
		this("", null);
	}

	public Sample(String name, ObservableList<Product> products) {
		setName(name);
		this.products = products;
	}
}
