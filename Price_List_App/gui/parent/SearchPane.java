package gui.parent;

import dataprocessing.Data;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;

public class SearchPane extends HBox {

    /* *************************************************************************
     *                                                                         *
     * Static Methods                                                          *
     *                                                                         *
     **************************************************************************/
	private static ArrayList<MenuItem> createDistributorMenuItems() {
		var arrayList = new ArrayList<MenuItem>();
		for (String name : Data.getDistributorNames())
			arrayList.add(new MenuItem(name));
		return arrayList;
	}

    /* *************************************************************************
     *                                                                         *
     * Private Field GUI Elements                                              *
     *                                                                         *
     **************************************************************************/
	private MenuButton distributorMenu;
	private TextField searchBox;
	private Button searchBtn;
	private Label saveStatusText;
	private Label errorText;

    /* *************************************************************************
     *                                                                         *
     * Public Interface                                                        *
     *                                                                         *
     **************************************************************************/
	public ObservableList<MenuItem> getDistributorMenuItems() {
		return distributorMenu.getItems();
	}
	
	public void setDistributorMenuText(String text) {
		distributorMenu.setText(text);
	}
	
	public void fireMenuItemButton(int n) {
		getDistributorMenuItems().get(n).fire();
	}
	
	public void setOnSearchButtonAction(EventHandler<ActionEvent> event) {
		searchBtn.setOnAction(event);
	}

	public String getSearchWord() {
		return searchBox.getText();
	}

	public String getSelectedDistributor() {
		return distributorMenu.getText();
	}

	public void setSaveStatusText(String s) {
		if (getChildren().indexOf(saveStatusText) < 0) {
			getChildren().remove(errorText);
			getChildren().add(saveStatusText);
		}
		saveStatusText.setText(s);
	}
	
	public void setErrorText(String s) {
		if (getChildren().indexOf(errorText) < 0) {
			getChildren().remove(saveStatusText);
			getChildren().add(errorText);
		}
		errorText.setText(s);
	}
	
	public void clearInfoLabels() {
		saveStatusText.setText("");
	}
	
	public void fireSearchButton() {
		searchBtn.fire();
	}

    /* *************************************************************************
     *                                                                         *
     * Constructor                                                            *
     *                                                                         *
     **************************************************************************/
	public SearchPane() {
		distributorMenu = new MenuButton();
		distributorMenu.getItems().addAll(createDistributorMenuItems());
		searchBox = new TextField();
		searchBtn = new Button("Search");
		saveStatusText = new Label();
		errorText = new Label();

		getChildren().addAll(distributorMenu, searchBox, searchBtn, saveStatusText);

		distributorMenu.setPrefWidth(180);
		errorText.setStyle("-fx-text-fill: red; -fx-font-size: 10pt;");
		setStyle("-fx-alignment: center-left; -fx-padding: 15px; -fx-spacing: 25px;");

		this.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				searchBtn.fire();
				e.consume();
			}
		});
	}
}
