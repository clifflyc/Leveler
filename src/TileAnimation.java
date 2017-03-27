import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Represents a transition use for tile animations. Cross-fades the color of a
 * label over a set duration.
 * <p>
 * Identifiers
 * <li>Label label - the target label to effect
 * <li>Color from - the original color to start with
 * <li>Color to - the target color to end with
 */
public class TileAnimation extends Transition {
	Label label;
	Color from;
	Color to;

	/**
	 * constructor for TileAnimation class: sets the label to affect, the start
	 * and end color, as well as the duration for the transition.
	 * 
	 * @param label
	 *            the target label to effect
	 * @param from
	 *            the original color to start with
	 * @param to
	 *            the target color to end with
	 * @param duration
	 *            the length of the fade
	 */
	public TileAnimation(Label label, Color from, Color to, Duration duration) {
		setCycleDuration(duration);
		this.from = from;
		this.to = to;
		this.label = label;
	}// end TileAnimation constructor

	/**
	 * interpolate method: Sets the color of the label based on the current
	 * fraction of the transition. The start color is interpolated towards the
	 * end color by amount specified by {@code frac}.
	 * 
	 * @param frac
	 *            - a decimal representing how far we are from the beginning to
	 *            the end (e.g. 0.4 means 40% way through the transition)
	 */
	@Override
	protected void interpolate(double frac) {
		Color curColor = from.interpolate(to, frac);
		label.setBackground(new Background(new BackgroundFill(curColor, CornerRadii.EMPTY, Insets.EMPTY)));
	}// end interpolate method

}// end TileAnimation class
