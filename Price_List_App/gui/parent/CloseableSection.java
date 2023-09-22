package gui.parent;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface CloseableSection {
	
	abstract boolean isOpen();
	
	abstract void open();
	abstract void close();
	
	abstract void setOnOpenButtonAction(EventHandler<ActionEvent> event);
}
