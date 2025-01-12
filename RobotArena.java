package robotSimGUI;

import java.util.ArrayList; // import ArrayList
import java.util.HashSet; // for unique removal tracking
import java.util.List; // to retrieve a list of basic robots

import javafx.scene.paint.Color; // import Color for border

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable; // for file save and load

/** RobotArena - Manages the arena, including size and the items within it.
 * Stores and interacts with robots and obstacles. 
 * 
 */
public class RobotArena implements Serializable {
	private static final long serialVersionUID = 1L; // serialisation ID
	// Attributes for arena size and the ArrayList of items
	private double width; // width of arena
	private double height; // height of arena
	private ArrayList<ArenaItem> items; // make an ArrayList of ArenaItems
	private transient HashSet<ArenaItem> itemsToRemove; // transient to avoid serialisation
	
	/** Constructor for RobotArena 
	 * Initialises the arena with specified dimensions and an empty list of items
	 * 
	 * @param width - width of the arena
	 * @param height - height of the arena
	 */
	public RobotArena(double width, double height) {
		this.width = width; // initialise width
		this.height = height; // initialise height
		this.items = new ArrayList<>(); // create an empty list of items
		this.itemsToRemove = new HashSet<>(); // initialise the removal set
	}
	
	/** Method addItem - adds an item to the arena
	 * 
	 * @param item - the ArenaItem to be added
	 */
	public void addItem(ArenaItem item) {
		items.add(item);
	}
	
	/** Method drawArena - Draws the arena and all its items on the provided canvas
	 * 
	 * @param canvas - MyCanvas object used for rendering
	 */
	public void drawArena(MyCanvas canvas) {
		canvas.clearCanvas(); // clear the canvas before drawing
		
		// Draw the arena border
		// The fill colour is set to null for a transparent background 
		canvas.drawRectangle(0, 0, canvas.getCanvasWidth(), canvas.getCanvasHeight(), null, Color.BLACK, 5.0); 
		
		// Draw each item in the arena by looping through ArenaItem items
		for (ArenaItem item : items) {
			item.draw(canvas); // call the draw method for each item
		}
	}
	
	
	/** Method getItems - gets all items in the arena
	 * 
	 * @return list of all ArenaItems
	 */
	public ArrayList<ArenaItem> getItems() {
		return items; // get items
	}
	
	/** Method getWidth - gets the arena width
	 * 
	 * @return width
	 */
	public double getWidth() {
		return width; // get width
	}
	
	/** Method getHeight - gets the arena height
	 * 
	 * @return height
	 */
	public double getHeight() {
		return height; // get height
	}
	
	/** Method checkOverlap - Checks if a given position overlaps with any existing item in the arena
	 * Ensures that when adding new obstacles and robots they don't overlap with already existing ones
	 * 
	 * @param x - x-coordinate to check
	 * @param y - y-coordinate to check
	 * @param radius - radius of the new item
	 * @return true if an overlap is detected, otherwise re turn false
	 */
	public boolean checkOverlap(double x, double y, double radius) {
		// Loop through all items in the arena to check for any overlap
		for (ArenaItem item : items) {
			// Calculate the distance between the given coordinates and the current item's position
			double dx = x - item.getXPosition(); // difference in x-coordinate
			double dy = y - item.getYPosition(); // difference in y-coordinate
			double distance = Math.sqrt(dx * dx + dy * dy); // compute Euclidean distance
			
			// Check if the distance is less than the sum of the radii (this would indicate an overlap)
			if (distance < (radius + item.getRadius())) {
				return true; // overlap detected
			}
		}
		return false; // no overlap detected
	}
	
	/** Method clearArena - This clears all the items from the arena
	 * Effectively removing all obstacles, robots, and other entities
	 */
	public void clearArena() {
		items.clear(); // clear all items from the list
	}
	
	/** Method getBasicRobots - This retrieves all basic robots in the arena
	*
	*@return A list of basic robots
	*/
	public List<Robot> getBasicRobots() {
		List<Robot> basicRobots = new ArrayList<>(); // initialise list
		for (ArenaItem item : items) {
			if (item instanceof Robot && !(item instanceof PredatorRobot)) { // only include basic robots
				basicRobots.add((Robot) item);
			}
		}
		return basicRobots; // return list of basic robots
	}
	
	/** Method scheduleRemoval - Schedules an item for removal after iteration
	 * 
	 * @param item - The ArenaItem to remove
	 */
	public void scheduleRemoval(ArenaItem item) {
		if (itemsToRemove == null) {
			itemsToRemove = new HashSet<>();
		}
		itemsToRemove.add(item); // add to removal set
	}
	
	/** Method processRemovals - Removes all items that are marked for removal
	 * It ensures a safe removal of items after iteration (Helped fix ConcurrentModification error)
	 */
	public void processRemovals() {
		items.removeAll(itemsToRemove); // remove all scheduled items
		itemsToRemove.clear(); // clear the removal sets
	}
	
	/** Method to deserialise the logic for RobotArena
	 * This method ensures that transient fields, such as itemsToRemove are properly reinitialised after deserialisation
	 *  
	 * 
	 * @param ois - The ObjectInputStream used for reading the serialised object
	 * @throws IOException - If an I/O error occurs during deserialisation
	 * @throws ClassNotFoundException - If the class of a serialised object cannot be found 
	 */
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
	    ois.defaultReadObject();
	    itemsToRemove = new HashSet<>(); // reinitialize transient field
	}
	
	/** Method findItemAt - This finds the item at the given coordinates
	 * 
	 * @param x - The x-coordinate of the click
	 * @param y - The y-coordinate of the click
	 * @return The ArenaItem at the given position, or null if none found
	 */
	public ArenaItem findItemAt(double x, double y ) {
		for (ArenaItem item : items) {
			double dx = x - item.getXPosition(); // find x
			double dy = y - item.getYPosition(); // find y
			double distance = Math.sqrt(dx * dx + dy * dy); // calculate distance 
			
			// Check if the click is within the item's radius
			if (distance <= item.getRadius()) {
				return item;
			}
		}
		return null; // no item found
	}

}

 