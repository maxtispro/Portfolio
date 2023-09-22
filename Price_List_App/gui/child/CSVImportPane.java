package gui.child;

import java.util.ArrayList;

import dataprocessing.CSVRow;
import dataprocessing.Data;
import dataprocessing.InvalidProductException;
import dataprocessing.Product;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TableColumnHeader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CSVImportPane extends BorderPane {
	
	private TextField filePathText;
	private Button browseFileBtn;
	private CheckBox firstRowHeaderCB;
	private Button importBtn;
	
	private ObservableList<LinkedCheckBox> propertySelect;
	private int firstEnabledPropertyIndex = 0;
	
	private TableView<CSVRow> csvDataTable;
	
	public void setOnBrowseFileButtonAction(EventHandler<ActionEvent> event) {
		browseFileBtn.setOnAction(event);
	}
	
	public String getFilePath() {
		return filePathText.getText();
	}
	
	public void setFilePath(String s) {
		filePathText.setText(s);
	}
	
	public CSVImportPane() {
		filePathText = new TextField();
		browseFileBtn = new Button("Browse");
		firstRowHeaderCB = new CheckBox("First Row is Header");
		importBtn = new Button("Import");
		filePathText.setPrefWidth(520);
		
		propertySelect = FXCollections.observableArrayList(new ArrayList<>());
		int cbIndex = 0;
		for (String name : Product.getPropertyNames()) {
			final int n = cbIndex;
			var cb = new LinkedCheckBox(name);
			cb.setSelected(true);
			cb.selectedProperty().addListener(ov -> {
				if (n < firstEnabledPropertyIndex)
					firstEnabledPropertyIndex = n;
			});
			propertySelect.add(cb);
			cbIndex++;
		}
		
		HBox searchFile = new HBox(new Label("File: "), filePathText, browseFileBtn);
		searchFile.setStyle("-fx-spacing: 20px; -fx-alignment: center; -fx-width: 100%;");
		HBox loadOptions = new HBox(firstRowHeaderCB, importBtn);
		loadOptions.setStyle("-fx-spacing: 20px; -fx-alignment: center-right; -fx-width: 100%;");
		FlowPane properties = new FlowPane();
		properties.getChildren().addAll(propertySelect);
		properties.setStyle("-fx-hgap: 20px; -fx-vgap: 10px;");
		VBox header = new VBox(searchFile, loadOptions, new Label("Properties to Import"), properties);
		header.setStyle("-fx-spacing: 20px; -fx-padding: 0 0 20px 0;");
		
		csvDataTable = new TableView<>();
		csvDataTable.setStyle("-fx-padding: 10px;");
		csvDataTable.setEditable(true);
		csvDataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		csvDataTable.getSelectionModel().setCellSelectionEnabled(true);
		csvDataTable.setPlaceholder(new Label("Load Data"));
		
		setTop(header);
		setCenter(csvDataTable);
		setStyle("-fx-padding: 20px;");
		
		firstRowHeaderCB.setOnAction(e -> {
			if (!filePathText.getText().equals(""))
				loadFile();
		});
		importBtn.setOnAction(e -> {
			var products = createProducts();
			if (products != null)
				System.out.println(products.get(0));
			else
				System.out.println("No Products Created");
		});
	}
	
	public void loadFile() {
		csvDataTable.getColumns().clear();
		ObservableList<CSVRow> items = Data.readCSV(getFilePath());
		if (items == null)
			return;
		csvDataTable.setItems(items);
		int propertyCount = csvDataTable.getItems().get(0).getPropertyCount();
		ArrayList<String> propertyNames = items.get(0).getValues();
		if (firstRowHeaderCB.isSelected())
			items.remove(0);
		
		for (int i = 0; i < propertyCount; i++) {
			final var columnHeader = new LinkedCheckBox();
			final TableColumn<CSVRow, String> column = new TableColumn<>();
			if (firstRowHeaderCB.isSelected())
				columnHeader.setText(propertyNames.get(i));
			else
				columnHeader.setText("Property %d".formatted(i+1));
			final int n = i;
			column.setGraphic(columnHeader);
			column.setCellValueFactory(p -> p.getValue().getProperty(n));
			column.setPrefWidth(150);
			column.setSortable(false);
			csvDataTable.getColumns().add(column);
			
			columnHeader.setOnAction(ev -> {
				if (columnHeader.isSelected()) {
					while (firstEnabledPropertyIndex < propertySelect.size() && (!propertySelect.get(firstEnabledPropertyIndex).isSelected() || propertySelect.get(firstEnabledPropertyIndex).hasLink()))
						firstEnabledPropertyIndex++;
					if (firstEnabledPropertyIndex == propertySelect.size()) {
						columnHeader.setSelected(false);
						return;
					}
					
					csvDataTable.getSelectionModel().selectRange(0, column, csvDataTable.getItems().size() - 1, column);
					columnHeader.setLink(propertySelect.get(firstEnabledPropertyIndex));
					propertySelect.get(firstEnabledPropertyIndex).setDisable(true);
					
				} else {
					int j;
					for (j = 0; j < csvDataTable.getItems().size(); j++)
						csvDataTable.getSelectionModel().clearSelection(j, column);
					
					for (var cb : propertySelect) {
						if (columnHeader.link == cb) {
							columnHeader.link.setDisable(false);
							columnHeader.removeLink();
							break;
						}
					}
					
					for (j = 0; j < propertySelect.size(); j++) {
						if (!propertySelect.get(j).isDisabled()) {
							firstEnabledPropertyIndex = j;
							break;
						}
					}
				}
			});
		}
	}
	
	public ObservableList<Product> createProducts() {
		ArrayList<Product> products = new ArrayList<>();
		String pricePropertyName = (new Product()).listPriceProperty().getName();
		for (int row = 0; row < csvDataTable.getItems().size(); row++) {
			ArrayList<String> productInfo = new ArrayList<>();
			for (var cb : propertySelect) {
				if (cb.hasLink()) {
					var column = ((TableColumnHeader)cb.link.getParent().getParent()).getTableColumn();
					productInfo.add(column.getCellData(row).toString());
				} else {
					if (cb.getText().equals(pricePropertyName))
						productInfo.add("0.0");
					else
						productInfo.add("");
				}
			}
			try {
				products.add(new Product(productInfo));
			} catch (InvalidProductException e) {
				System.err.println("Error: product info %s is invalid and could not create a proper product. Error from reading csv row data in the import window.".formatted(productInfo));
				System.err.println(e.getMessage());
				return null;
			}
		}
		return FXCollections.observableArrayList(products);
	}
	
	private class LinkedCheckBox extends CheckBox {
		
		private LinkedCheckBox link;
		
		public boolean hasLink() {
			return link == null ? false : true;
		}
		
		public void setLink(LinkedCheckBox cb) {
			if (cb == null)
				return;
			link = cb;
			cb.link = this;
		}
		
		public void removeLink() {
			link.link = null;
			link = null;
		}
		
		public LinkedCheckBox(String text, LinkedCheckBox link) {
			setText(text);
			setLink(link);
		}
		
		public LinkedCheckBox() {
			this("", null);
		}
		
		public LinkedCheckBox(String text) {
			this(text, null);
		}
		
		public LinkedCheckBox(LinkedCheckBox link) {
			this("", link);
		}
	}
}
