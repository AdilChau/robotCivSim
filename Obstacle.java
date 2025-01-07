package robotSimGUI;

import javafx.scene.image.Image; // for obstacle assets

/** Obstacle - Represents a static object in the RobotArena.
 * Inherits from ArenaItem.
 */
public class Obstacle extends ArenaItem {
	private Image image; // image to represent the obstacle
	
	
	/** Constructor for Obstacle
	 * Initialises the obstacle with position, size, and an image
	 * 
	 * @param xPosition - x-coordinate of the obstacle
	 * @param yPosition - y-coordinate of the obstacle
	 * @param radius - radius of the obstacle
	 */
	public Obstacle(double xPosition, double yPosition, double radius) {
		super(xPosition, yPosition, radius); // reference parent class ArenaItem
		this.image = new Image(getClass().getResourceAsStream("/robotSimGUI/Assets/treeObstacle.png"));
	}
	
	/** Method to draw the obstacle
	 * Draws the obstacles an an image from Assets
	 * 
	 * @param canvas - MyCanvas instance for rendering
	 */
	@Override
	public void draw(MyCanvas canvas) {
		// Scale image to fit within the radius
		canvas.drawImage(
				image, // the image being drawn
				getXPosition() - getRadius(), // x-coordinate 
				getYPosition() - getRadius(), // y-coordinate
				getRadius() * 2, // width (scaled)
				getRadius() * 2 // height (scaled)
			);
	}
	
	/** Method update - Does nothing as we want obstacles to remain static
	 * Ensures that obstacles do not move or change state
	 */
	@Override
	public void update() {
		// Static object - So no updates required
	}
}
