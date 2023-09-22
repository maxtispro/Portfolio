/* 
 * Name:			Maxim Tishchenko
 * Email:			maxtispro@gmail.com
 * Mobile:			(413) 687-1508
 * Date Created:	04-20-2023
 * Version:			1.0.0 Beta
 * Description:		Application for searching and modifying price lists.
 */

package gui.parent;

import dataprocessing.Data;
import dataprocessing.Product;
import gui.child.ChildWindow;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TablePosition;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomeWindow extends Application {

	private AppGUI gui = new AppGUI();
	private Alert alert;
	
	private ChildWindow childWindow;

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(gui, 900, 600);
		primaryStage.setTitle("Price Tool");
		scene.getStylesheets().add("appstyle.css");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinWidth(500);
		primaryStage.setMinHeight(300);

		alert = new Alert(AlertType.CONFIRMATION, "Do you want to save before exiting?");
		childWindow = new ChildWindow(primaryStage);

		final var ctrl_s = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
		scene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			if (ctrl_s.match(e))
				gui.saveData();
		});

		scene.setOnKeyPressed(e -> {
			if (e.getCode().equals(KeyCode.ALT))
				gui.toggleToolbar();
		});
		
		gui.toolbar.setOnEditDistributorButtonAction(e -> {
			childWindow.openEditDistributors();
		});
		
		gui.toolbar.setOnImportButtonAction(e -> {
			childWindow.openCSVImport();
		});
		
		gui.toolbar.setOnExportButtonAction(e -> {
			childWindow.openCSVExport();
		});
	}

	@Override
	public void stop() {
		if (Data.isChanged()) {
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.OK)
					gui.saveData();
			});
		}
	}

	public static void main(String[] args) {
		launch(args);
	}


	private class AppGUI extends BorderPane {

		Toolbar toolbar;

//		TabPane tabPane;
//		Tab priceListTab;
//		Tab sampleListTab;

		SearchPane searchPricesTool;
		PriceList priceListTable;
		PriceListEditor priceListEditor;
		InfoSection priceListInfo;

//		SearchPane searchSamplesTool;
//		SamplesList samplesCardList;
//		SamplesListEditor sampleListEditor;
//		InfoSection sampleListInfo;

		PauseTransition pauseTransition;


		public void saveData() {
			if (Data.isChanged()) {
				if (Data.saveAll())
					searchPricesTool.setSaveStatusText("Saved");
				else
					searchPricesTool.setSaveStatusText("Error Saving");
			} else {
				searchPricesTool.setSaveStatusText("Nothing to Save");
			}
			pauseTransition.play();
		}


		public void toggleToolbar() {
			ObservableList<Node> topChildren = ((VBox) getTop()).getChildren();
			if (topChildren.get(0) != toolbar)
				topChildren.add(0, toolbar);
			else
				topChildren.remove(toolbar);
		}


		public AppGUI() {

			pauseTransition = new PauseTransition(Duration.seconds(3));
			pauseTransition.setOnFinished(e -> searchPricesTool.clearInfoLabels());

			toolbar = new Toolbar();

			searchPricesTool = new SearchPane();
			priceListTable = new PriceList();
			priceListEditor = new PriceListEditor();
			priceListInfo = new InfoSection();
			setTop(new VBox(toolbar, searchPricesTool));
			setCenter(priceListTable);
			setRight(priceListInfo);
			setBottom(priceListEditor);
			
//			priceListTab = new Tab("Prices");
//			priceListTab.setContent(new BorderPane(priceListTable, searchPricesTool, priceListInfo, priceListEditor, null));
//			priceListTab.setClosable(false);
//
//			searchSamplesTool = new SearchPane();
//			samplesCardList = new SamplesList();
//			sampleListEditor = new SamplesListEditor();
//			sampleListInfo = new InfoSection();
//			sampleListTab = new Tab("Samples");
//			sampleListTab.setContent(new BorderPane(samplesCardList, searchSamplesTool, sampleListInfo, sampleListEditor, null));
//			sampleListTab.setClosable(false);
//
//			tabPane = new TabPane(priceListTab, sampleListTab);
//			setCenter(tabPane);


			// reset mouse focus
			setOnMousePressed(e -> requestFocus());
			
			
			toolbar.setOnSaveButtonAction(e -> saveData());


			searchPricesTool.getDistributorMenuItems().forEach(menuItem -> {
				menuItem.setOnAction(event -> {
					if (!priceListInfo.isEditing())
						priceListInfo.fillInfo(null);
					searchPricesTool.setDistributorMenuText(menuItem.getText());
					priceListTable.generateProducts(menuItem.getText());
				});
			});
			searchPricesTool.fireMenuItemButton(0);

			searchPricesTool.setOnSearchButtonAction(e -> priceListTable.findMatchingProducts(searchPricesTool.getSearchWord()));
			searchPricesTool.setOnKeyPressed(e -> {
				if (e.getCode().equals(KeyCode.ENTER)) {
					searchPricesTool.fireSearchButton();
					e.consume();
				}
			});


			priceListTable.getFocusModel().focusedCellProperty().addListener(new ChangeListener<TablePosition>() {
				@Override
				public void changed(ObservableValue<? extends TablePosition> observable, TablePosition oldValue,
						TablePosition newValue) {
					int row = newValue.getRow();
					if (row >= 0) {
						priceListInfo.beforeSwitchingToNewProductAction();
						Product p = priceListTable.getItems().get(row);
						priceListInfo.setSelectedProduct(p);
						if (!priceListInfo.isEditing()) {
							priceListInfo.setEditingProduct(p);
							priceListInfo.fillInfo(p);
						}
						priceListInfo.afterSwitchingToNewProductAction();
					}
				}
			});

			priceListInfo.setOnOpenButtonAction(e -> {
				if (priceListInfo.isOpen())
					priceListInfo.close();
				else {
					priceListInfo.open();
					if (priceListEditor.isOpen())
						priceListEditor.close();
				}
			});

			priceListEditor.setOnOpenButtonAction(e -> {
				if (priceListEditor.isOpen())
					priceListEditor.close();
				else {
					priceListEditor.open();
					if (priceListInfo.isOpen())
						priceListInfo.close();
				}
			});

			priceListEditor.setOnAddButtonAction(e -> {
				searchPricesTool.setErrorText("");
				int errorCode = priceListTable.insertProductsInAlphaOrder(priceListEditor.getProductsFromTextFields());
				if (errorCode == 0)
					return;
				switch (errorCode) {
				case PriceListEditor.IMPROPERDATAERROR -> {
					searchPricesTool.setErrorText("Improper data format");
				}
				case PriceListEditor.PRODUCTEXISTSERROR -> {
					searchPricesTool.setErrorText("Product(s) already exist");
				}
				default -> {
					searchPricesTool.setErrorText("Unexpected error");
				}
				}
			});

			priceListEditor.setOnInsertButtonAction(e -> {
				searchPricesTool.setErrorText("");
				int errorCode = priceListTable.insertProductsAboveLastSelection(priceListEditor.getProductsFromTextFields());
				if (errorCode == 0)
					return;
				switch (errorCode) {
				case PriceListEditor.IMPROPERDATAERROR -> {
					searchPricesTool.setErrorText("Improper data format");
				}
				case PriceListEditor.PRODUCTEXISTSERROR -> {
					searchPricesTool.setErrorText("Product(s) already exist");
				}
				case PriceListEditor.NOROWSELECTEDERROR -> {
					searchPricesTool.setErrorText("No row selected");
				}
				default -> {
					searchPricesTool.setErrorText("Unexpected error");
				}
				}
			});

			priceListEditor.setOnDeleteButtonAction(e -> priceListTable.deleteSelectedProducts());

			priceListEditor.setOnSortButtonAction(e -> priceListTable.sortProductsInAlphaOrder());
		}
	}
}
