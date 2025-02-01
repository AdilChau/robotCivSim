package robotCivSim;

import javafx.scene.image.Image; // for obstacle assets
import javafx.scene.paint.Color; // for fallback
import java.io.Serializable; // for file save and load
import java.util.Objects;
import java.util.Timer; // for obstacle re-spawn
import java.util.TimerTask; // for obstacle re-spawn

/**
 * Obstacle - Represents a static object in the RobotArena.
 * Obstacles can either be a "tree" or a "rock."
 * When destroyed, they drop a corresponding resource item
 * (e.g., WoodResource for trees, RockResource for rocks).
 */

public class Obstacle extends ArenaItem {
	@SuppressWarnings("unused") // static means it thinks its unused
	private static final long serialVersionUID = 1L; // serialisation ID
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
	
	/** Method draw - This is to draw the obstacle
	 * Draws the obstacle based on its type
	 * 
	 * @param canvas - MyCanvas instance for rendering
	 */
	@Override
	public void draw(MyCanvas canvas) {
		Image obstacleImage = switch (type) {
		case "tree" -> new Image(getClass().getResource("/robotCivSim/Assets/treeObstacle.png").toExternalForm());
		case "rock" -> new Image(getClass().getResource("/robotCivSim/Assets/rockObstacle.png").toExternalForm());
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
	
	/** Method update - This does nothing as we want obstacles to remain static
	 * Ensures that obstacles do not move or change state
	 */
	@Override
	public void update() {
		// Static object - So no updates required
	}
	
	/**
	 * Method Destroy - This destroys an item and drops a resource
	 * It checks the type of the obstacle and drops the appropriate resource item.
	 */
	@Override
	public void destroy() {
	    // Ensure the arena is available
	    if (getArena() != null) {
	        // Add a resource item to the arena when the obstacle is destroyed
	        if ("tree".equals(type)) {
	            getArena().scheduleAddition(new WoodResource(getXPosition(), getYPosition(), getRadius() * 1.5));
	        } else if ("rock".equals(type)) {
	            getArena().scheduleAddition(new RockResource(getXPosition(), getYPosition(), getRadius() * 1.5));
	        }
	        // Remove this obstacle from the arena
	        getArena().scheduleRemoval(this); // schedule safe removal
	        obstacleRespawn(); // re-spawn obstacle after time limit
	    }
	}

	/** Method obstacleRespawn - This means that when an obstacle is destroyed by a robot a timer activates and then the object re-spawns at the same coordinates
	 * Ensures resource item has been collected and then starts timer
	 */
	public boolean obstacleRespawn() {
		Timer timer = new Timer(); // create a timer
		
		// Schedule a task to re-spawn the obstacle after 30 seconds (30000 milliseconds)
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Ensure the arena exists before re-spawning
				if (getArena() != null) {
	                // Create a new obstacle
	                Obstacle newObstacle = new Obstacle(getXPosition(), getYPosition(), getRadius(), type);
	                // Set arena reference
	                javafx.application.Platform.runLater(() -> {
	                    newObstacle.setArena(getArena()); // treat the re-spawn as a new obstacle so that the LumberRobot target method works
	                    getArena().scheduleAddition(newObstacle); // schedule for addition to arena
	                });
				}
			}
		}, 10000); // delay of 10 seconds
		return true;
	} 
	
	/** Method getType - This gets the type of the obstacle
	 * 
	 * @return the type of the obstacle (e.g., "tree", "rock")
	 */
	public String getType() {
		return type; // return the obstacle type
	}
	
	/** Method getImage - This retrieves the current image of the obstacle
	 * 
	 * @return The image object representing the obstacle
	 */
	public Image getImage() {
		return switch (type.toLowerCase()) {
        	case "tree" -> new Image(getClass().getResource("/robotCivSim/Assets/treeObstacle.png").toExternalForm()); // retrieves tree image
        	case "rock" -> new Image(getClass().getResource("/robotCivSim/Assets/rockObstacle.png").toExternalForm()); // retrieves rock image
        	default -> null; // default to null if the type selected is invalid
		};
	}
	
	/** Method getName - This retrieves the name of the item so it can be displayed when selected
	 * 
	 *  @return a string for the name of the robot
	 */
	@Override 
	public String getName() {
		return type.substring(0, 1).toUpperCase() + type.substring(1) + " Obstacle"; 
	}
	
	/** Method getDecsription - This retrieves the description of the item so it can be displayed when selected
	 * 
	 *  @return a string for the description of the robot
	 */
	@Override 
	public String getDescription() {
		return "A stationary object that blocks robots or is farmed by specific robot type.";
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true; // Same reference
	    if (obj == null || getClass() != obj.getClass()) return false; // Type check

	    Obstacle obstacle = (Obstacle) obj;

	    // Compare based on unique attributes (position, type, and size)
	    return Double.compare(obstacle.getXPosition(), getXPosition()) == 0 &&
	           Double.compare(obstacle.getYPosition(), getYPosition()) == 0 &&
	           Double.compare(obstacle.getRadius(), getRadius()) == 0 &&
	           this.type.equals(obstacle.type);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(getXPosition(), getYPosition(), getRadius(), type);
	}

}
