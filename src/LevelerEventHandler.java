import javafx.event.*;
import javafx.scene.input.MouseEvent;

/**
 * This class is a custom implementation of a JavaFX mouse event EventHandler.
 * This implementation allows the storage of a coordinate.
 * <p>
 * Identifiers:
 * <li>Pair coord - a Pair that can be used to store the coordinates of a square
 * in a grid.
 */
public class LevelerEventHandler implements EventHandler<MouseEvent> {

	Pair coord;

	/**
	 * handle method: This method will be called whenever a mouse event occurs
	 * to the object this handler is attached to.
	 * 
	 * @param event
	 *            - a MouseEvent object that holds information related to the
	 *            mouse event that triggered this method.
	 */
	@Override
	public void handle(MouseEvent event) {

	}//end handle method

}//end LevelerEventHandler class
