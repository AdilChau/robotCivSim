package robotCivSim;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import robotCivSim.sound.SoundManager;
import javafx.scene.control.Label;

import java.io.File;
import java.util.ArrayList; 
import java.util.List;


/**
 * ShopkeeperNPC class-  Represents a stationary NPC that acts as a shopkeeper.
 * Opens a shop interface to allow the player to purchase various items. 
 */
public class ShopkeeperNPC extends NPC_Robot {
	private static final long serialVersionUID = 1L;
	public boolean isInteracting = false; // tracks if the NPC is in interaction state
	private List<ShopItem> shopItems = new ArrayList<>(); // list of available shop items
	
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
		
		// Initialise the shop items
		shopItems.add(new ShopItem("LumberRobot", "lumberRobotFrame1.png", 5, RobotType.LUMBER));

	}
	
	/**
	 * Method interact - This defines the interaction behaviour when the player collied with the NPC
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
	        	checkAndAddNewItems(); // dynamically add new items based on resource count
	        	
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

	            // Add an image for the shopkeeper
	            ImageView shopkeeperImage = new ImageView(new Image("file:src/robotCivSim/Assets/shopkeeperNPC.png"));
	            shopkeeperImage.setFitWidth(100);
	            shopkeeperImage.setFitHeight(100);

	            // Shop grid for items
	            GridPane shopGrid = new GridPane();
	            shopGrid.setHgap(10);
	            shopGrid.setVgap(10);
	            shopGrid.setAlignment(Pos.CENTER); // centre align
	            
	            populateShopGrid(shopGrid, player, canvas);
	            
                // Close button
                Button closeButton = new Button("Close");
                closeButton.setStyle("-fx-font-size: 14px; -fx-padding: 10; -fx-background-color: #dc3545; -fx-text-fill: white;");
                closeButton.setOnAction(e -> {
                    dialog.close();
                    isInteracting = false;
                    SoundManager.getInstance().playSound("interactShop"); // play the interaction soundd
                });

                layout.getChildren().addAll(titleLabel, shopkeeperImage, shopGrid, closeButton);

                Scene scene = new Scene(layout, Math.min(shopItems.size() * 400, 500), 600);
                dialog.setScene(scene);
                dialog.showAndWait();
            } finally {
                isInteracting = false;
            }
	    });
	}
	
	/** 
	 * Method populateShopGrid - Populates the shop grid with available items
	 * 
	 * @param shopGrid - The GridPane to populate
	 * @param player - Reference to the PlayerRobot
	 * @param canvas - Reference to the MyCanvas instance
	 */
	public void populateShopGrid(GridPane shopGrid, PlayerRobot player, MyCanvas canvas) {
		for(int i = 0; i < shopItems.size(); i++) {
			ShopItem item = shopItems.get(i);
			
			VBox itemBox = new VBox(10);
			itemBox.setAlignment(Pos.CENTER); 
			itemBox.setStyle("-fx-border-color: black; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: white;");
			
			// Dynamically load the image
			Image itemImage = loadImage(item.getImagePath());
			ImageView itemImageView = new ImageView(itemImage);
			itemImageView.setFitWidth(50);
			itemImageView.setFitHeight(50);
			
			// Label for item name
			Label itemName = new Label(item.getName());
			itemName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
			
			// Label for item cost
			Label itemCost = new Label(item.getCost() + "x Wood");
			itemCost.setStyle("-fx-font-size: 12px;");
			
			// Create purchase button
			Button purchaseButton = new Button("Buy");
            purchaseButton.setStyle("-fx-font-size: 12px; -fx-background-color: #28a745; -fx-text-fill: white;");
            purchaseButton.setOnAction(e -> handlePurchase(item, player, canvas));
            
            itemBox.getChildren().addAll(itemImageView, itemName, itemCost, purchaseButton);
            
            shopGrid.add(itemBox,  i % 2,  i / 2); // Arrange items in a grid 			
		}
	}
	
	/**
	 * Method handlePurchase - Handles the purchase logic for a shop item
	 * 
	 * @param item - The ShopItem being purchased
	 * @param player - The PlayerRobot making the purchase
	 * @param canvas - The MyCanvas instance
	 */
	private void handlePurchase(ShopItem item, PlayerRobot player, MyCanvas canvas) {
		// Ensure user has enough of the wood resource
		if (arena.getWoodResourceCount() >= item.getCost()) {
			arena.decrementWoodResource(item.getCost());
			double x = player.getXPosition() + 50; // offset the new Robot's position
			double y = player.getYPosition() + 50; 
			arena.addItem(createRobot(item.getType(), x, y, arena));
			canvas.clearCanvas();
			arena.drawArena(canvas); // re-draw the arena
		} else {
			// Send alert to user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Insufficient Resources");
            alert.setHeaderText(null);
            alert.setContentText("You need at least " + item.getCost() + " wood to buy this item.");
            alert.showAndWait();
		}
	}
	
	/**
	 * Method checkAndAddNewItems - Checks that the Player has now got 10 or more wood
	 * If they do the Shopkeeper updates to now allow MinerRobots to be purchased
	 */
	public void checkAndAddNewItems() {
		if(arena.getWoodResourceCount() >= 10 && shopItems.stream().noneMatch(item -> item.getName().equals("MinerRobot"))) {
			shopItems.add(new ShopItem("MinerRobot", "minerRobotFrame1.png", 5, RobotType.MINER)); // add the miner robot to the shop
		}
	}
	
	/** 
	 * Method createRobot - Creates a robot based on the specified type
	 * 
     * @param type - The type of robot to create
     * @param x - x-coordinate of the robot
     * @param y - y-coordinate of the robot
     * @param arena - Reference to the RobotArena
     * @return - The created robot instance
	 */
	private Robot createRobot(RobotType type, double x, double y, RobotArena arena) {
		return switch (type) {
			case LUMBER -> new LumberRobot(x, y, 20, arena); // add LumberRobot to the arena
			case MINER -> new MinerRobot(x, y, 20, arena); // add MinerRobot to the arena
			default -> throw new IllegalArgumentException("Unsupported robot type: " + type); // default message
		};
	}

	/**
	 * Method drawKeyIndicator - Draws the "E" key indicator above the Shopkeeper when the Player is near
	 *
	 * @param canvas - The canvas to draw on.
	 */
	public void drawKeyIndicator(MyCanvas canvas) {
	    canvas.drawKeyIndicator(getXPosition(), getYPosition() - getRadius() * 2);
	}

	 
	/**
	 * Method loadImage - is a helper method that dynamically constructs the path and loads the image
	 * 
	 * @param imagePath - a string representing the image path
	 */
	public Image loadImage(String imagePath) {
		File file = new File("src/robotCivSim/Assets/" + imagePath);
		if (!file.exists()) {
			throw new RuntimeException("Image not found" + file.getAbsolutePath());
		}
		return new Image(file.toURI().toString());
	}
	
	/**
	 * Method addShopItem - Adds a new item to the shop when certain conditions are met
	 * 
	 * @param item - The new ShopItem to add
	 */
	public void addShopItem(ShopItem item) {
		shopItems.add(item);
	}
}
