package robotCivSim;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;


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
		super(x, y, radius, "file:src/robotCivSim/Assets/shopkeeperNPC.png", arena);
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
	        return; // Do nothing if already interacting
	    }

	    isInteracting = true; // Set the interaction flag

	    Platform.runLater(() -> {
	        try {
	            // Create a new stage for the custom dialog
	            Stage dialog = new Stage();
	            dialog.setTitle("Shopkeeper");

	            // Create the main VBox layout
	            VBox layout = new VBox(15);
	            layout.setAlignment(Pos.CENTER);
	            layout.setStyle("-fx-padding: 20; -fx-background-color: #3bcdda; -fx-border-color: black; -fx-border-width: 2;");

	            // Add a title
	            Label titleLabel = new Label("Welcome to the shop!");
	            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

	            // Add an image for the shopkeeper (optional)
	            ImageView shopkeeperImage = new ImageView(new Image("file:src/robotCivSim/Assets/shopkeeperNPC.png"));
	            shopkeeperImage.setFitWidth(100);
	            shopkeeperImage.setFitHeight(100);

	            // Add a message
	            Label messageLabel = new Label("Choose an action:");
	            messageLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #444;");

	            // Create a button to purchase a LumberRobot with a cost display
	            HBox purchaseButtonContainer = new HBox(10);
	            purchaseButtonContainer.setAlignment(Pos.CENTER);

	            Button purchaseButton = new Button("Purchase LumberRobot");
	            purchaseButton.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #28a745; -fx-text-fill: white;");

	            // Add a wood resource icon and cost
	            ImageView woodIcon = new ImageView(new Image("file:src/robotCivSim/Assets/wood.png"));
	            woodIcon.setFitWidth(50); // scale wood icon image
	            woodIcon.setFitHeight(50); // scale wood icon image
	            Label woodCostLabel = new Label("x5");
	            woodCostLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
	            purchaseButtonContainer.getChildren().addAll(purchaseButton, woodIcon, woodCostLabel);

	            // Create a close button
	            Button closeButton = new Button("Close");
	            closeButton.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #dc3545; -fx-text-fill: white;");

	            // Add event handling for the purchase button
	            purchaseButton.setOnAction(e -> {
	                if (arena.getWoodResourceCount() >= 5) { // Ensure the player has enough wood
	                    arena.decrementWoodResource(5); // Deduct 5 wood
	                    double x = player.getXPosition() + 50; // Offset new robot's position
	                    double y = player.getYPosition() + 50;
	                    arena.addItem(new LumberRobot(x, y, 20, arena)); // Add the LumberRobot

	                    if (canvas != null) {
	                        canvas.clearCanvas();
	                        arena.drawArena(canvas); // Redraw the arena
	                    }
	                } else {
	                    // Show a warning if insufficient resources
	                    Alert notEnoughResources = new Alert(Alert.AlertType.WARNING);
	                    notEnoughResources.setTitle("Insufficient Resources");
	                    notEnoughResources.setHeaderText(null);
	                    notEnoughResources.setContentText("You need at least 5 wood to purchase a LumberRobot!");
	                    notEnoughResources.showAndWait();
	                }
	            });

	            // Add event handling for the close button
	            closeButton.setOnAction(e -> dialog.close());

	            // Assemble the layout
	            layout.getChildren().addAll(titleLabel, shopkeeperImage, messageLabel, purchaseButtonContainer, closeButton);

	            // Create and set the scene
	            Scene scene = new Scene(layout, 300, 400);
	            dialog.setScene(scene);
	            dialog.showAndWait();
	        } finally {
	            isInteracting = false; // Reset interaction state when dialog is closed
	        }
	    });
	}

}
