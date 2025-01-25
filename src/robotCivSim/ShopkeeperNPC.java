package robotCivSim;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * ShopkeeperNPC class-  Represents a stationary NPC that acts as a shopkeeper.
 * Opens a dialog when interacted with, allowing the player to purchase a LumberRobot or close the menu.
 */
public class ShopkeeperNPC extends NPC_Robot {
	private static final long serialVersionUID = 1L;
	public boolean isInteracting = false; // tracks if the NPC is in interaction state
	
	/**
	 * Constructor for Shopkeeper
	 * 
	 * @param x - x-coordinate of the NPC
	 * @param y - y-coordinate of the NPC
	 * @param radius - radius of the NPC
	 * @param arena - reference to the RobotArena's instance
	 */
	public ShopkeeperNPC(double x, double y, double radius, RobotArena arena) {
		super(x, y, radius, "file:src/robotCivSim/Assets/minerRobotFrame1.png", arena);
	}
	
	/** Method interact - This defines the interaction behaviour when the player collied with the NPC
	 * Opens a dialog to allow the player to purchase a LumberRobot or close the menu
	 * 
	 * @param player - THe PlayerRobot interacting with the NPC.
	 */
	@Override
	public void interact(PlayerRobot player, MyCanvas canvas) {
		
		// Check if the NPC is already in an interaction
		if (isInteracting) {
			return; // do nothing if already interacting
		}
		
		isInteracting = true; // set the interaction flag
		
		// Create a dialog for interaction
		Platform.runLater(() -> {
			try {
				Alert dialog = new Alert(AlertType.CONFIRMATION);
				dialog.setTitle("Shopkeeper");
				dialog.setHeaderText("Welcome to the shop!");
				dialog.setContentText("Choose an action:");
				
				// Add buttons for the dialog
				ButtonType purchaseLumberRobot = new ButtonType("Purchase LumberRobot");
				ButtonType closeMenu = new ButtonType("Close");
				
				dialog.getButtonTypes().setAll(purchaseLumberRobot, closeMenu);
				
				// Handle button actions
				dialog.showAndWait().ifPresent(response -> {
					if(response == purchaseLumberRobot) {
						// Add a LumberRobot to the arena
						double x = player.getXPosition() + 50; // offset new robot's position
						double y = player.getYPosition() + 50; 
						arena.addItem(new LumberRobot(x, y, 20, arena)); // Add the LumberRobot
						if (canvas != null) { // ensure canvas is not null
							canvas.clearCanvas(); // clear the canvas
							arena.drawArena(canvas); // redraw with the new Robot
						}
					}
				});
			} finally {
				// Reset the interaction of the dialog is closed
			    isInteracting = false;
			}
		});
	}
}
