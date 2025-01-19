package robotCivSim;

import javafx.scene.image.Image;

import java.io.IOException; // for file save and load
import java.io.ObjectInputStream; // for file save and load
import java.io.Serializable; // for file save and load


/** LumberRobot - A robot that targets trees and removes them on collision.
 * Inherits from the robot class and overrides specific behaviours to handle trees.
 */
public class LumberRobot extends Robot implements Serializable {
	private static final long serialVersionUID = 1L; // serialisation ID
	private Obstacle targetTree; // the current tree target
	private transient Image[] frames; // animation frames for the LumberRobot (transient for serialisation)
	
	/** Constructor for LumberRobot
	 * Initialises the LumberRobot with position, size, and reference to arena
	 * Loads its unique animation frames
	 * 
	 * @param xPosition - x-coordinate of the LumberRobot
	 * @param yPosition - y-coordinate of the LumberRobot
	 * @radius - radius of the LumberRobot
	 * @arena - reference to the RobotArena instance
	 */
	public LumberRobot(double xPosition, double yPosition, double radius, RobotArena arena) {
		super(xPosition, yPosition, radius, arena); // call parent constructor
		this.speed = 2.0; // normal robot speed
		
		// Load animation frames
		frames = new Image[]{
	            new Image("file:src/robotCivSim/Assets/lumberRobotFrame1.png"),
	            new Image("file:src/robotCivSim/Assets/lumberRobotFrame2.png"),
	            new Image("file:src/robotCivSim/Assets/lumberRobotFrame3.png"),
	            new Image("file:src/robotCivSim/Assets/lumberRobotFrame4.png")
	    };
	}
	
	/** Method update - This updates the LumberRobot's behaviour 
	 * The robot targets and removes trees, or behaves like a normal robot if there are no trees
	 */
	@Override
	public void update() {
		// If no target or target is removed, find the closest tree
		if (targetTree == null || !arena.getItems().contains(targetTree)) {
			targetTree = findClosestTree(); // find a new target
		}
		
		// If a target exists, move toward it
		if (targetTree!= null) {
			double dxToTarget = targetTree.getXPosition() - getXPosition(); // horizontal distance
			double dyToTarget = targetTree.getYPosition() - getYPosition(); // vertical distance
			double distance = Math.sqrt((dxToTarget * dxToTarget + dyToTarget * dyToTarget)); // calculate distance
			
			// If close enough to the tree, "chop" it
			if (distance < getRadius() + targetTree.getRadius()) {
				arena.scheduleRemoval(targetTree); // schedule tree removal
				targetTree = null; // reset the target
			} else {
				// Move toward the tree
				double angleToTarget = Math.atan2(dyToTarget, dxToTarget); // calculate angle
				dx = Math.cos(angleToTarget) * speed; // update horizontal speed
				dy = Math.sin(angleToTarget) * speed; // update vertical speed
			}
		}
		
		// Fall back to normal robot behaviour if no target exists
		super.update();
	}
	
	/** Method findClosestTree - This finds the nearest tree in the arena 
	 * 
	 * @return The closest tree obstacle, or null if none exists
	 */
	private Obstacle findClosestTree() {
		Obstacle closestTree = null; // initialise as null
		double closestDistance = Double.MAX_VALUE; // start with max distance 
		
		// Iterate over all items in the arena
		for (ArenaItem item : arena.getItems()) {
			if (item instanceof Obstacle && "tree".equals(((Obstacle) item).getType())) { // only consider trees
				Obstacle tree = (Obstacle) item;
				double dx = tree.getXPosition() - getXPosition(); // horizontal distance
				double dy = tree.getYPosition() - getYPosition(); // vertical distance
				double distance = Math.sqrt(dx * dx + dy * dy); // calculate distance
				
				// Check if this tree is closer that the current closest
				if (distance < closestDistance) {
					closestDistance = distance; // update closest distance 
					closestTree = tree; // set this as the closest tree
				}
			}
		}
		
		return closestTree; // return the closest tree, or null if none found
	}
	
	/** Method draw - This draws the LumberRobot on the canvas
	 * It displays its unique animation frames.
	 * 
	 * @param canvas - MyCanvas instance for rendering
	 */
	@Override 
	public void draw(MyCanvas canvas) {
		// Save the current GraphicsContext state
		canvas.save();
		
		// Translate to the robot's position and rotate to face its direction 
		canvas.translate(getXPosition(), getYPosition());
		double angle = Math.toDegrees(Math.atan2(dy, dx)); // calculate rotation angle
		canvas.rotate(angle);
		
		// Draw the current animation frame
        long currentTime = System.nanoTime(); // get current time
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            currentFrameIndex = (currentFrameIndex + 1) % frames.length; // loop through frames
            lastFrameTime = currentTime; // update last frame time
        }
        canvas.drawImage(
                frames[currentFrameIndex],
                -getRadius() * 1.5, // centering the image horizontally
                -getRadius() * 1.5, // centering the image vertically
                getRadius() * 3, // scaled width
                getRadius() * 3 // scaled height
        );
        
        // Restore the GraphicsContext state
        canvas.restore();
	}
	
	/** Method getCurrentFrame - Retrieves the current animation frame
	 * 
	 * @return The current image object representing the animation frame
	 */
	public Image getCurrentFrame() {
		return frames[currentFrameIndex]; // frames for lumber robot
	}
	
	/** Method getName - This retrieves the name of the item so it can be displayed when selected
	 * 
	 *  @return a string for the name of the robot
	 */
	@Override 
	public String getName() {
		return "Lumber Robot";
	}
	
	/** Method getDecsription - This retrieves the description of the item so it can be displayed when selected
	 * 
	 *  @return a string for the description of the robot
	 */
	@Override 
	public String getDescription() {
		return "A lumber robot that moves around the arena chopping trees.";
	}
	
	/** Method readObject - This deserialises non-transient fields
	 * It also restores the transient fields like the frames array
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject(); // deserialize non-transient fields
	    
	    // Reinitialise the transient frames array
	    frames = new Image[] {
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame1.png"),
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame2.png"),
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame3.png"),
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame4.png")
	    };
	}
}
