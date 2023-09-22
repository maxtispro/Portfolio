package gui.parent;

import dataprocessing.Sample;
import javafx.collections.ObservableList;
import javafx.scene.layout.FlowPane;

public class SamplesList extends FlowPane {
	
	public SamplesList() {
		setHgap(20);
		setVgap(20);
	}
	
	private ObservableList<Sample> samples;
}
