package robotCivSim;

import javafx.scene.image.Image; // to add assets

import java.io.IOException; // for file save and load
import java.io.ObjectInputStream; // for file save and load
import java.io.ObjectOutputStream; // for file save and load
import java.io.Serializable; // for file save and load

/** Robot - Represents a moving Robot in the RobotArena
 * Inherits from ArenaItem and supports animated frames
 */
public class Robot extends ArenaItem {
	@SuppressWarnings("unused") // static means it thinks its unused
	private static final long serialVersionUID = 1L; // serialisation ID
	protected double dx; // x-direction of movement
	protected double dy; // y-direction of movement
	protected double speed; // speed multiplier
	public RobotArena arena; // reference to the RobotArena instance
	private transient Image[] frames; // array to store animation frames (transient, not serialisable)
	public int currentFrameIndex; // index to track the current frame
	public long lastFrameTime; // time when the last frame was updated
	public static final long FRAME_DURATION = 200_000_000; // duration of each frame in nanoseconds
	private long lastCollisionTime = 0;
	private static final long COLLISION_COOLDOWN = 200; // 200ms cooldown
	protected ArenaItem lastCollidedItem;
	
	
	/** Constructor for Robot
	 * Initialises the robot with position and size and consistent movement speed
	 * Loads the animation frames and sets the initial direction 
	 * 
	 * @param xPosition - x-coordinate of the robot
	 * @param yPosition - y-coordinate of the robot
	 * @param radius - radius of the robot
	 * @param arena - reference to the RobotArena instance
	 */
	public Robot(double xPosition, double yPosition, double radius, RobotArena arena) {
		super(xPosition, yPosition, radius); // reference parent class ArenaItem
		this.arena = arena; // assign the RobotArena instance
		this.speed = 2.0; // default speed multiplier
		
		double randomAngle = Math.random() * 2 * Math.PI; // random angle in radians
		this.dx = Math.cos(randomAngle) * speed; // random x-direction between -1 and 1
		this.dy = Math.sin(randomAngle) * speed; // random y-direction between -1 and 1
	
		// Load animation frames
		frames = new Image[] {
			    new Image("file:src/robotCivSim/Assets/basicRobotFrame1.png"),
			    new Image("file:src/robotCivSim/Assets/basicRobotFrame2.png"),
			    new Image("file:src/robotCivSim/Assets/basicRobotFrame3.png"),
			    new Image("file:src/robotCivSim/Assets/basicRobotFrame4.png")
		};
		currentFrameIndex = 0; // start with the first frame
		lastFrameTime = System.nanoTime(); // initialise the last frame
	}
	
	/** Method update - This is to update the robot's position based on movement direction and handles animation frame
	 * Ensure that the robot stays with the boundaries of the arena and handles collisions
	 * 
	 */
	@Override
	public void update() {
	    long currentTime = System.nanoTime(); // current time in nanoseconds

	    // Update the animation frame
	    if (currentTime - lastFrameTime >= FRAME_DURATION) {
	        currentFrameIndex = (currentFrameIndex + 1) % frames.length; // loop through frames
	        lastFrameTime = currentTime; // reset frame time
	    }

	    // Predict the new position
	    double newX = getXPosition() + dx;
	    double newY = getYPosition() + dy;

	    boolean collisionOccurred = false;
	    
	    
	    
	    // Check for collisions with other items in the arena
	    for (ArenaItem item : arena.getItems()) {
	        if (item != this && checkCollision(item)) {
	                collisionOccurred = true;

	                // reverse direction
	                dx = -dx;
	                dy = -dy;

	                // Move slightly back to prevent overlapping
	                newX = getXPosition() + dx * 2;
	                newY = getYPosition() + dy * 2;

	                System.out.println(getName() + " collided with " + item.getName() + " and reversed direction.");
	                break; // handle one collision at a time
	            }
	        }
	    

	    // Boundary logic to prevent leaving the arena
	    if (newX < getRadius() || newX > arena.getWidth() - getRadius()) {
	        dx = -dx; // reverse the x-direction
	        newX = Math.max(getRadius(), Math.min(newX, arena.getWidth() - getRadius()));
	    }
	    if (newY < getRadius() || newY > arena.getHeight() - getRadius()) {
	        dy = -dy; // reverse the y-direction
	        newY = Math.max(getRadius(), Math.min(newY, arena.getHeight() - getRadius()));
	    }
	  
	    // If no collision, apply the new position
	    if (!collisionOccurred) {
	        setPosition(newX, newY, arena.getWidth(), arena.getHeight());	        
	    }
	}

