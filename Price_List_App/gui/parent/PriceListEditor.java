package gui.parent;

import dataprocessing.Data;
import dataprocessing.InvalidProductException;
import dataprocessing.Product;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class PriceListEditor extends BorderPane implements CloseableSection {

	public static final int IMPROPERDATAERROR = -1;
	public static final int PRODUCTEXISTSERROR = -2;
	public static final int NOROWSELECTEDERROR = -3;


    /* *************************************************************************
     *                                                                         *
     * Public Interface                                                        *
     *                                                                         *
     **************************************************************************/
	public void setOnAddButtonAction(EventHandler<ActionEvent> event) {
		addProductsBtn.setOnAction(event);
	}

	public void fireAddButton() {
		addProductsBtn.fire();
	}

	public void setOnInsertButtonAction(EventHandler<ActionEvent> event) {
		insertProductsBtn.setOnAction(event);
	}

	public void setOnDeleteButtonAction(EventHandler<ActionEvent> event) {
		deleteProductsBtn.setOnAction(event);
	}

	public void setOnSortButtonAction(EventHandler<ActionEvent> event) {
		sortProductsBtn.setOnAction(event);
	}


	public ArrayList<Product> getProductsFromTextFields() {
		ArrayList<Product> products = new ArrayList<>();
		Matcher matcher = pattern.matcher(productInfoText.getText());
		int productCount = productInfoText.getText().split("\n").length;
		String manufacturer = manufacturerText.getText();
		String series = seriesText.getText();
		int counter = 0;
		while (matcher.find()) {
			counter++;
			String name = matcher.group(1);
			String[] colorsArray = matcher.group(2).split(Data.SEPARATOR);
			StringBuilder colors = new StringBuilder();
			for (int i = 0; i < colorsArray.length; i++) {
				colors.append(colorsArray[i]);
				if (i < colorsArray.length - 1)
					colors.append("\n");
			}
			String[] packingInfoArray = matcher.group(3).split(Data.SEPARATOR);
			StringBuilder packingInfo = new StringBuilder();
			for (int i = 0; i < packingInfoArray.length; i++) {
				packingInfo.append(packingInfoArray[i]);
				if (i < packingInfoArray.length - 1)
					packingInfo.append("\n");
			}
			Double price = Double.parseDouble(matcher.group(4));
			String unit = matcher.group(5);
			try {
				products.add(new Product(manufacturer, series, name, colors.toString(), packingInfo.toString(), price, unit));
			} catch (InvalidProductException e) {
				products.clear();
				return products;
			}
		}
		if (counter != productCount)
			products.clear();
		return products;
	}

	@Override
	public boolean isOpen() {
		return sectionOpen;
	}

	@Override
	public void open() {
		sectionOpen = true;
		openBtn.setText("v");
		setBottom(this.container);
	}

	@Override
	public void close() {
		sectionOpen = false;
		openBtn.setText("^");
		setBottom(null);
	}

	@Override
	public void setOnOpenButtonAction(EventHandler<ActionEvent> event) {
		openBtn.setOnAction(event);
	}


    /* *************************************************************************
     *                                                                         *
     * Private Fields                                                          *
     *                                                                         *
     **************************************************************************/
	private Pattern pattern;
	
	private boolean sectionOpen = false;
	private GridPane container;
	private TextField manufacturerText;
	private TextField seriesText;
	private TextArea productInfoText;
	private Button openBtn;
	private Button sortProductsBtn;
	private Button addProductsBtn;
	private Button insertProductsBtn;
	private Button deleteProductsBtn;


    /* *************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

	public PriceListEditor() {
		StringBuilder units = new StringBuilder();
		for (int i = 0; i < Data.VALIDUNITS.length; i++) {
			units.append(Data.VALIDUNITS[i]);
			if (i < Data.VALIDUNITS.length - 1)
				units.append("|");
		}
		String b = "\\s*" + Data.SEPARATOR + "\\s*";
		String textGroup = "(.*?)";
		String textListGroup = "\"(.*?)\"";
		String priceGroup = "(\\d*\\.\\d{1,3})";
		String unitsGroup = "(" + units + ")";
		String regex = textGroup + b + textListGroup + b + textListGroup + b + priceGroup + b + unitsGroup;
		pattern = Pattern.compile(regex, Pattern.MULTILINE);

		container = new GridPane();
		manufacturerText = new TextField();
		seriesText = new TextField();
		productInfoText = new TextArea();
		openBtn = new Button("^");
		sortProductsBtn = new Button("Sort");
		addProductsBtn = new Button("Add");
		insertProductsBtn = new Button("Insert");
		deleteProductsBtn = new Button("Delete");

		container.addRow(0, new Label("Manufacturer"), new Label("Series"), new Label("Name, \"Color1, Color2, ...\", \"Packing Info, ...\", List Price, Unit"));
		container.addRow(1, manufacturerText, seriesText, productInfoText);
		setCenter(openBtn);
		VBox buttonPane = new VBox(addProductsBtn, insertProductsBtn, deleteProductsBtn);
		container.add(sortProductsBtn, 3, 0);
		container.add(buttonPane, 3, 1);

		productInfoText.setPrefHeight(125);
		buttonPane.setStyle("-fx-spacing: 10px;");
		openBtn.setPrefHeight(20);
		openBtn.setPrefWidth(75);
		openBtn.setStyle("-fx-font-size: 9px;");
		addProductsBtn.setMinWidth(65);
		insertProductsBtn.setMinWidth(65);
		deleteProductsBtn.setMinWidth(65);
		sortProductsBtn.setMinWidth(65);
		container.setPrefWidth(200);
		container.setStyle("-fx-hgap: 10px; -fx-vgap: 10px; -fx-padding: 10px 20px 10px 10px;");
	}
}
