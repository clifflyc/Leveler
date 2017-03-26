import javafx.animation.Transition;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class TileAnimation extends Transition{
	Label label;
	Color from;
	Color to;

	public TileAnimation(Label label, Color from, Color to, Duration duration) {
		setCycleDuration(duration);
		this.from=from;
		this.to=to;
		this.label=label;
	}

	@Override
	protected void interpolate(double frac) {
		Color curColor= from.interpolate(to, frac);
		label.setBackground(new Background(new BackgroundFill(curColor, CornerRadii.EMPTY, Insets.EMPTY)));
	}
	
}
