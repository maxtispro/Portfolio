package gui.parent;

import dataprocessing.Product;
import dataprocessing.TextStringConverter;
import dataprocessing.Data;
import dataprocessing.PriceStringConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public class PriceList extends TableView<Product> {

    /* *************************************************************************
     *                                                                         *
     * Public Interface                                                        *
     *                                                                         *
     **************************************************************************/
	public void generateProducts(String distributorName) {
		setItems(Data.getProducts(distributorName));
	}

	public Product getLastSelectedProduct() {
		return getSelectionModel().getSelectedItem();
	}

	public void sortProductsInAlphaOrder() {
		Collections.sort(getItems());
	}

	public void findMatchingProducts(String keyword) {
		getSelectionModel().clearSelection();
		for (Product p : getItems()) {
			if (p.hasKeyWord(keyword))
				getSelectionModel().select(p);
		}
		if (getSelectionModel().getSelectedIndices().size() > 0)
			scrollTo(getSelectionModel().getSelectedIndices().get(0));
	}


	public int insertProductsInAlphaOrder(ArrayList<Product> products) {
		if (products.isEmpty())
			return PriceListEditor.IMPROPERDATAERROR;
		Collections.sort(products);
		for (Product p : products) {
			if (getItems().indexOf(p) != -1)
				return PriceListEditor.PRODUCTEXISTSERROR;
			getItems().add(findAlphaOrderIndexOf(p), p);
		}
		return 0;
	}


	public int insertProductsAboveLastSelection(ArrayList<Product> products) {
		int index = getSelectionModel().getSelectedIndex();
		if (index < 0)
			return PriceListEditor.NOROWSELECTEDERROR;
		if (products.isEmpty())
			return PriceListEditor.IMPROPERDATAERROR;
		for (Product p : products) {
			if (getItems().indexOf(p) != -1)
				return PriceListEditor.PRODUCTEXISTSERROR;
			getItems().add(index, p);
		}
		return 0;
	}


	public void deleteSelectedProducts() {
		getItems().removeAll(getSelectionModel().getSelectedItems());
	}


    /* *************************************************************************
     *                                                                         *
     * Private Methods                                                         *
     *                                                                         *
     **************************************************************************/
	private int findAlphaOrderIndexOf(Product p) {
		Iterator<Product> tableItemsIter = getItems().iterator();
		int index = 0;
		while (tableItemsIter.hasNext() && p.compareTo(tableItemsIter.next()) >= 0)
			index++;
		return index;
	}


    /* *************************************************************************
     *                                                                         *
     * Constructor                                                            *
     *                                                                         *
     **************************************************************************/
	@SuppressWarnings("unchecked")
	public PriceList() {
		/*
		 * Creating the table columns: setCellValueFactory() specifies where the values
		 * for each column come from setCellFactory() specifies how the values are
		 * displayed p.getValue returns the Product instance of a particular TableView
		 * StringConverter: abstract class defining structure of conversion between
		 * 	objects and strings
		 * 	- used to validated user input data into editable table cells
		 */
		/*idCol.setCellFactory(new Callback<TableColumn<Product, String>, TableCell<Product, String>>() {
		public TableCell<Product, String> call(TableColumn<Product, String> p) {
			TableCell<Product, String> cell = new TextFieldTableCell<Product, String>(new TextStringConverter());

			// define cell style
			return cell;
		}});*/

		setStyle("-fx-padding: 10px;");
		setEditable(true);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		setPlaceholder(new Label("No products to display"));


		TableColumn<Product, String> manufacturerColumn = new TableColumn<>("Manufacturer");
		manufacturerColumn.setCellValueFactory(p -> p.getValue().manufacturerProperty());
		manufacturerColumn.setCellFactory(p -> new TextFieldTableCell<Product, String>(new TextStringConverter()));
		manufacturerColumn.setPrefWidth(110);


		TableColumn<Product, String> seriesColumn = new TableColumn<Product, String>("Series");
		seriesColumn.setCellValueFactory(p -> p.getValue().seriesProperty());
		seriesColumn.setCellFactory(p -> new TextFieldTableCell<Product, String>(new TextStringConverter()));


		TableColumn<Product, String> productColumn = new TableColumn<Product, String>("Product");
		productColumn.setCellValueFactory(p -> p.getValue().nameProperty());
		productColumn.setCellFactory(p -> new TextFieldTableCell<Product, String>(new TextStringConverter()));
		productColumn.setPrefWidth(180);


		TableColumn<Product, Number> listPriceColumn = new TableColumn<Product, Number>("List Price");
		listPriceColumn.setCellValueFactory(p -> p.getValue().listPriceProperty());
		listPriceColumn.setCellFactory(p -> {
			var cell = new TextFieldTableCell<Product, Number>(new PriceStringConverter());
			cell.setStyle("-fx-alignment: CENTER-RIGHT;");
			return cell;
		});


		TableColumn<Product, String> unitColumn = new TableColumn<Product, String>("Unit");
		unitColumn.setCellValueFactory(p -> p.getValue().unitProperty());
		unitColumn.setCellFactory(p -> new TextFieldTableCell<Product, String>(new TextStringConverter()));


		TableColumn<Product, Number> salesPriceColumn = new TableColumn<Product, Number>("Sales Price");
		salesPriceColumn.setCellValueFactory(p -> p.getValue().salesPriceProperty());
		salesPriceColumn.setCellFactory(p -> {
			TableCell<Product, Number> cell = new TextFieldTableCell<Product, Number>(new PriceStringConverter());
			cell.setEditable(false);
			cell.setStyle("-fx-alignment: CENTER-RIGHT;");
			return cell;
		});
		salesPriceColumn.setPrefWidth(90);


		TableColumn<Product, Number> contractorPriceColumn = new TableColumn<Product, Number>("Contractor Price");
		contractorPriceColumn.setCellValueFactory(p -> p.getValue().contractorPriceProperty());
		contractorPriceColumn.setCellFactory(p -> {
			TableCell<Product, Number> cell = new TextFieldTableCell<Product, Number>(new PriceStringConverter());
			cell.setEditable(false);
			cell.setStyle("-fx-alignment: CENTER-RIGHT;");
			return cell;
		});
		contractorPriceColumn.setPrefWidth(120);


		this.getColumns().addAll(manufacturerColumn, seriesColumn, productColumn, listPriceColumn, unitColumn, salesPriceColumn,
				contractorPriceColumn);
	}
}