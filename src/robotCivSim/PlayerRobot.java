package robotCivSim;

import javafx.scene.image.Image; // for animation 
import javafx.scene.input.KeyEvent; // for player controlled movement
import robotCivSim.sound.SoundManager;

/** 
 * PlayerRobot class - This represents a user-controlled robot in the arena.
 * Takes inputs such as W, A, S, D for directional movement (UP, LEFT, DOWN, RIGHT)
 * Collides with other items and stops, treating as impassable. 
 */
public class PlayerRobot extends ArenaItem {
	private static final long serialVersionUID = 1L; // for file save / load
	private double speed = 3.0; // movement speed multiplier
	private double dx = 0, dy = 0; // direction of movement
	private transient Image[] frames; // array to store animation frames
	private int currentFrameIndex = 0; // tracks the current animation frame
	private long lastFrameTime; // last time the frame was updated
	private static final long FRAME_DURATION = 200_000_000; // 200ms per frame
	private RobotArena arena; // reference to RobotArena arena
	private double rotationAngle = 0; // angle in degrees
	private boolean isMoving = false; // flag to track movement for audio clip
	private ShopkeeperNPC nearbyShopkeeper = null; // track the nearest shopkeeper



	/**
	 * Construct for PlayerRobot class.
	 * Initialises robot position, size, and animation frames
	 * 
	 * @param x - x-coordinate 
	 * @param y - y-coordinate
	 * @param radius - radius of the robot
	 * @param arena - reference to RobotArena instance
	 */
	public PlayerRobot(double x, double y, double radius, RobotArena arena) {
		super(x, y, radius);
		this.arena = arena;
		this.speed = 3.5;
		loadFrames();	
	}
	
	/** 
	 * Method loadFrames - Loads animation frames for the player robot
	 * Frames reference by file path (Asset Folder)
	 */
	private void loadFrames() {
		frames = new Image[] {
		        new Image("file:src/robotCivSim/Assets/playerRobotFrame1.png"),
		        new Image("file:src/robotCivSim/Assets/playerRobotFrame2.png"),
		        new Image("file:src/robotCivSim/Assets/playerRobotFrame3.png"),
		        new Image("file:src/robotCivSim/Assets/playerRobotFrame4.png")
		};
		currentFrameIndex = 0; // start with the first frame
		lastFrameTime = System.nanoTime(); // initialise the last frame
	}
	
	/** 
	 * Method Update - Updates the robot's position based on user input and handles collisions
	 * If movement is blocked by an obstacle or another robot, the player robot stops
	 */
	@Override
	public void update() {
	    double newX = getXPosition() + dx * speed;
	    double newY = getYPosition() + dy * speed;

	    nearbyShopkeeper = null; // Reset nearby shopkeeper detection
	    
	    for (ArenaItem item : arena.getItems()) {
	        if (item instanceof ShopkeeperNPC shopkeeper) { // handle Shopkeeper-specific interaction
	            double dx = newX - shopkeeper.getXPosition();
	            double dy = newY - shopkeeper.getYPosition();
	            double distance = Math.sqrt(dx * dx + dy * dy);

	            if (distance < getRadius() + shopkeeper.getRadius() + 50) { // close enough to trigger "E" indicator
	                nearbyShopkeeper = shopkeeper; // store reference to the nearby shopkeeper
	                break;
	                
	            	}
	            }
	        }
	    
	    // Move the player if not blocked by obstacles
	    if (!arena.checkOverlap(newX, newY, getRadius(), this)) {
	        setPosition(newX, newY, arena.getWidth(), arena.getHeight());
	    }
	    
	    // Update the animation frame
	    if (dx != 0 || dy != 0) { // Only animate when there is movement
		    long currentTime = System.nanoTime(); // current time in nanoseconds
		    if (currentTime - lastFrameTime >= FRAME_DURATION) {
		        currentFrameIndex = (currentFrameIndex + 1) % frames.length; // loop through frames
		        lastFrameTime = currentTime; // reset frame time
		    }
	    }
	}

	
	/** 
	 * Method draw - Draws the player robot on the canvas
	 * Displays the current animation frame at the robot's position
	 * 
	 * @param canvas - MyCanvas instance for rendering
	 */
	@Override
	public void draw(MyCanvas canvas) {
		canvas.save();
		canvas.translate(getXPosition(), getYPosition());
		canvas.rotate(rotationAngle); // rotate PlayerRobot to face direction its moving
		canvas.drawImage(
	            frames[currentFrameIndex],
	            -getRadius() * 1.5, // centre horizontally
	            -getRadius() * 1.5, // centre vertically
	            getRadius() * 3, // scaled width
	            getRadius() * 3  // scaled height
		);
		canvas.restore();
		
		// Draw the "E" key indicator if near a shopkeeper
		if (nearbyShopkeeper != null) {
			nearbyShopkeeper.drawKeyIndicator(canvas);
		}
	}
	
	/**
	 * Method handleKeyPress - Handles key press event to control movement
	 * Updates the direction based on user input (W, A, S, D)
	 * 
	 * @param event - KeyEvent containing the released key information
	 */
	public void handleKeyPress(KeyEvent event) {
		switch (event.getCode()) {
			case W -> { dx = 0; dy = -1; rotationAngle = 270;} // move up
			case A -> { dx = -1; dy = 0; rotationAngle = 180;} // move left
			case S -> { dx = 0; dy = 1; rotationAngle = 90;} // move down
			case D -> { dx = 1; dy = 0; rotationAngle = 0;} // move right
			case E -> { // Press "E" to interact with shopkeeper
				if (nearbyShopkeeper != null) {
					SoundManager.getInstance().playSound("interactShop"); // play the interaction sound
					nearbyShopkeeper.interact(this, arena.getSimulationGUI().getCanvas()); // trigger interaction
				}
				
			}
	        default -> {
	            // Log ignored keys for debugging
	            System.out.println("Unhandled key pressed: " + event.getCode());
	        }
		}
		// If the robot is not already moving, start the movement and play the sound
		if (!isMoving) {
			isMoving = true;
			SoundManager.getInstance().playSound("grass");
		}
	}
	
	/**
	 * Method handleKeyRelease - Handles key release event to control movement
	 * Ensure robot stops moving when user releases key
	 * 
	 * @param event - KeyEvent containing the released key information
	 */
    public void handleKeyRelease(KeyEvent event) {
        switch (event.getCode()) {
            case W, S -> dy = 0; // Stop vertical movement
            case A, D -> dx = 0; // Stop horizontal movement
            default -> {
                // Log ignored keys for debugging
                System.out.println("Unhandled key pressed: " + event.getCode());
            }
        }
        
        // Check if both horizontal and vertical movement has stopped
        if (dx == 0 && dy == 0) {
        	// Reset the movement flag
        	isMoving = false;
        	// Stop playing the "grass" sound
        	SoundManager.getInstance().stopSound("grass");
        }
    }
    
	/** Method getCurrentFrame - Retrieves the current animation frame
	 * 
	 * @return The current image object representing the animation frame
	 */
	public Image getCurrentFrame() {
		return frames[currentFrameIndex]; // frames for lumber robot
	}
	
    
    /** 
     * Robot is not able to be destroyed
     */
    public void destroy() {
    	// Player robot cannot be destroyed.
    }
} 
