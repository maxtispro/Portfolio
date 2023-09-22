package gui.child;

import dataprocessing.Data;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class EditDistributorsPane extends BorderPane {
	
	private TextField newDistributorName;
	private Button addBtn;
	private Button deleteBtn;
	private Alert alert;
	private ListView<String> distributors;
	
	public EditDistributorsPane() {
		alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete this distributor?\nAll data will be permanently deleted.");
		
		newDistributorName = new TextField();
		addBtn = new Button("Add");
		deleteBtn = new Button("Delete");
		deleteBtn.setDisable(true);
		HBox controls = new HBox(newDistributorName, addBtn, deleteBtn);
		controls.setStyle("-fx-spacing: 20px; -fx-padding: 20px; -fx-alignment: center-right;");
		
		distributors = new ListView<>(Data.getDistributorNames());
		
		setTop(controls);
		setCenter(distributors);
		
		setOnMouseClicked(e -> distributors.getSelectionModel().clearSelection());
		
		distributors.getSelectionModel().selectedIndexProperty().addListener(ov -> {
			if (((ReadOnlyIntegerProperty) ov).getValue() >= 0)
				deleteBtn.setDisable(false);
			else
				deleteBtn.setDisable(true);
		});
		
		addBtn.setOnAction(e -> {
			if (newDistributorName.getText().length() > 0) {
				Data.addDistributor(newDistributorName.getText());
				newDistributorName.clear();
			}
		});
		
		setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER)
				addBtn.fire();
		});
		
		deleteBtn.setOnAction(e -> {
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK)
					Data.deleteDistributor(distributors.getSelectionModel().getSelectedItem());
			});
		});
	}
}
