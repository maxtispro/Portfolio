package gui.parent;

import dataprocessing.Product;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InfoSection extends BorderPane implements CloseableSection {

	private boolean open = false;
	private boolean editing = false;
	private Product selectedProduct;
	private Product editingProduct;

	private Button openBtn;
	private Button editBtn;
	private Button applyBtn;
	private Button cancelBtn;
	private VBox container;
	private TextArea colorInfoText;
	private TextArea packingInfoText;

	public Product getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(Product p) {
		selectedProduct = p;
	}

	public Product getEditingProduct() {
		return editingProduct;
	}

	public void setEditingProduct(Product p) {
		editingProduct = p;
	}

	public String getColorInfo() {
		return colorInfoText.getText() == null ? "" : colorInfoText.getText();
	}

	public void setColorInfo(String s) {
		colorInfoText.setText(s);
	}

	public String getPackingInfo() {
		return packingInfoText.getText() == null ? "" : packingInfoText.getText();
	}

	public void setPackingInfo(String s) {
		packingInfoText.setText(s);
	}

	public void fillInfo(Product p) {
		if (p == null) {
			setColorInfo("");
			setPackingInfo("");
			return;
		}
		setColorInfo(p.getColors());
		setPackingInfo(p.getPackingInfo());
	}

	public void copyInfoToEditingProduct() {
		getEditingProduct().setColors(getColorInfo());
		getEditingProduct().setPackingInfo(getPackingInfo());
	}

	public boolean isEditing() {
		return editing;
	}

	public boolean isEmpty() {
		return getColorInfo().trim().equals("") && getPackingInfo().trim().equals("");
	}

	public void beforeSwitchingToNewProductAction() {
		if (isEditing()) {
			if (isEmpty())
				cancelBtn.fire();
			else
				applyBtn.fire();
		}
	}

	public void afterSwitchingToNewProductAction() {
		if (isEmpty())
			editBtn.fire();
	}

	public void fireEditButton() {
		editBtn.fire();
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void open() {
		open = true;
		openBtn.setText(">");
		setRight(container);
	}

	@Override
	public void close() {
		open = false;
		openBtn.setText("<");
		setRight(null);
	}

	@Override
	public void setOnOpenButtonAction(EventHandler<ActionEvent> event) {
		openBtn.setOnAction(event);
	}

    /* *************************************************************************
     *                                                                         *
     * Private Methods                                                         *
     *                                                                         *
     **************************************************************************/
	private void startEdit() {
		editing = true;
		colorInfoText.setEditable(true);
		packingInfoText.setEditable(true);
		ObservableList<Node> containerItems = container.getChildren();
		containerItems.remove(editBtn);
		containerItems.add(new HBox(cancelBtn, applyBtn));
	}

	private void stopEdit() {
		editing = false;
		setEditingProduct(getSelectedProduct());
		colorInfoText.setEditable(false);
		packingInfoText.setEditable(false);
		ObservableList<Node> containerItems = container.getChildren();
		containerItems.remove(containerItems.size() - 1);
		containerItems.add(editBtn);
	}

    /* *************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/
	public InfoSection() {
		openBtn = new Button("<");
		editBtn = new Button("Edit");
		applyBtn = new Button("Apply");
		cancelBtn = new Button("Cancel");
		colorInfoText = new TextArea();
		packingInfoText = new TextArea();
		container = new VBox(new Label("Color Info"), colorInfoText, new Label("Packing Info"), packingInfoText, editBtn);

		setCenter(openBtn);

		openBtn.setPrefWidth(20);
		openBtn.setPrefHeight(75);
		openBtn.setStyle("-fx-font-size: 9px;");
		colorInfoText.setEditable(false);
		packingInfoText.setEditable(false);
		packingInfoText.setPrefHeight(125);
		container.setSpacing(10);
		container.setPrefWidth(250);
		container.setStyle("-fx-padding: 10px 20px 10px 10px");

		editBtn.setOnAction(e -> startEdit());
		applyBtn.setOnAction(e -> {
			copyInfoToEditingProduct();
			stopEdit();
		});
		cancelBtn.setOnAction(e -> stopEdit());
	}
}
