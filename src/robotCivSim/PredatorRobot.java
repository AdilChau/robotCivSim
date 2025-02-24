package robotCivSim;

import javafx.scene.image.Image;

import java.io.IOException; // for file save and load
import java.io.ObjectInputStream; // for file save and load
import java.io.Serializable; // for file save and load

/** PredatorRobot - Represents a robot that chases and eliminates basic robots.
 * It inherits from robots and overrides behaviour to chase the basic robots.
 * If there are no basic robots then it behaves like a normal robot.
 */
public class PredatorRobot extends Robot implements Serializable {
	private static final long serialVersionUID = 1L; // serialisation ID
	private Robot targetRobot; // the current target (a basic robot)
	private transient Image[] frames; // animation frames for the PredatorRobot (transient for serialisation)
	
	/** Constructor for PredatorRobot
	 * Initialises the predator robot with position, size, and references to the arena
	 * Loads its unique animation frames and sets a faster base speed 
	 * 
	 * @param xPostiton - x-coordinate of the predator robot
	 * @param yPosition = y-coordinate of the predator robot
	 * @param radius - the radius of the predator robot
	 * @param arena - reference to the RobotArena instance 
	 */
	public PredatorRobot(double xPosition, double yPosition, double radius, RobotArena arena) {
		super(xPosition, yPosition, radius, arena); // call parent constructor
		this.speed = 2.5; // slightly faster base speed than basic robot
		
		// Load animation frames
		frames = new Image[] {
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame1.png"),
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame2.png"),
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame3.png"),
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame4.png")
		};
	}

	/** Method update - This updates the predator robot's behaviour 
	 * The predator chases the closest basic robot 
	 * If there isn't a basic robot on canvas, the PredatorRobot acts like a normal robot
	 */
	@Override
	public void update() {
		// If no target or target is removed, fine the closest basic robot
		if (targetRobot == null || !arena.getItems().contains(targetRobot)) {
			targetRobot = findClosestBasicRobot(); // find a new target 
		}
		
		// If a target exists, move toward it
		if (targetRobot != null) {
			double dxToTarget = targetRobot.getXPosition() - getXPosition(); // horizontal distance
			double dyToTarget = targetRobot.getYPosition() - getYPosition(); // vertical distance
			double distance = Math.sqrt(dxToTarget * dxToTarget + dyToTarget * dyToTarget); // calculate distance
			
			// If close enough to the target, "eliminate" it 
			if (distance < getRadius() + targetRobot.getRadius()) {
				// Schedule for removal (don't directly remove)
				arena.scheduleRemoval(targetRobot);
				targetRobot = null; // reset the target
			} else {
				// Move toward the target
				double angleToTarget = Math.atan2(dyToTarget, dxToTarget); // calculate distance 
				dx = Math.cos(angleToTarget) * speed; // update horizontal speed
				dy = Math.sin(angleToTarget) * speed; // update vertical speed
			}
		}
		
		// Fall back to normal robot behaviour if no target exists
		super.update(); 
	}
	
	/** Method findClosestBasicRobot - This finds the nearest basic robot in the arena
	 * 
	 * @return The closest basicRobot, or null if none exist 
	 */
	private Robot findClosestBasicRobot() {
		Robot closestRobot = null; // initialise as null
		double closestDistance = Double.MAX_VALUE; // start with max distance 
		
		// Iterate over all items in the arena 
		for (ArenaItem item : arena.getItems()) {
			if (item instanceof Robot && !(item instanceof PredatorRobot) && !(item instanceof SmartRobot) && !(item instanceof LumberRobot) && !(item instanceof MinerRobot)) { // only consider basic robots
				Robot robot = (Robot) item;
				double dx = robot.getXPosition() - getXPosition(); // horizontal distance 
				double dy = robot.getYPosition() - getYPosition(); // vertical distance
				double distance = Math.sqrt(dx * dx + dy * dy); // calculate distance
				
				// Check if this robot is closer than the current closest 
				if (distance < closestDistance) {
					closestDistance = distance; // update closest distance
					closestRobot = robot; // set this as the closest distance
				}
			}
		}
		
		return closestRobot; // return the closest robot, or null if none found
	}
	
	/** Method draw - This draws the predator robot on the canvas 
	 * Displays its unique animation frames
	 * 
	 * @param canvas - MyCanvas instance for rendering 
	 */
	@Override
	public void draw(MyCanvas canvas) {
		// Save the current GraphicsContext state
		canvas.save();
		
		// Translate to the robot's position and rotate to face its direction
		canvas.translate(getXPosition(), getYPosition());
		double angle= Math.toDegrees(Math.atan2(dy, dx)); // calculate rotation angle
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
		return frames[currentFrameIndex]; // frames for predator robot
	}
	
	/** Method getName - This retrieves the name of the item so it can be displayed when selected
	 * 
	 *  @return a string for the name of the robot
	 */
	@Override 
	public String getName() {
		return "Predator Robot";
	}
	
	/** Method getDecsription - This retrieves the description of the item so it can be displayed when selected
	 * 
	 *  @return a string for the description of the robot
	 */
	@Override 
	public String getDescription() {
		return "A predator robot that hunts basic robots and eliminates them from the arena.";
	}
	
	/** Method readObject - This deserialises non-transient fields
	 * It also restores the transient fields like the frames array
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject(); // deserialize non-transient fields
	    
	    // Reinitialise the transient frames array
	    frames = new Image[] {
	        new Image("file:src/robotCivSim/Assets/predatorRobotFrame1.png"),
	        new Image("file:src/robotCivSim/Assets/predatorRobotFrame2.png"),
	        new Image("file:src/robotCivSim/Assets/predatorRobotFrame3.png"),
	        new Image("file:src/robotCivSim/Assets/predatorRobotFrame4.png")
	    };
	}
}
