package robotSimGUI;

import javafx.scene.paint.Color;

/** Obstacle - Represents a static object in the RobotArena.
 * Inherits from ArenaItem.
 */
public class Obstacle extends ArenaItem {

	/** Constructor for Obstacle
	 * Initialises the obstacle with position and size
	 * 
	 * @param xPosition - x-coordinate of the obstacle
	 * @param yPosition - y-coordinate of the obstacle
	 * @param radius - radius of the obstacle
	 */
	public Obstacle(double xPosition, double yPosition, double radius) {
		super(xPosition, yPosition, radius); // reference parent class ArenaItem
	}
	
	/** Method to draw the obstacle
	 * Draws the obstacle as a gray circle
	 */
	@Override
	public void draw(MyCanvas canvas) {
		// Draw a gray circle
		canvas.drawCircle(getXPosition(), getYPosition(), getRadius(), Color.GRAY);
	}
	
	/** Method update - Does nothing as we want obstacles to remain static
	 * Ensures that obstacles do not move or change state
	 */
	@Override
	public void update() {
		// Static object - So no updates required
	}
}
