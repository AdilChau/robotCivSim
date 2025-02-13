package robotCivSim;

import java.util.ArrayList; // import ArrayList
import java.util.HashSet; // for unique removal tracking
import java.util.Set; // for using hashset as a set
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
	private transient List<ArenaItem> itemsToAdd = new ArrayList<>(); // list of items to add
	private transient SimulationGUI simulationGUI;
	private transient MyCanvas canvas; // canvas used for drawing (transient to avoid serialisation)
	private int woodResourceCount = 5; // start with 5 wood resources by default


	
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
	
	/** Method addItem - This adds an item to the arena
	 * 
	 * @param item - the ArenaItem to be added
	 */
	public void addItem(ArenaItem item) {
		item.setArena(this); // set the arena reference
		items.add(item);
	}
	
	/** Method drawArena - This draws the arena and all its items on the provided canvas
	 * 
	 * @param canvas - MyCanvas object used for rendering
	 */
	public void drawArena(MyCanvas canvas) {
		canvas.clearCanvas(); // clear the canvas before drawing
		
		// Draw the arena border
		// The fill colour is set to null for a transparent background 
		canvas.drawRectangle(0, 0, canvas.getCanvasWidth(), canvas.getCanvasHeight(), Color.GREEN, Color.BLACK, 5.0); 
		
		// Draw each item in the arena by looping through ArenaItem items
		for (ArenaItem item : items) {
			item.draw(canvas); // call the draw method for each item
		}
	}
	
	
	/** Method getItems - This gets all items in the arena
	 * 
	 * @return list of all ArenaItems
	 */
	public ArrayList<ArenaItem> getItems() {
		return items; // get items
	}
	
	/** Method getWidth - This gets the arena width
	 * 
	 * @return width
	 */
	public double getWidth() {
		return width; // get width
	}
	
	/** Method getHeight - This gets the arena height
	 * 
	 * @return height
	 */
	public double getHeight() {
		return height; // get height
	} 
	
	/** Method getCanvas - This gets the canvas
	 * 
	 * @return canvas
	 */
	public MyCanvas getCanvas() {
	    return this.canvas; 
	}
	
	/** Method checkOverlap - This checks if a given position overlaps with any existing item in the arena
	 * Ensures that when adding new obstacles and robots they don't overlap with already existing ones
	 * 
	 * @param x - x-coordinate to check
	 * @param y - y-coordinate to check
	 * @param radius - radius of the new item
	 * @return true if an overlap is detected, otherwise re turn false
	 */
	public boolean checkOverlap(double x, double y, double radius, ArenaItem self) {
	    for (ArenaItem item : items) {
	        if (item == self) {
	            continue; // Skip self
	        }
	        double dx = x - item.getXPosition();
	        double dy = y - item.getYPosition();
	        double distance = Math.sqrt(dx * dx + dy * dy);

	        if (distance < (radius + item.getRadius())) {
	            return true; // Overlap detected
	        }
	    }
	    return false; // No overlap
	}

	    
		/** Method incrementWoodResouce - This adds 1 to the total wood resource count
		 * 
		 */
	    // Increment the wood resource count
	    public void incrementWoodResource() {
	        woodResourceCount++;
	        
	        // Once 10 wood has been collected notify the player to go to the shop
	        if (woodResourceCount >= 10) {
	        	if (simulationGUI != null) {
	        		simulationGUI.notifyPlayer(); 
	        	}
	        }
	    }
	    
	    /** Method decrementWoodResource - This takes one away from the total wood count (for purchasing items etc...)
	     * 
	     * @param amount
	     */
	    // Decrement the wood resource count
	    public void decrementWoodResource(int amount) {
	        woodResourceCount -= amount;
	        simulationGUI.updateResourceDisplay(); // Automatically update the GUI
	    }

	    /** Getter getWoodResourceCount - Returns the woodResourceCount
	     * 
	     * @return - The wood resource count
	     */
	    // Get the current wood resource count
	    public int getWoodResourceCount() {
	        return woodResourceCount;
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
	
	/** Method scheduleRemoval - This schedules an item for removal after iteration
	 * 
	 * @param item - The ArenaItem to remove
	 */
	public void scheduleRemoval(ArenaItem item) {
		if (itemsToRemove == null) {
			itemsToRemove = new HashSet<>();
		}
		itemsToRemove.add(item); // add to removal set
	}
	
	/** Method scheduleAddition - Schedules an item to be added after iteration is complete
	 * 
	 * @param item - The ArenaItem to be added
	 */
	public void scheduleAddition(ArenaItem item) {
		if (itemsToAdd == null) {
			itemsToAdd = new ArrayList<>();
		}
		itemsToAdd.add(item);
		
	}
	
	/** Method processRemovals - This removes all items that are marked for removal
	 * It ensures a safe removal of items after iteration (Helped fix ConcurrentModification error)
	 */
	public void processRemovals() {
	    // Remove all scheduled items
	    if (itemsToRemove != null) {
	        items.removeAll(itemsToRemove); // remove all scheduled items
	        itemsToRemove.clear(); // clear the removal set
	    }

	    // Add all scheduled items
	    if (itemsToAdd != null && !itemsToAdd.isEmpty()) {
	        items.addAll(itemsToAdd); // add all scheduled items
	        itemsToAdd.clear(); // clear the addition set
	    }
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
	    itemsToAdd = new ArrayList<>(); // ensure itemsToAdd is initialised
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
	
	/**
	 * Method removeItem - Removes an item from the arena.
	 * 
	 * @param item - The ArenaItem to be removed
	 */
	public void removeItem(ArenaItem item) {
	    items.remove(item); // remove the item from the list
	}
	
	/** Setter for simulationGUI
	 * 
	 */
	public void setSimulationGUI(SimulationGUI simulationGUI) {
	    this.simulationGUI = simulationGUI;
	}
	
	/** Getter for simulationGUI
	 * 
	 */
	public SimulationGUI getSimulationGUI() {
	    return simulationGUI;
	}

}

 