package robotCivSim;

import javafx.scene.image.Image;

import java.io.IOException; // for file save and load
import java.io.ObjectInputStream; // for file save and load
import java.io.Serializable; // for file save and load


/** MinerRoboot - A robot that targets rocks and removes them on collision.
 * Inherits from the robot class and overrides specific behaviours to handle rocks.
 */
public class MinerRobot extends Robot implements Serializable {
	private static final long serialVersionUID = 1L; // serialisation ID
	private Obstacle targetRock; // the current rock target
	private transient Image[] frames; // animation frames for the MienrRobot (transient for serialisation)
	
	/** Constructor for MinerRobot
	 * Initialises the MinerRobot with position, size, and reference to arena
	 * Loads its unique animation frames
	 * 
	 * @param xPosition - x-coordinate of the MinerRobot
	 * @param yPosition - y-coordinate of the MinerRobot
	 * @radius - radius of the MinerRobot
	 * @arena - reference to the RobotArena instance
	 */
	public MinerRobot(double xPosition, double yPosition, double radius, RobotArena arena) {
		super(xPosition, yPosition, radius, arena); // call parent constructor
		this.speed = 2.0; // normal robot speed
		
		// Load animation frames
		frames = new Image[]{
	            new Image("file:src/robotCivSim/Assets/minerRobotFrame1.png"),
	            new Image("file:src/robotCivSim/Assets/minerRobotFrame2.png"),
	            new Image("file:src/robotCivSim/Assets/minerRobotFrame3.png"),
	            new Image("file:src/robotCivSim/Assets/minerRobotFrame4.png")
	    };
	}
	
	/** Method update - This updates the MinerRobot's behaviour 
	 * The robot targets and removes rocks, or behaves like a normal robot if there are no rocks
	 */
	@Override
	public void update() {
		// If no target or target is removed, find the closest rock
		if (targetRock == null || !arena.getItems().contains(targetRock)) {
			targetRock = findClosestRock(); // find a new target
		}
		
		// If a target exists, move toward it
		if (targetRock!= null) {
			double dxToTarget = targetRock.getXPosition() - getXPosition(); // horizontal distance
			double dyToTarget = targetRock.getYPosition() - getYPosition(); // vertical distance
			double distance = Math.sqrt((dxToTarget * dxToTarget + dyToTarget * dyToTarget)); // calculate distance
			
			// If close enough to the rock, "mine" it
			if (distance < getRadius() + targetRock.getRadius()) {
				arena.scheduleRemoval(targetRock); // schedule rock removal
				targetRock = null; // reset the target
			} else {
				// Move toward the rock
				double angleToTarget = Math.atan2(dyToTarget, dxToTarget); // calculate angle
				dx = Math.cos(angleToTarget) * speed; // update horizontal speed
				dy = Math.sin(angleToTarget) * speed; // update vertical speed
			}
		}
		
		// Fall back to normal robot behaviour if no target exists
		super.update();
	}
	
	/** Method findClosestRock - This finds the nearest rock in the arena 
	 * 
	 * @return The closest rock obstacle, or null if none exists
	 */
	private Obstacle findClosestRock() {
		Obstacle closestRock = null; // initialise as null
		double closestDistance = Double.MAX_VALUE; // start with max distance 
		
		// Iterate over all items in the arena
		for (ArenaItem item : arena.getItems()) {
			if (item instanceof Obstacle && "rock".equals(((Obstacle) item).getType())) { // only consider rocks
				Obstacle rock = (Obstacle) item;
				double dx = rock.getXPosition() - getXPosition(); // horizontal distance
				double dy = rock.getYPosition() - getYPosition(); // vertical distance
				double distance = Math.sqrt(dx * dx + dy * dy); // calculate distance
				
				// Check if this rock is closer that the current closest
				if (distance < closestDistance) {
					closestDistance = distance; // update closest distance 
					closestRock = rock; // set this as the closest rock
				}
			}
		}
		
		return closestRock; // return the closest rock, or null if none found
	}
	
	/** Method draw - This draws the MinerRobot on the canvas
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
	
	/** Method getCurrentFrame - This retrieves the current animation frame
	 * 
	 * @return The current image object representing the animation frame
	 */
	public Image getCurrentFrame() {
		return frames[currentFrameIndex]; // frames for miner robot
	}
	
	/** Method getName - This retrieves the name of the item so it can be displayed when selected
	 * 
	 *  @return a string for the name of the robot
	 */
	@Override 
	public String getName() {
		return "Miner Robot";
	}
	
	/** Method getDecsription - This retrieves the description of the item so it can be displayed when selected
	 * 
	 *  @return a string for the description of the robot
	 */
	@Override 
	public String getDescription() {
		return "A miner robot that moves around the arena mining rocks.";
	}
	
	/** Method readObject - This deserialises non-transient fields
	 * It also restores the transient fields like the frames array
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject(); // deserialize non-transient fields
	    
	    // Reinitialise the transient frames array
	    frames = new Image[] {
	        new Image("file:src/robotCivSim/Assets/minerRobotFrame1.png"),
	        new Image("file:src/robotCivSim/Assets/minerRobotFrame2.png"),
	        new Image("file:src/robotCivSim/Assets/minerRobotFrame3.png"),
	        new Image("file:src/robotCivSim/Assets/minerRobotFrame4.png")
	    };
	}
}
