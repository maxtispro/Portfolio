package gui.child;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ChildWindow extends Stage {
	
	private EditDistributorsPane editDistributorsPane;
	private CSVImportPane csvImportPane;
	private CSVExportPane csvExportPane;
	
	private Scene editDistributors;
	private Scene csvImport;
	private Scene csvExport;
	
	private FileChooser fileChooser;
	
	private Scene initScene(Pane gui) {
		Scene scene = new Scene(gui, 700, 700);
		scene.getStylesheets().add("appstyle.css");
		setResizable(false);
		return scene;
	}
	
	public ChildWindow(Stage parentStage) {
		editDistributorsPane = new EditDistributorsPane();
		csvImportPane = new CSVImportPane();
		csvExportPane = new CSVExportPane();
		
		editDistributors = initScene(editDistributorsPane);
		csvImport = initScene(csvImportPane);
		csvExport = initScene(csvExportPane);
		
		initOwner(parentStage);
		initModality(Modality.APPLICATION_MODAL);
		
		fileChooser = new FileChooser();
		fileChooser.setTitle("Open CSV File");
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("CSV Files", "*.csv"),
				new ExtensionFilter("All Files", "*.*"));
		
		csvImportPane.setOnBrowseFileButtonAction(e -> {
			File file = fileChooser.showOpenDialog(this);
			if (file != null) {
				csvImportPane.setFilePath(file.getAbsolutePath());
				csvImportPane.loadFile();
			}
		});
	}
	
	public void openEditDistributors() {
		setTitle("Edit Distributors");
		setScene(editDistributors);
		showAndWait();
	}
	
	public void openCSVImport() {
		setTitle("Import From CSV");
		setScene(csvImport);
		showAndWait();
	}
	
	public void openCSVExport() {
		setTitle("Export To CSV");
		setScene(csvExport);
		showAndWait();
	}
}
