package robotCivSim;

import javafx.scene.image.Image;


/**
 * EnemyRobot - Superclass for all enemy robots.
 * Handles movement, targeting, and attacks on worker robots.
 */

public abstract class EnemyRobot extends ArenaItem {
    private static final long serialVersionUID = 1L;
    protected RobotArena arena;
    protected double speed;
    protected transient Image[] frames; // animation frames
    protected int currentFrameIndex = 0;
    protected long lastFrameTime;
    protected static final long FRAME_DURATION = 200_000_000; // 200ms per frame
	
    /**
     * Constructor for EnemyRobot 
     * 
     * @param x - x position
     * @param y - y position
     * @param radius - size of the robot
     * @param speed - speed of the robot
     * @param arena - reference to the RobotArena
     */
    public EnemyRobot(double x, double y, double radius, double speed, RobotArena arena) {
    	super(x, y, radius);
    	this.arena = arena;
    	this.speed = speed;
    	loadFrames();
    }
    
    /**
     * Method loadFrames - Loads animation frames for the enemy robots
     * Will be unique in all subclasses
     */
    protected abstract void loadFrames(); // each subclass must define its own animation frames

    /**
     * Method Update - Updates the enemy behaviour (movement & attacking)
     */
    @Override
    public void update() {
    	ArenaItem target = findTarget();
    	
    	// If there is a target move towards it
    	if (target != null) {
    		moveToward(target);
    		
    		// If contact is made, attack the target
    		if (isColliding(target)) {
    			attack(target); 
    		}
    	}
    	
    	updateAnimation();
    }
    
    /**
     * Method findTarget - Finds the closest worker robot to target
     * 
     * @return The closest LumberRobot or MinerRobot
     */
    protected ArenaItem findTarget() {
    	ArenaItem closest = null;
    	double closestDistance = Double.MAX_VALUE;
    	
        for (ArenaItem item : arena.getItems()) {
            if (item instanceof LumberRobot || item instanceof MinerRobot) { // only target worker robots
                double distance = calculateDistance(item); 

                if (distance < closestDistance) {
                    closestDistance = distance; // update to closest target
                    closest = item;
                }
            }
        }
        return closest; // closest target 
    }
    
    /**
     * Method moveToward - Moves the enemy toward the target
     * 
     * @param target - The robot being pursued.
     */
    protected void moveToward(ArenaItem target) {
        double dx = target.getXPosition() - getXPosition();
        double dy = target.getYPosition() - getYPosition();
        double length = Math.sqrt(dx * dx + dy * dy);

        if (length > 0) { // prevent division by zero
            setXPosition(getXPosition() + (dx / length) * speed);
            setYPosition(getYPosition() + (dy / length) * speed);
        }
    }
    
    /** 
     * Method isColliding - Checks if the enemy robot is colliding with another item
     * 
     * @param item - The item to check collision with
     * @return true - If colliding
     */
    protected boolean isColliding(ArenaItem item) {
    	double distance = calculateDistance(item);
    	return distance < getRadius() + item.getRadius();
    }
    
    /**
     * Method attack - Attacks the target robot
     * 
     * @param target - the worker robot
     */
    protected void attack(ArenaItem target) {
    	arena.removeItem(target); // destroy the target 
    }
    
    /**
     * Method calculateDistance - Calculates the distance between this enemy and another arena item
     * 
     * @param item - The item to measure distance to
     * @return The distance value
     */
    protected double calculateDistance(ArenaItem item) {
    	double dx = item.getXPosition() - getXPosition();
    	double dy = item.getYPosition() - getYPosition();
    	return Math.sqrt(dx * dx + dy * dy);
    }
    
    /** 
     * Method updateAnimation - Updates the enemy's animation frame 
     */
    protected void updateAnimation() {
    	long currentTime = System.nanoTime();
    	if (currentTime - lastFrameTime >= FRAME_DURATION) {
    		currentFrameIndex = (currentFrameIndex + 1) % frames.length;
    		lastFrameTime = currentTime;
    	}
    }
    
    /**
     * Method draw - Draws the enemy with its current animation frame
     * 
     * @param canvas - A reference to MyCanvas
     */
    @Override
    public void draw(MyCanvas canvas) {
        canvas.drawImage(frames[currentFrameIndex], getXPosition() - getRadius(),
                getYPosition() - getRadius(), getRadius() * 2, getRadius() * 2);
    }	
}
