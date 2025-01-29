package robotCivSim;

import java.io.Serializable; // for file save and load

/** ArenaItem - Is an Abstract class which represents an item in the RobotArena.
 * Will define properties such as position and size, as well as, shared methods.
 */
public abstract class ArenaItem implements Serializable { // use of "abstract" for draw method
	@SuppressWarnings("unused") // static means it thinks its unused
	private static final long serialVersionUID = 1L; // serialisation ID
	// Private attributes for position and size
	private double xPosition; // x-coordinate
	private double yPosition; // y-coordinate
	private double radius; // radius for the item
	private RobotArena arena; // reference to the RobotArena
	
	/** Constructor for ArenaItem
	 * Initialises the item's positions and size.
	 * 
	 * @param xPosition - x-coordiante of the item's centre
	 * @param yPosition - y-coordinate of the item's centre
	 * @param radius - radius of the item
	 */
	public ArenaItem(double xPosition, double yPosition, double radius) {
		this.xPosition = xPosition; // initialise x-coordinate
		this.yPosition = yPosition; // initialise y-coordinate
		this.radius = radius; // initialise radius
	}
	
	/** Method getXPositon - This gets the x-coordinate of the item
	 * 
	 * @return x-coordinate
	 */
	public double getXPosition() {
		return xPosition; // get x pos
	}
	
	/** Method setXPosition - This sets the x-coordinate of the item
	 * 
	 * @param xPosition
	 */
	public void setXPosition(double xPosition) {
		this.xPosition = xPosition; // set x position
	}
	
	/** Method getYPosition - This gets the y-coordinate of the item
	 * 
	 * @return y-coordinate
	 */
	public double getYPosition() {
		return yPosition; // get y pos
	}
	
	/** Method setYPosition - This sets the x-coordinate of the item
	 * 
	 * @param yPosition
	 */
	public void setYPosition(double xPosition) {
		this.yPosition = xPosition; // set y position
	}
	
	/** Method getRadius - This gets the radius of the item
	 * 
	 * @return radius
	 */
	public double getRadius() {
		return radius; // get radius
	}
	
	/**
	 * Method setRadius - This sets the radius of the item
	 * 
	 * @param radius - New radius of the item
	 */
	public void setRadius(double radius) {
		this.radius = radius; // set y position
	}
	
	/**
	 * Method getArena - This gets the arena reference
	 * 
	 * @return the RobotArena reference
	 */
	public RobotArena getArena() {
		return arena;
	}
	
	/**
	 * Method setArena - This gets the arena reference
	 * 
	 * @param arena - The RobotArena to which this item belongs
	 */
    public void setArena(RobotArena arena) {
        this.arena = arena;
    }
    
	/** Abstract method to be implemented by subclasses for drawing the item
	 * 
	 * @return void
	 */
	public abstract void draw(MyCanvas canvas);
	
	/** Abstract method to be implemented by subclasses for updating the item
	 * This is used to modify the state of the item, (e.g., move the robots)
	 * 
	 */
	public abstract void update();

	/** Method setPosition - This updates the position of the item
	 * 
	 * @param x - The new x-coordinate
	 * @param y - The new y-coordinate
	 * @param arenaWidth - Width of the arena
	 * @param arenaHeight - Height of the arena
	 */
	public void setPosition(double x, double y, double arenaWidth, double arenaHeight) {
		double newX = Math.max(getRadius(), Math.min(x, arenaWidth - getRadius())); // ensure within horizontal boundary
		double newY = Math.max(getRadius(), Math.min(y, arenaHeight - getRadius())); // ensure within vertical boundary
		
		try {
			java.lang.reflect.Field fieldX = ArenaItem.class.getDeclaredField("xPosition"); // access x position
			java.lang.reflect.Field fieldY = ArenaItem.class.getDeclaredField("yPosition"); // access y position
			
			// Set both fields to be accessible 
			fieldX.setAccessible(true);
			fieldY.setAccessible(true);
			
			// Update x and y position with new ones
			fieldX.set(this, newX);
			fieldY.set(this, newY);
		} catch (NoSuchFieldException e) {
			// Handle case where the field name doesn't exist 
			System.err.println("Field not found: " + e.getMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// Handle the case where access to the field is denied 
			System.err.println("Illegal access to field" + e.getMessage()); 
			e.printStackTrace();
		} catch (Exception e) {
			// Handle any other unexpected exceptions
			System.err.println("Unexpected error when setting position: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Method destroy - This is an abstract method that handles the logic for when the item is destroyed
	 */
	public abstract void destroy();
	
	
	/** Method getName - This retrieves the name of the item (this is overridden in subclasses)
	 * 
	 * @return a string which represents the name of the item
	 */
	public String getName() {
		return "Normal Item";
	}
	
	/** Method getDescription - This retrieves the description of the item (this is also overridden in subclasses)
	 * 
	 * @return a string which represents the description of the item
	 */
	public String getDescription() {
		return "A normal item in the arena.";
	}
}
