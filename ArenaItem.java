package robotSimGUI;


/** ArenaItem - Is an Abstract class which represents an item in the RobotArena.
 * Will define properties such as position and size, as well as, shared methods.
 */
public abstract class ArenaItem { // use of "abstract" for draw method
	// Private attributes for position and size
	private double xPosition; // x-coordinate
	private double yPosition; // y-coordinate
	private double radius; // radius for the item
	
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
	
	/** Method getXPositon - to get the x-coordinate of the item
	 * 
	 * @return x-coordinate
	 */
	public double getXPosition() {
		return xPosition; // get x pos
	}
	
	/** Method getYPosition - to get the y-coordinate of the item
	 * 
	 * @return y-coordinate
	 */
	public double getYPosition() {
		return yPosition; // get y pos
	}
	
	/** Method getRadius - to get the radius of the item
	 * 
	 * @return radius
	 */
	public double getRadius() {
		return radius; // get radius
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
}
