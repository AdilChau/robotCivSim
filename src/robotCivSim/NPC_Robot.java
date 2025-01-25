package robotCivSim;

import javafx.scene.image.Image; // for asset renering
import java.io.IOException; // for file save and load
import java.io.ObjectInputStream; // for file save and load
import java.io.ObjectOutputStream; // for file save and load
import java.io.Serializable; // for file save and load


/**
 * This class will be the parent class for all NPC robot types, various shop keepers and other variants will all inherit from here.
 * The class will not require animation frames nor movement as they will be stand still. 
 * Upon collision the robot will open a dialog where it will offer items or guide the user through what to do.
 * The user can purchase items using resources gathered.
 */
public abstract class NPC_Robot extends ArenaItem {
	@SuppressWarnings("unused") // static means it thinks its unused
	private static final long serialVersionUID = 1L; // serialisationID
	private transient Image npcImage; // image representing the NPC
	protected RobotArena arena; // reference to the RobotArena instance
	private boolean isInteracting = false;
	
	/** 
	 * Constructor for the NPC_Robot 
	 * Initialises the NPC's position, size, and image
	 * 
	 * @param x - x-coordinate of the NPC
	 * @param y - y-coordinate of the NPC
	 * @param radius - radius of the NPC
	 * @param imagePath - file path for the NPC's image
	 * @param arena - reference to the RobotArena's instance
	 */
	public NPC_Robot(double x, double y, double radius, String imagePath, RobotArena arena) {
		super(x, y, radius); // take from parent class
		this.arena = arena; // reference RobotArena's instance
		this.npcImage = new Image(imagePath); // load the NPC image
	}
	
	/** 
	 * Abstract Method interact - This defines the behaviour when the player collides with the NPC
	 * Each NPC type will have to implement its own interaction logic as they are all unique
	 */
	public abstract void interact(PlayerRobot player, MyCanvas canvas);
		
	/** 
	 * Method draw - draws the NPC on the canvas
	 * Display's the NPC's image at its position for rendering
	 * 
	 * @param canvas - MyCanvas instance for rendering
	 */
	public void draw(MyCanvas canvas) {
		canvas.save();
		canvas.translate(getXPosition(), getYPosition());
		canvas.drawImage(
				npcImage,
				-getRadius() * 1.5, // centre horizontally
				-getRadius() * 1.5, // centre vertically
				getRadius() * 3, // scale width
				getRadius() * 3 // scale height
		);		
		canvas.restore();
	}
	
	/** 
	 * Method setInteracting - Tracks if the NPC robots are already being interacted with
	 * Prevents multiple dialog boxes opening
	 */
	public void setInteracting(boolean interacting) {
	    this.isInteracting  = interacting;
	}
	
	/**
	 * Method update - Must be included as ShopkeeperNPC extends ArenaItem
	 * However all functionality is handles in interact method
	 */
	@Override
	public void update() {
	    // NPC does not move or update its state over time.
	    // This method is intentionally left empty.
	}
	
	/**
	 * Must have destroy class for ArenaItem parent class 
	 * Ensure NPC Robot does not destroy other items
	 */
	@Override
	public void destroy() {
	    // Default behaviour: do nothing
	    throw new UnsupportedOperationException("This robot does not have destroy capabilities.");
	}
}
