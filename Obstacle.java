package robotSimGUI;

import javafx.scene.image.Image; // for obstacle assets
import javafx.scene.paint.Color; // for fallback

/** Obstacle - Represents a static object in the RobotArena.
 * Inherits from ArenaItem.
 */
public class Obstacle extends ArenaItem {
	private String type; // type of obstacle (e.g., "tree", "rock")
	
	
	/** Constructor for Obstacle
	 * Initialises the obstacle with position, size, and a type of image
	 * 
	 * @param xPosition - x-coordinate of the obstacle
	 * @param yPosition - y-coordinate of the obstacle
	 * @param radius - radius of the obstacle
	 * @param type - the type of obstacle (e.g., "tree", "rock"
	 */
	public Obstacle(double xPosition, double yPosition, double radius, String type) {
		super(xPosition, yPosition, radius); // reference parent class ArenaItem
		this.type = type.toLowerCase(); // initialises the type of the obstacle and normalises to lowercase for consistency
	}
	
	/** Method to draw the obstacle
	 * Draws the obstacle based on its type
	 * 
	 * @param canvas - MyCanvas instance for rendering
	 */
	@Override
	public void draw(MyCanvas canvas) {
		Image obstacleImage = switch (type) {
		case "tree" -> new Image(getClass().getResource("/robotSimGUI/Assets/treeObstacle.png").toExternalForm());
		case "rock" -> new Image(getClass().getResource("/robotSimGUI/Assets/rockObstacle.png").toExternalForm());
		default -> null;
		};
		
		// Draw the obstacle image if it exists
		if (obstacleImage != null) {
			canvas.drawImage(obstacleImage,
					getXPosition() - getRadius(), // x-coordinate
					getYPosition() - getRadius(), // y-coordinate
					getRadius() * 2.5, // width (scaled)
					getRadius() * 2.5); // height (scaled)
		} else {
			// Fallback in case the image isn't defined
			canvas.drawCircle(getXPosition(), getYPosition(), getRadius(), Color.GRAY);
		}
	}
	
	/** Method update - Does nothing as we want obstacles to remain static
	 * Ensures that obstacles do not move or change state
	 */
	@Override
	public void update() {
		// Static object - So no updates required
	}
}
