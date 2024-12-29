package robotSimGUI;

import javafx.scene.image.Image; // to add assets

/** Robot - Represents a moving Robot in the RobotArena
 * Inherits from ArenaItem and supports animated frames
 */
public class Robot extends ArenaItem {
	private double dx; // x-direction of movement
	private double dy; // y-direction of movement
	private double speed; // speed multiplier
	private RobotArena arena; // reference to the RobotArena instance
	private Image[] frames; // array to store animation frames
	private int currentFrameIndex; // index to track the current frame
	private long lastFrameTime; // time when the last frame was updated
	private static final long FRAME_DURATION = 200_000_000; // duration of each frame in nanoseconds
	
	
	
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
			    new Image("file:src/robotSimGUI/Assets/basicRobotFrame1.png"),
			    new Image("file:src/robotSimGUI/Assets/basicRobotFrame2.png"),
			    new Image("file:src/robotSimGUI/Assets/basicRobotFrame3.png")
		};
		currentFrameIndex = 0; // start with the first frame
		lastFrameTime = System.nanoTime(); // initialise the last frame
	}
	
	/** Method update - to update the robot's position based on movement direction and handles animation frame
	 * Ensure that the robot stays with the boundaries of the arena and handles collisions
	 * 
	 */
	@Override
	public void update() {
		long currentTime = System.nanoTime(); // current time in nanoseconds
		// Update the animation frame based on how much time has elapsed
		if (currentTime - lastFrameTime >= FRAME_DURATION) {
			currentFrameIndex = (currentFrameIndex + 1) % frames.length; // loop through the three frames
			lastFrameTime = currentTime; // reset the frame time
		}
		
		// Update the position
		double newX = getXPosition() + dx; // new x-coordinate
		double newY = getYPosition() + dy; // new y-coordinate
		
		// Check for collisions with other items in the arena
		for (ArenaItem item : arena.getItems()) {
			// If a collision is detected, reverse the direction of the robot
			if (item != this && checkCollision(item)) {
				dx = -dx; // reverse the x-direction
				dy = -dy; // reverse the y-direction
				
				// Move slightly to prevent getting stuck in a collision
				newX += dx * 2; // adjust x-position
				newY += dy * 2; // adjust y-position
				break; // only handle one collision at a time
			}
		}
		
		// Logic to "bounce" of walls
		if (newX < getRadius() || newX > 800 - getRadius()) { // reverse the x-direction if either condition is met
			dx = -dx; // reverse the x-direction on hitting the left or right boundary
		}
		
		if (newY < getRadius() || newY > 600 - getRadius()) { // reverse the y-direction if either condition is met
			dy = -dy; // reverse the y-direction on hitting a top or bottom boundary
		}
		
		// Apply the new position
		newX = Math.max(getRadius(), Math.min(newX, 800 - getRadius())); // ensure x-coordinates within bounds
		newY = Math.max(getRadius(), Math.min(newY, 600 - getRadius())); // ensure y-coordinates within bounds
		
		// Set the new position 
		setPosition(newX, newY);
	}
	
	
	/** Method to draw the robot
	 * Displays the current animation frame
	 * 
	 * @param canvas - MyCanvas instance for drawing
	 * 
	 */
	@Override
	public void draw(MyCanvas canvas) {
		// Draw the current animation frame
		canvas.drawImage(
		frames[currentFrameIndex],
		getXPosition() - getRadius(),
		getYPosition() - getRadius(),
		getRadius() * 3, // for height
		getRadius() * 3); // for width
	}
	
	/** Method setPosition - Updates the robot's position
	 * This method is used to set the private xPosition and yPosition
	 * Reflection is used to modify the private fields securely
	 * 
	 * @param x = New x-coordinate
	 * @param y = New y-coordinate
	 */
	private void setPosition(double x, double y) {
		try {
			// Access the private xPosition fields from the ArenaItem class
			java.lang.reflect.Field fieldX = ArenaItem.class.getDeclaredField("xPosition");
			// Access the private yPosition fields from the ArenaItem class
			java.lang.reflect.Field fieldY = ArenaItem.class.getDeclaredField("yPosition");
			
			// Set the fields to be accessible for modification
			fieldX.setAccessible(true);
			fieldY.setAccessible(true);
			
			// Update the xPosition and yPosition fields with the new values
			fieldX.set(this, x);
			fieldY.set(this, y);
		} catch (Exception e) {
			// Catch and print any exceptions that occur during the reflection process
			e.printStackTrace();
		}
	}
	
	/** Method checkCollision - Checks if this robot is colliding with another item
	 * Uses the Euclidean distance formula to detect if two items are overlapping
	 * 
	 * @param item - The other ArenaItem to check collision with
	 * @return true if a collision is detected, otherwise return false
	 */
	private boolean checkCollision(ArenaItem item) {
		// Calculate the distance between the current robot and other item
		double dx = getXPosition() - item.getXPosition(); // difference in x-coordinates
		double dy = getYPosition() - item.getYPosition(); // difference in y-coordinates
		double distance = Math.sqrt(dx * dx + dy * dy); // compute Euclidean distance
		
		// Check if the distance is less than the sum of the radii (including a collision)
		return distance < (getRadius() + item.getRadius());
	}
}

