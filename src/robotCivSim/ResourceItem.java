package robotCivSim;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * ResourceItem - Represents a dropped resource item in the arena.
 */
public abstract class ResourceItem extends ArenaItem {
	private static final long serialVersionUID = 1L;
	private double originalY; // for bobbing animation
	private double bobbingOffset = 0; // current bobbing offset
	private boolean movingUp = true; // animation direction
	private static final double BOBBING_SPEED = 0.1; // speed of bobbing animation
	private long spawnTime; // time the resource was dropped
	
	/** Constructor for ResourceItem
	 * 
	 * @param x - x - coordinate
	 * @param y - y - coordinate
	 * @param size - size of the item
	 * @param resourceType - type of resource ("WOOD", "ROCK")
	 */
	public ResourceItem(double x, double y, double size) {
		super(x, y, size); // call ArenaItem constructor
		this.originalY = y; // store the original y-coordinate for bobbing
		this.spawnTime = System.currentTimeMillis(); // set the spawn time
	}
	
	/**
	 * Method Update to determine bobbing animation
	 */
	@Override
	public void update() {
	    // Increment the offset to create a smooth loop
	    bobbingOffset += BOBBING_SPEED;

	    // Use sine function for a smooth looping animation within a set range
	    double loopedOffset = Math.sin(bobbingOffset) * 5; // 5 is the amplitude of the bobbing motion

	    // Update the y-coordinate based on the original position and looping offset
	    setYPosition(originalY + loopedOffset);
	}
	
	/**
	 * Method Draw - to draw the resource item
	 * 
	 * @param canvas - MyCanvas object used for rendering
	 */
	@Override
	public void draw(MyCanvas canvas) {
	    // Draw the resource item
	    Image resourceImage = getResourceImage();
	    if (resourceImage != null) {
	        canvas.drawImage(resourceImage, 
	            getXPosition() - getRadius(), // X position adjusted for centre
	            getYPosition() - getRadius(), // Y position adjusted for centre
	            getRadius() * 2, // Width of the resource item
	            getRadius() * 2  // Height of the resource item
	        );
	    }
	}

	/**
	 * Method Destroy - This destroys an item and drops a resource
	 * It checks the type of the obstacle and drops the appropriate resource item.
	 */
	@Override
	public void destroy() {
	    if (getArena() != null) {
	        if (getArena().getItems().contains(this)) {
	            getArena().scheduleRemoval(this); // safely remove resource from the arena
	        }
	    }
	}
	
	/**
	 * Method isReadyToCollect - This checks if the resource is ready to be collected
	 * 
	 * @return true if the resource can be collected, false otherwise
	 */
	public boolean isReadyToCollect() {
		return System.currentTimeMillis() - spawnTime > 1; // almost 0 delayI want you to help me with a project I have bee
	}
	
    /**
     * Abstract method getResourceImage - Subclasses provide their specific image.
     * 
     * @return Image of the resource item
     */
    protected abstract Image getResourceImage();
}
