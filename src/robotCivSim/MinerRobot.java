package robotCivSim;

import javafx.scene.image.Image;
import robotCivSim.sound.SoundManager;

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
	private long lastActionTime = 0;
	private ArenaItem rememberedObstacle; // tracks the current obstacle
	private long obstacleAvoidanceEndTime = 0; // tracks when to stop avoiding the obstacle	
	
	/** 
	 * Enum state for prioritising tasks
	 */
	private enum State {
		COLLECTING_RESOURCE,
		TARGETING_ROCK,
		DEFAULT_BEHAVIOR, 
		IDLE
	}
	
	private State currentState = State.IDLE; // initial state
	
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
	 * The robot targets and removes rocks then collects their rock, or behaves like a normal robot if there are no rocks
	 */
	@Override
	public void update() {
	    long currentTime = System.currentTimeMillis();

	    // Handle state-based behaviour
	    switch (currentState) {
	        case COLLECTING_RESOURCE:
	            handleCollectingResource(currentTime);
	            break;

	        case TARGETING_ROCK:
	            handleTargetingRock(currentTime);
	            break;

	        case DEFAULT_BEHAVIOR:
	            super.update(); // use BasicRobot's default update behaviour
	            findNextTask(currentTime);
	            break;    
	            
	        case IDLE:
	            findNextTask(currentTime);
	            break;
	    }

	    // Handle collisions
	    for (ArenaItem item : arena.getItems()) {
	        if (item instanceof ResourceItem || item instanceof Obstacle) continue; // Skip rocks and resources
	        if (item != this && checkCollision(item)) {
	            dx = -dx;
	            dy = -dy;
	            break;
	        }
	    }
	}

	/** Method handleCollectingResource - This is for the collecting resources 
	 * 
	 * @param currentTime 
	 */
	private void handleCollectingResource(long currentTime) {
	    ResourceItem resourceToCollect = findClosestResource();
	    if (resourceToCollect == null || !resourceToCollect.isReadyToCollect()) {
	        currentState = State.DEFAULT_BEHAVIOR; // go back to default behaviour if no resource to collect
	        return;
	    }
	
	    double dxToResource = resourceToCollect.getXPosition() - getXPosition();
	    double dyToResource = resourceToCollect.getYPosition() - getYPosition();
	    double distanceToResource = Math.sqrt(dxToResource * dxToResource + dyToResource * dyToResource);
	
	    if (distanceToResource < getRadius() + resourceToCollect.getRadius()) {
	    	arena.scheduleRemoval(resourceToCollect); // schedule removal
	        resourceToCollect.destroy(); // collect the resource

	        SoundManager.getInstance().playSound("itemCollect"); // play the itemCollect sound
	        
	        SimulationGUI gui = getArena().getSimulationGUI();
	        if (gui != null) {
	            gui.incrementRockResource(); // Increment rock counter in GUI
	        }
	        
	        lastActionTime = currentTime; // set cooldown
	        currentState = State.DEFAULT_BEHAVIOR; // return to default behaviour after collecting 
	    } else {
	        double angleToResource = Math.atan2(dyToResource, dxToResource);
	        dx = Math.cos(angleToResource) * speed;
	        dy = Math.sin(angleToResource) * speed;
	
	        setXPosition(getXPosition() + dx);
	        setYPosition(getYPosition() + dy);
	    }
	}
		
	/** Method handleTargetingRock - Guides the MinerRobot towards the target rock
	 * Includes simple obstacle avoidance behaviour by adjusting direction dynamically
	 * If the robot reaches the rock, it mines it and collects the dropped resource
	 * If the path is blocked, the robot attempts to steer around obstacles
	 * 
	 * @param currentTime - The current system time in milliseconds
	 */
	private void handleTargetingRock(long currentTime) {
	    if (targetRock == null || targetRock.getArena() == null) {
	        currentState = State.DEFAULT_BEHAVIOR;
	        return;
	    }

	    double dxToTree = targetRock.getXPosition() - getXPosition();
	    double dyToTree = targetRock.getYPosition() - getYPosition();
	    double distanceToTree = Math.sqrt(dxToTree * dxToTree + dyToTree * dyToTree);

	    if (distanceToTree < getRadius() + targetRock.getRadius()) {
	        mineRock();
	        return;
	    }

	    if (rememberedObstacle != null && currentTime < obstacleAvoidanceEndTime) {
	        avoidObstacle(rememberedObstacle);
	    } else {
	        double angleToTree = Math.atan2(dyToTree, dxToTree);
	        dx = Math.cos(angleToTree) * speed;
	        dy = Math.sin(angleToTree) * speed;

	        for (ArenaItem item : arena.getItems()) {
	            if (item != this && item != targetRock && checkCollision(item)) {
	                rememberedObstacle = item;
	                obstacleAvoidanceEndTime = currentTime + 2000; // Avoid obstacle for 2 seconds
	                avoidObstacle(item);
	                return;
	            }
	        }

	        setXPosition(getXPosition() + dx);
	        setYPosition(getYPosition() + dy);
	    }
	}

	/** Method mineRock - Mines the target rock and schedules the dropped resource for addition to the arena.
	 */
	private void mineRock() {
	    if (targetRock instanceof Obstacle && "rock".equals(((Obstacle) targetRock).getType())) {
	        targetRock.destroy(); // Mine the rock, triggering its destruction
	        
	        SoundManager.getInstance().playSound("mine"); // play mine sound
	        
	        targetRock = null; // Clear the current target
	        currentState = State.IDLE; // Revert to idle state
	        lastActionTime = System.currentTimeMillis(); // set cooldown
	    }
	}

	/** Method avoidObstacle - Adjusts the robot's direction to avoid a blocking obstacle.
	 * 
	 * @param obstacle - The obstacle to avoid
	 */
	private void avoidObstacle(ArenaItem obstacle) {
	    double angleToObstacle = Math.atan2(obstacle.getYPosition() - getYPosition(),
	                                        obstacle.getXPosition() - getXPosition());
	    double avoidanceAngle = angleToObstacle + Math.PI / 2; // Perpendicular direction
	    dx = Math.cos(avoidanceAngle) * speed;
	    dy = Math.sin(avoidanceAngle) * speed;

	    setXPosition(getXPosition() + dx);
	    setYPosition(getYPosition() + dy);
	}
	
	/** Method findNextTask - This is for finding the next task
	 * 
	 * @param currentTime 
	 */
	private void findNextTask(long currentTime) {
	    // Wait during cooldown
	    if (currentTime - lastActionTime < 1000) {
	    	return;
	    }
	    
	    ResourceItem closestResource = findClosestResource();
	    if (closestResource != null && closestResource.isReadyToCollect()) {
	        currentState = State.COLLECTING_RESOURCE; // prioritise resource collection
	        return;
	    } 
	    
	    // If no resource, look for the nearest rock
        targetRock = findClosestRock();
	    if (targetRock != null) {
	          currentState = State.TARGETING_ROCK; // target rock if no resource
	          return;
	    } 
	    
	    // If no tasks are available, go default behaviour
	    currentState = State.DEFAULT_BEHAVIOR; // stay  if no task available
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
	
	
	/** Method findClosestResource - This finds the nearest rock resource in the arena 
	 * 
	 * @return The closest rock resource, or null if none exists
	 */
	private ResourceItem findClosestResource() {
	    ResourceItem closestResource = null;
	    double closestDistance = Double.MAX_VALUE;

	    // Iterate through all items in the arena
	    for (ArenaItem item : arena.getItems()) {
	        if (item instanceof RockResource) { // Only consider rock resource
	            ResourceItem resource = (ResourceItem) item;
	            double dx = resource.getXPosition() - getXPosition();
	            double dy = resource.getYPosition() - getYPosition();
	            double distance = Math.sqrt(dx * dx + dy * dy);

	            // Check if this resource is closer
	            if (distance < closestDistance) {
	                closestDistance = distance;
	                closestResource = resource;
	            }
	        }
	    }

	    return closestResource; // Return the closest resource, or null if none exist
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
                -getRadius() * 1.5, // centring the image horizontally
                -getRadius() * 1.5, // centring the image vertically
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
