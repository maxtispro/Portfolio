package gui.parent;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

public class Toolbar extends HBox {

	private MenuButton fileMenu;
	private MenuItem saveBtn;
	private MenuItem importBtn;
	private MenuItem exportBtn;

	private MenuButton editMenu;
	private MenuItem editDistributorsBtn;
	
	public void setOnSaveButtonAction(EventHandler<ActionEvent> event) {
		saveBtn.setOnAction(event);
	}
	
	public void setOnEditDistributorButtonAction(EventHandler<ActionEvent> event) {
		editDistributorsBtn.setOnAction(event);
	}
	
	public void setOnImportButtonAction(EventHandler<ActionEvent> event) {
		importBtn.setOnAction(event);
	}
	
	public void setOnExportButtonAction(EventHandler<ActionEvent> event) {
		exportBtn.setOnAction(event);
	}

	public Toolbar() {
		fileMenu = new MenuButton("File");
		saveBtn = new MenuItem("Save");
		importBtn = new MenuItem("Import From CSV");
		exportBtn = new MenuItem("Export To CSV");
		fileMenu.getItems().addAll(saveBtn, importBtn, exportBtn);

		editMenu = new MenuButton("Edit");
		editDistributorsBtn = new MenuItem("Edit Distributors");
		editMenu.getItems().addAll(editDistributorsBtn);

		fileMenu.setId("toolbar-button");
		editMenu.setId("toolbar-button");
		setStyle("-fx-font-size: 9pt;");

		getChildren().addAll(fileMenu, editMenu);
	}
}
