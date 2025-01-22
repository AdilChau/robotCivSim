package robotCivSim;

import javafx.scene.image.Image;

import java.io.IOException; // for file save and load
import java.io.ObjectInputStream; // for file save and load
import java.io.Serializable; // for file save and load


/** LumberRobot - A robot that targets trees and removes them on collision.
 * Inherits from the robot class and overrides specific behaviours to handle trees.
 * Uses A* pathfinding algorithm to navigate the arena and avoid obstacles
 */
public class LumberRobot extends Robot implements Serializable {
	private static final long serialVersionUID = 1L; // serialisation ID
	private Obstacle targetTree; // the current tree target
	private transient Image[] frames; // animation frames for the LumberRobot (transient for serialisation)
	private long lastActionTime = 0; 
	private ArenaItem rememberedObstacle; // tracks the current obstacle
	private long obstacleAvoidanceEndTime = 0; // tracks when to stop avoiding the obstacle
	
	/** 
	 * Enum state for prioritising tasks
	 */
	private enum State {
		COLLECTING_RESOURCE,
		TARGETING_TREE,
		DEFAULT_BEHAVIOR, 
		IDLE
	}
	
	private State currentState = State.IDLE; // initial state
	
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
	 * The robot targets and removes trees then collects their wood, or behaves like a normal robot if there are no trees
	 */
	@Override
	public void update() {
	    long currentTime = System.currentTimeMillis();

	    // Handle state-based behaviour
	    switch (currentState) {
	        case COLLECTING_RESOURCE:
	            handleCollectingResource(currentTime);
	            break;

	        case TARGETING_TREE:
	            handleTargetingTree(currentTime);
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
	        if (item instanceof ResourceItem || item instanceof Obstacle) continue; // Skip tree and resources
	        if (item != this && checkCollision(item)) {
	            dx = -dx;
	            dy = -dy;
	            break;                
	        }
	    }
	}

	/** Method handleCollectingResource - This is for the collecting resources 
	 * 
	 * @param currentTime - The current system time in milliseconds
	 */
	private void handleCollectingResource(long currentTime) {
	    ResourceItem resourceToCollect = findClosestResource();
	    if (resourceToCollect == null || !resourceToCollect.isReadyToCollect()) {
	        currentState = State.IDLE; // go back to idle if no resource to collect
	        return;
	    }
	
	    double dxToResource = resourceToCollect.getXPosition() - getXPosition();
	    double dyToResource = resourceToCollect.getYPosition() - getYPosition();
	    double distanceToResource = Math.sqrt(dxToResource * dxToResource + dyToResource * dyToResource);
	
	    if (distanceToResource < getRadius() + resourceToCollect.getRadius()) {
	    	arena.scheduleRemoval(resourceToCollect); // schedule removal
	        resourceToCollect.destroy(); // collect the resource 
	        
	        SimulationGUI gui = getArena().getSimulationGUI();
	        if (gui != null) {
	            gui.incrementWoodResource(); // Increment wood counter in GUI
	        }
	        
	        lastActionTime = currentTime; // set cooldown
	        currentState = State.IDLE; // return to idle after collecting 
	    } else {
	        double angleToResource = Math.atan2(dyToResource, dxToResource);
	        dx = Math.cos(angleToResource) * speed;
	        dy = Math.sin(angleToResource) * speed;
	
	        setXPosition(getXPosition() + dx);
	        setYPosition(getYPosition() + dy);
	    }
	}
	
	/** Method handleTargetingTree - Guides the LumberRobot towards the target tree.
	 * Includes simple obstacle avoidance behaviour by adjusting direction dynamically.
	 * If the robot reaches the tree, it chops it and collects the dropped resource.
	 * If the path is blocked, the robot attempts to steer around obstacles.
	 * 
	 * @param currentTime - The current system time in milliseconds
	 */
	private void handleTargetingTree(long currentTime) {
	    if (targetTree == null || targetTree.getArena() == null) {
	        currentState = State.IDLE;
	        return;
	    }

	    double dxToTree = targetTree.getXPosition() - getXPosition();
	    double dyToTree = targetTree.getYPosition() - getYPosition();
	    double distanceToTree = Math.sqrt(dxToTree * dxToTree + dyToTree * dyToTree);

	    if (distanceToTree < getRadius() + targetTree.getRadius()) {
	        chopTree();
	        return;
	    }

	    if (rememberedObstacle != null && currentTime < obstacleAvoidanceEndTime) {
	        avoidObstacle(rememberedObstacle);
	    } else {
	        double angleToTree = Math.atan2(dyToTree, dxToTree);
	        dx = Math.cos(angleToTree) * speed;
	        dy = Math.sin(angleToTree) * speed;

	        for (ArenaItem item : arena.getItems()) {
	            if (item != this && item != targetTree && checkCollision(item)) {
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

	/** Method chopTree - Chops the target tree and schedules the dropped resource for addition to the arena.
	 */
	private void chopTree() {
	    if (targetTree instanceof Obstacle && "tree".equals(((Obstacle) targetTree).getType())) {
	        targetTree.destroy(); // Mine the rock, triggering its destruction
	        targetTree = null; // Clear the current target
	        currentState = State.IDLE; // Revert to idle state
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
	        currentState = State.COLLECTING_RESOURCE; // Prioritize resource collection
	        return;
	    }

	    // If no resource, look for the nearest tree
	    targetTree = findClosestTree();
	    if (targetTree != null) {
	        currentState = State.TARGETING_TREE; // Target tree if no resource
	        return;
	    }

	    // If no tasks are available, go idle
	    currentState = State.DEFAULT_BEHAVIOR; // Stay idle if no task available
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
	
	
	/** Method findClosestResource - This finds the nearest wood resource in the arena 
	 * 
	 * @return The closest wood resource, or null if none exists
	 */
	private ResourceItem findClosestResource() {
	    ResourceItem closestResource = null;
	    double closestDistance = Double.MAX_VALUE;

	    // Iterate through all items in the arena
	    for (ArenaItem item : arena.getItems()) {
	        if (item instanceof WoodResource) { // Only consider wood resource
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
                -getRadius() * 1.5, // centring the image horizontally
                -getRadius() * 1.5, // centring the image vertically
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
	    ois.defaultReadObject(); // deserialise non-transient fields
	    
	    // Reinitialise the transient frames array
	    frames = new Image[] {
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame1.png"),
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame2.png"),
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame3.png"),
	        new Image("file:src/robotCivSim/Assets/lumberRobotFrame4.png")
	    };
	}
}