	/** Method checkCollision - Checks if this robot is colliding with another item.
	 * Uses the Euclidean distance formula to detect if two items are overlapping.
	 * Adds logic to track recent collisions and prevent immediate re-collision.
	 * 
	 * @param item - The other ArenaItem to check collision with
	 * @return true if a collision is detected, otherwise false
	 */
	protected boolean checkCollision(ArenaItem item) {
	    double dx = getXPosition() - item.getXPosition();
	    double dy = getYPosition() - item.getYPosition();
	    double distance = Math.sqrt(dx * dx + dy * dy);

	    double collisionThreshold = getRadius() + item.getRadius();
	    boolean collision = distance <= collisionThreshold;

	    if (collision && (item != lastCollidedItem || System.currentTimeMillis() - lastCollisionTime > COLLISION_COOLDOWN)) {
	        lastCollidedItem = item;
	        lastCollisionTime = System.currentTimeMillis();
	        return true;
	    }
	    return false;
	}
	
	
	/** Method draw - This is to draw the robot
	 * Displays the current animation frame
	 * Calculates angle to ensure assets face the direction they are moving
	 * 
	 * @param canvas - MyCanvas instance for drawing
	 * 
	 */
	@Override
	public void draw(MyCanvas canvas) {
		// Calculate the rotation angle in degrees
		double angle = Math.toDegrees(Math.atan2(dy, dx));	
		
		// Save the current state of the GraphicsContext
		canvas.save();
		
		// Translate the robot's position
		canvas.translate(getXPosition(), getYPosition());
		
		// Rotate the GraphicsContedxt to the calculated angle
		canvas.rotate(angle);
		
		// Draw the current animation frame
		canvas.drawImage(
		frames[currentFrameIndex],
		-getRadius() * 1.5, // centering the image horizontally
		-getRadius() * 1.5, // centering the image vertically
		getRadius() * 3, // scaled for height
		getRadius() * 3); // scaled for width
	
	
		// Restore the original state of the GraphicsContext
		canvas.restore();
	
	}
	/** Method setPosition - This updates the robot's position
	 * This method is used to set the private xPosition and yPosition
	 * Reflection is used to modify the private fields securely
	 * 
	 * @param x - New x-coordinate
	 * @param y - New y-coordinate
	 * @param arenaWidth - Width of the arena
	 * @param arenaHeight - height of the arena
	 */
	public void setPosition(double x, double y, double arenaWidth, double arenaHeight) {
			super.setPosition(x, y, arenaWidth, arenaHeight); // Delegate to base class (ArenaItem)
	}
		
	/**
	 * This default method is so that the Lumber and Miner robots can have destroy functionality
	 */
	@Override
	public void destroy() {
	    // Default behaviour: do nothing
	    throw new UnsupportedOperationException("This robot does not have destroy capabilities.");
	}

	
	/** Method writeObject - This serialises non-transient fields
	 */
	private void writeObject(ObjectOutputStream oos) throws IOException {
	    oos.defaultWriteObject(); // serialise non-transient fields
	}
	/** Method readObject - This deserialises non-transient fields
	 * It also restores the transient fields like the frames array
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject(); // deserialize non-transient fields
	    
	    // Reinitialise the transient frames array
	    frames = new Image[] {
	        new Image("file:src/robotCivSim/Assets/basicRobotFrame1.png"),
	        new Image("file:src/robotCivSim/Assets/basicRobotFrame2.png"),
	        new Image("file:src/robotCivSim/Assets/basicRobotFrame3.png"),
	        new Image("file:src/robotCivSim/Assets/basicRobotFrame4.png")
	    };
	}
	
	/** Method getCurrentFrame - This retrieves the current animation frame
	 * 
	 * @return The current image object representing the animation frame
	 */
	public Image getCurrentFrame() {
		return frames[currentFrameIndex]; // frames for basic robot
	}
	
	/** Method getName - This retrieves the name of the item so it can be displayed when selected
	 * 
	 *  @return a string for the name of the robot
	 */
	@Override 
	public String getName() {
		return "Basic Robot";
	}
	
	/** Method getDecsription - This retrieves the description of the item so it can be displayed when selected
	 * 
	 *  @return a string for the description of the robot
	 */
	@Override 
	public String getDescription() {
		return "A basic robot with no unique functioanlity, bumps into other items around the arena.";
	}
}

