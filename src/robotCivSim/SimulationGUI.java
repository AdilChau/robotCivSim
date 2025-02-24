package robotCivSim;

// Import necessary built-in classes for GUI
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert;
import javafx.scene.control.Button; // import button
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar; // import toolbar
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import robotCivSim.sound.ThemeManager;
import javafx.scene.layout.VBox; // for VBox layout
import javafx.geometry.Insets; // for VBox edges 
import javafx.geometry.Pos; // for alignment options
import java.io.*; // for saving and loading files
import javafx.scene.input.MouseEvent; // for mouse drag option
import javafx.scene.input.MouseButton; // for mouse click detection
import javafx.animation.Animation; // for item popup
import javafx.animation.AnimationTimer; // for item popup
import javafx.animation.KeyFrame; // for item popup
import javafx.animation.Timeline; // for item popup
import javafx.scene.image.Image; // For loading and displaying images


/** SimulationGUI - Is the Main GUI class for the robot simulation.
 * It extends JavaFX Application to create and manage the GUI.
 * Also implements functionality to click and drag items in the arena.
 * 
 */
public class SimulationGUI extends Application { 
	private RobotArena arena; // RobotArena managing the simulation
	private MyCanvas canvas; // custom canvas for drawing
	private AnimationTimer timer; // animation timer for controlling simulation 
	private ArenaItem draggedItem = null; // currently dragged item	private ArenaItem draggedItem = null; // currently dragged item
	
	private double speedMultiplier = 1.0; // multiplier for animation speed
    private int woodResourceCount = 5; // counter for wood resources
    private int rockResourceCount = 0; // counter for rock resources
    
    private Label woodResourceLabel; // label to display wood resource count
    private Label rockResourceLabel; // label to display rock resource count
    
    private boolean woodNotificationShown = false; // flag to track if the notification has been shown
	private boolean isDragging = false; // flag to track if an item is being dragged
	private boolean isPaused = false; // tracks if the animation is paused or running
	
	/** Method Start - This sets up Menu stage and allows the user to proceed to the simulation
	 * 
	 * @param stage - Primary stage for the application
	 */
	@Override
	public void start(Stage stage) {
		// Display show the menu screen
		MenuScreen menuScreen = new MenuScreen(stage, this); // pass the current simulationGUI instance to MenuScreen
		menuScreen.show();
	}
	
	/** Method initialiseSimulation - This sets up the JavaFX stage (the window) and Scene (the content)
	 * 
	 * @param stage - Primary stage for the application
	 */
	public void initialiseSimulation(Stage stage) {
		// Preserve the current window state (min/max screen)
		boolean isMaximized = stage.isMaximized(); // Check if the window has been maximised
	    double stageX = stage.getX();
	    double stageY = stage.getY();
	    double stageWidth = stage.getWidth();
	    double stageHeight = stage.getHeight();
	    
	    // Start the game theme which also stops the menu theme
	    // Create a ThemeManager instance 
	    ThemeManager.getInstance().playGameTheme();

		arena = new RobotArena(800, 600); // Initialise the arena
		arena.setSimulationGUI(this);
		
		// Add PlayerRobot and NPC's with set coordinates
		arena.addItem(new PlayerRobot(400, 300, 20, arena)); // manually add robot to arena
		// Add a ShopkeeperNPC to the arena
		arena.addItem(new ShopkeeperNPC(700, 90, 20, arena));
		
		// Place 5 trees and 5 rocks randomly in the arena using one loop.
		for (int i = 0; i < 5; i++) {
		    double xTree, yTree;
		    // Generate coordinates for a tree until a non-overlapping position is found.
		    do {
		        xTree = 30 + Math.random() * (800 - 60);
		        yTree = 30 + Math.random() * (600 - 60);
		    } while (arena.checkOverlap(xTree, yTree, 30, null));
		    arena.addItem(new Obstacle(xTree, yTree, 30, "tree"));

		    double xRock, yRock;
		    // Generate separate coordinates for a rock until a non-overlapping position is found.
		    do {
		        xRock = 30 + Math.random() * (800 - 60);
		        yRock = 30 + Math.random() * (600 - 60);
		    } while (arena.checkOverlap(xRock, yRock, 30, null));
		    arena.addItem(new Obstacle(xRock, yRock, 30, "rock"));
		}		
		
		// Set up the GUI Layout
		BorderPane root = new BorderPane(); // root layout for the GUI
		
		// Create and set up the canvas
		Canvas drawCanvas = new Canvas(800, 600); // size matches the RobotArena
		GraphicsContext gc = drawCanvas.getGraphicsContext2D();
		canvas = new MyCanvas(gc, 800, 600); // create the canvas
		
		drawCanvas.setFocusTraversable(true); // make canvas focusable
		drawCanvas.requestFocus(); // ensure canvas receives focus initially
		
		root.setCenter(drawCanvas); // add the canvas to the centre of the layout
		
        // Create resource display
        HBox resourceDisplay = createResourceDisplay();

        // Add resource display and canvas to the container
        VBox arenaContainer = new VBox();
        arenaContainer.getChildren().addAll(resourceDisplay, drawCanvas);
        arenaContainer.setStyle("-fx-alignment: center;");
        root.setCenter(arenaContainer);
		
		// Combine both the menu bar and toolbar into a VBox to avoid clashing
		MenuBar menuBar = createMenuBar(); // create the menu bar
		ToolBar toolBar = createToolBar(); // create the toolbar
		javafx.scene.layout.VBox topContainer = new javafx.scene.layout.VBox(menuBar, toolBar);
		
		root.setTop(topContainer); // add the toolbar to top of the canvas
			
		
	    // Dynamically size the scene based on the current stage dimensions
	    Scene scene = new Scene(root, stageWidth, stageHeight);
		
	    //  Add key event handlers for the player robot
	    scene.setOnKeyPressed(event -> {
	    	for (ArenaItem item : arena.getItems()) {
	    		if (item instanceof PlayerRobot) {
	    			((PlayerRobot) item).handleKeyPress(event);
	    		}
	    	}
	    });
	    
	    scene.setOnKeyReleased(event -> {
	    	for (ArenaItem item : arena.getItems()) {
	    		if (item instanceof PlayerRobot) {
	    			((PlayerRobot) item).handleKeyRelease(event);
	    		}
	    	}
	    });
	    
		// Set up the scene and stage
	    Platform.runLater(() -> {
			stage.setScene(scene);
			stage.setTitle("Robot Simulation"); // changes title
			
			// Restore the window position
	        stage.setX(stageX);
	        stage.setY(stageY);
	        
	        // Restore size if not maximised
	        if (!isMaximized) {
	            stage.setWidth(stageWidth);
	            stage.setHeight(stageHeight);
	        }
	        
	        // Restore the maximised state
	        stage.setMaximized(isMaximized);

	    });
	    
		// Animation Timer for movement
		timer = new AnimationTimer() {
			private long lastUpdate = 0; // to control speed
			
			@Override
			public void handle(long now) {
				long adjustedInterval = (long)(16_666_667 / Math.max(speedMultiplier, 0.1)); // avoid division by zero
				if (lastUpdate == 0 || now - lastUpdate >= adjustedInterval) {
					for (ArenaItem item : arena.getItems()) {
						item.update(); // update the state of each item
					}
					arena.processRemovals(); // process any scheduled removals
					arena.drawArena(canvas); // draw the arena and items
					lastUpdate = now; // reset the last update
				}
			}
		};
		
		// Add mouse event listeners to the canvas
		drawCanvas.setOnMousePressed(this::handleMousePressed); // handle clicks
		drawCanvas.setOnMouseDragged(this::handleMouseDragged); // handle dragging
		drawCanvas.setOnMouseReleased(this::handleMouseReleased); // handle release
		
		
		timer.start();
		addCanvasClickHandler(drawCanvas); // allows for items to be selected
	}
	
	/**
	 * Method notifyPlayer - Sends alert to direct the player on what they should be doing
	 * After conditions are met this acts as a guide to show the player what's new
	 */
	public void notifyPlayer() {
		
		// When 10 wood has been collected notify the player
    	if (arena.getWoodResourceCount() == 10) {
    		// Prevent multiple notifications using a flag
    		if (woodNotificationShown) return;
    		woodNotificationShown = true; // set flag to true to stop alert repeating
    		
    		Platform.runLater(() -> { // Ensure the alert is executed in the UI thread
				// Alert the player that new items are available for purchase
				Alert alert = new Alert(Alert.AlertType.INFORMATION); 
				alert.setTitle("New Robot ready for Purchase");
				alert.setHeaderText(null);
				alert.setContentText("Now that you have collected 10 wood, why not visit the shop again?");
				alert.showAndWait();
    		});
    	}
	}
	
	/** Method CreateMenuBar - This sets up the menu bar with some placeholder options
	 * 
	 * @return MenuBar - The created menu bar
	 */
	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar(); // initialises menu bar object
		
		// File menu 
		Menu fileMenu = new Menu("File"); // initialises file option
		MenuItem saveItem = new MenuItem("Save"); // initialises save option
		MenuItem loadItem = new MenuItem("Load"); // initialises load option
		
		// Add actions to the Save and Load options
		saveItem.setOnAction(e -> saveArenaToFile());
		loadItem.setOnAction(e -> loadArenaFromFile());;
		
		fileMenu.getItems().addAll(saveItem, loadItem);
		
		// Help menu
		Menu helpMenu = new Menu ("Help"); // initialises help menu option
		MenuItem aboutItem = new MenuItem("About"); // initialises about menu option
		
		// Add action for the About menu item
		aboutItem.setOnAction(e -> showAboutDialog());
		helpMenu.getItems().add(aboutItem);
		
		// Add menus to the menu bar
		menuBar.getMenus().addAll(fileMenu, helpMenu);
		
		return menuBar;
	}
	
	/** Method ToolBar - This creates a toolbar with buttons for user interaction
	 * Adds options to add robots, obstacles, and reset the canvas
	 * 
	 * @return ToolBar - The created toolbar
	 */
	private ToolBar createToolBar() {
		// Create buttons
		Button addRobotButton = new Button("Add Robot"); // creates button to add robot
		Button addObstacleButton = new Button("Add Obstacle"); // creates button to add obstacle
		Button newCanvasButton = new Button("New Canvas"); // creates button to reset the canvas
		Button pausePlayButton = new Button("Pause"); // creates button to pause/play animation
		
		// Set up the button actions
		addRobotButton.setOnAction(e -> showRobotMenu()); // allows addRobot button to have an action
		addObstacleButton.setOnAction(e -> showObstacleMenu()); // opens obstacle selection menu
		newCanvasButton.setOnAction(e -> resetCanvas()); // reset the canvas to a blank state
		pausePlayButton.setOnAction(e -> togglePause(pausePlayButton)); // toggles pause/play
		
		// Label for the speed slider
		javafx.scene.control.Label speedLabel = new javafx.scene.control.Label("Speed: 1.0x"); // default speed
		
		// Slider for speed control
		Slider speedSlider = new Slider(0.5, 2.0, 1.0); // min=0.5x, max=2x, default=1x
		speedSlider.setShowTickMarks(true); // show tick marks 
		speedSlider.setShowTickLabels(true); // show tick labels
		speedSlider.setMajorTickUnit(0.5);; // tick every 0.x
		
		// Update speed multiplier and label dynamically
		speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
			speedMultiplier = newVal.doubleValue(); // update speed multiplier
			speedLabel.setText(String.format("Speed: %.1fx", speedMultiplier));; // update the speed label dynamically
		});
		
		// Add buttons to the toolbar
		ToolBar toolBar = new ToolBar(
				addRobotButton, // add robot button
				addObstacleButton, // add obstacle button
				newCanvasButton, // new canvas button
				pausePlayButton, // play/pause button
				speedLabel, // speed label
				speedSlider // speed slider
		);
		return toolBar;
	}
	
	/** Method addRobot - This adds a robot of the specified type to the arena without overlapping any existing items
	 * Validates the position to ensure no collisions with existing entities
	 * 
	 * @param type - The type of robot to add (e.g., "Basic", "Smart")
	 */
	private void addRobot(RobotType type) {
		double x, y; // initialise x and y coordinates
		Robot robot = null;
		do {
			x = 30 + Math.random() * (800 - 60); // random x-coordinate (ensures it fits within boundaries)
			y = 30 + Math.random() * (600 - 60); // random y-coordiante	 (ensures it fits within boundaries)
		} while (arena.checkOverlap(x, y, 20, robot)); // check for overlap
		
		
		// Add the specified type of robot
		if (type == RobotType.BASIC ) {
			arena.addItem(new Robot(x, y, 20, arena)); // add basic robot using validated coordinates
		} else if (type == RobotType.SMART) {
			arena.addItem(new SmartRobot(x, y, 20, arena)); // add smart robot using validated coordinates
		} else if (type == RobotType.PREDATOR) {
			arena.addItem(new PredatorRobot(x, y, 20, arena)); // add predator robot using validated coordinates
		} else if (type == RobotType.LUMBER) {
			arena.addItem(new LumberRobot(x, y, 20, arena)); // add lumber robot using validated coordinates
		} else if (type == RobotType.MINER) {
			arena.addItem(new MinerRobot(x, y, 20, arena)); // add miner robot using valdiated coordinates
		}
		
		// Redraw the arena
		arena.drawArena(canvas);


	}
	
	/** Method addPlayerRobot - This adds the Player's robot to the arena which it can control 
	 * Has unique collision logic and functionality compared to all other robot types
	 */
	public void addPlayerRobot() {
		double x = arena.getWidth() / 2; // x-coordinate
		double y = arena.getHeight() / 2; // y-coordinate
		arena.addItem(new PlayerRobot(x, y, 20, arena)); // manually add robot to arena
		arena.drawArena(canvas); // draw on canvas
	}
	
	/** Method showRobotMenu - This displays a popup menu to choose robot type 
	 * It allows for the user to select a specific robot to add out of available options(e.g., Basic, Smart)
	 */
	public void showRobotMenu() {
		// Create a new stage for the popup
		Stage dialog = new Stage();
		dialog.setTitle("Select Robot Type"); // set title
		
		// Create buttons for each of the robot types
		Button basicRobotButton = new Button("Basic Robot");
		Button smartRobotButton = new Button("Smart Robot");
		Button predatorRobotButton = new Button("Predator Robot");
		Button lumberRobotButton = new Button("Lumber Robot");
		Button minerRobotButton = new Button("Miner Robot");
		Button playerRobotButton = new Button("Player Robot");
		
		// Set up the actions of each button
		basicRobotButton.setOnAction(e -> {
			addRobot(RobotType.BASIC); // adds a basic robot
			dialog.close();
		});
		
		smartRobotButton.setOnAction(e -> {
			addRobot(RobotType.SMART); // adds a smart robot
			dialog.close();
		});
		
		predatorRobotButton.setOnAction(e -> {
			addRobot(RobotType.PREDATOR); // adds a predator robot 
			dialog.close();
		});
		
		lumberRobotButton.setOnAction(e -> {
			addRobot(RobotType.LUMBER); // adds a lumber robot
			dialog.close();
		});
		
		minerRobotButton.setOnAction(e -> {
			addRobot(RobotType.MINER); // adds a miner robot
			dialog.close();
		});
		
		playerRobotButton.setOnAction(e -> {
			addPlayerRobot();
			dialog.close();
		});
		
		// Layout for the buttons
		VBox layout = new VBox(10, basicRobotButton, smartRobotButton, predatorRobotButton, lumberRobotButton, minerRobotButton, playerRobotButton); // vertical box layout with spacing
		layout.setAlignment(Pos.CENTER); // center-align the buttons 
		Scene scene = new Scene(layout, 300, 300); // create the scene with specified size
		dialog.setScene(scene);
		dialog.showAndWait(); // show the dialog box and wait for user interaction
	}
	
	/** Method showObstacleMenu - Displays a popup  menu to choose obstacle type
	 * It allows for the user to select a specific obstacle to add out of available options (e.g., Tree, Rock)
	 */
	private void showObstacleMenu() {
		// Create a new stage for the popup
		Stage dialog = new Stage(); 
		dialog.setTitle("Select Obstacle"); // add title
		
		// Create the buttons for each type
		Button treeButton = new Button("Tree"); // tree button
		Button rockButton = new Button("Rock"); // rock button
		
		// Set up actions for each button
		treeButton.setOnAction(e -> {
			addSpecificObstacle("tree"); // add tree using addSpecificObstacle method
			dialog.close(); // close the dialog after selection
		});
		
		rockButton.setOnAction(e -> {
			addSpecificObstacle("rock"); //add rock using addSpecificObstacle method
			dialog.close(); // close the dialog after selection
		});
		
		// Layout for the buttons
		VBox layout = new VBox(10, treeButton, rockButton); // vertical box layout with spacing 
		layout.setAlignment(Pos.CENTER); // centre align the buttons
		Scene scene = new Scene(layout, 200, 150); // create the scene with specified size
		dialog.setScene(scene);
		dialog.showAndWait(); // show the dialog and wait for the user interaction
	}
	
	/** Method addSpecificObstacle - This adds a specific obstacle to the arena
	 * Based on the type passed as a parameter
	 * Ensures there is no overlap with existing items
	 * 
	 * @param type - The type of obstacle to add (e.g., "tree" or "rock")
	 */
	private void addSpecificObstacle(String type) {
		double x, y; // initialise x and y coordinates
		ArenaItem obstacle = null;
		do {
			x = 30 + Math.random() * (800 - 60); // random x-coordiante (ensures it fits within boundaries)
			y = 30 + Math.random() * (600 - 60); // random y-coordinate (ensures it fits within boundaries)
		} while (arena.checkOverlap(x, y, 30, obstacle)); // check for overlap with existing items
		
		// Add the appropriate obstacle based on the type
		if (type.equals("tree")) {
			arena.addItem(new Obstacle(x, y, 30, "tree"));
		} else if (type.equals("rock")) {
			arena.addItem(new Obstacle(x, y, 30, "rock"));
		}
		
		arena.drawArena(canvas); // redraw the arena
	}
	
	/** Method showAboutDialog - This displays an "About" dialog with information on the project and myself
	 * 
	 */
	private void showAboutDialog() {
		// Create a new stage for the dialog
		Stage aboutStage = new Stage();
		aboutStage.setTitle("About"); // sets title of the dialog
		
		// Content for the about dialog
		javafx.scene.control.Label aboutLabel = new javafx.scene.control.Label(
				"CS2OP Robot Simulation Coursework\n\n" +
				"Developed by: Adil Chaudhry\n" +
				"Student Number: 32023993\n\n" +
				"This project simulates an arena where many different robots can interact with each other and various obstacles.\n" +
				"They will perform tasks such as chasing, cutting wood, or mining.\n\n" +
				"Use the menu to add robots/obstacles and control the animation.\n\n" +
				"You can click on any item for more information.\n" +
				"If any robot gets stuck you can drag them to a new position."
		);
		aboutLabel.setWrapText(true); // allow text wrapping for better readability
		
		// Layout for the dialog 
		VBox layout = new VBox(10, aboutLabel); // add spacing between elements
		layout.setAlignment(Pos.CENTER); // center-align the content
		layout.setPadding(new javafx.geometry.Insets(10)); // add padding around the content
		
		// Set up the scene and stage
		Scene scene = new Scene(layout, 650, 300); // set dimensions for dialog box
		aboutStage.setScene(scene);
		aboutStage.showAndWait(); // show the dialog and wait for user interaction 
	}
	
	/** Method createResourceDisplay - Creates a display for resource counters to show wood and rock resources
	 * Positioned above the arena  
	 * 
	 * @return HBox - the container for the resource counters
	 */
	private HBox createResourceDisplay() {
		HBox resourceDisplay = new HBox(30); // spacing of 30 px
		resourceDisplay.setStyle("-fx-padding: 10px; -fx-alignment: center;"); // set style 
		
		// Wood resource display
		ImageView woodIcon = new ImageView(new Image(getClass().getResource("/robotCivSim/Assets/wood.png").toExternalForm()));
		woodIcon.setFitWidth(30); // adjust width of icon
		woodIcon.setFitHeight(30); // adjust height of the icon
		woodResourceLabel = new Label("5"); //default to 5 wood
		woodResourceLabel.setStyle("-fx-font-size: 18px;"); // style the label
		
		HBox woodContainer = new HBox(5, woodIcon, woodResourceLabel); // icon and count
		woodContainer.setStyle("-fx-alignment: center;"); // centre the wood container

        // Rock resource display
        ImageView rockIcon = new ImageView(new Image(getClass().getResource("/robotCivSim/Assets/rock.png").toExternalForm()));
        rockIcon.setFitWidth(30);
        rockIcon.setFitHeight(30);
        rockResourceLabel = new Label("0");
        rockResourceLabel.setStyle("-fx-font-size: 18px;");

        HBox rockContainer = new HBox(5, rockIcon, rockResourceLabel);
        rockContainer.setStyle("-fx-alignment: center;");

        resourceDisplay.getChildren().addAll(woodContainer, rockContainer);

        return resourceDisplay;
    }
	
    /**
     * Increments the wood resource counter and updates the GUI.
     */
    public void incrementWoodResource() {
        arena.incrementWoodResource(); // increment the count in RobotArena
        updateResourceDisplay(); // update the GUI labels
    }

    /**
     * Increments the rock resource counter and updates the GUI.
     */
    public void incrementRockResource() {
        rockResourceCount++;
        rockResourceLabel.setText(String.valueOf(rockResourceCount));
    }

    /**
     * Updates the UI to reflect the updated count
     */
    public void updateResourceDisplay() {
    	woodResourceLabel.setText(String.valueOf(arena.getWoodResourceCount()));
    }
	
	/** Method resetCanvas - This resets the canvas and arena to a blank state
	 * Removes all entities from the arena and clears the canvas
	 */
	public void resetCanvas() {
		arena.clearArena(); // clear all items in the arena
		arena.drawArena(canvas); // redraw the empty arena (with borders only)
	}
	
	
	/** Method handleMousePressed - This detects when the mouse is pressed
	 * It identifies the item under the mouse and marks it for dragging
	 * 
	 * @param event - MouseEvent containing details of the click
	 */
	private void handleMousePressed(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) { // only handle left-clicks
			double x = event.getX();
			double y = event.getY();
			
			// Find the item at the clicked position
			draggedItem = arena.findItemAt(x, y);
			isDragging = false; // reset dragging flag
		}
	}
	
	/** Method handleMouseDragged - This tracks mouse movement while dragging an item
	 * It updates the item's position to follow the mouse
	 * 
	 * @param event - MouseEvent containing details of the drag
	 */
	private void handleMouseDragged(MouseEvent event) {
		if (draggedItem != null) {
			double x = event.getX();
			double y = event.getY();
			
			// Update the item's position
			if (!arena.checkOverlap(x, y, ((ArenaItem) draggedItem).getRadius(), (ArenaItem) draggedItem)) { // ensure no overlap
				draggedItem.setPosition(x, y, arena.getWidth(), arena.getHeight());
				arena.drawArena(canvas); // redraw arena to reflect changes
				isDragging = true; // set flag as dragging
			}
		}
	}
	
	/** Method handleMouseReleased - This handles when the mouse is released
	 * It stops the item being dragged further
	 * 
	 * @param event - MouseEvent containing details of the release
	 */
	private void handleMouseReleased(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) { // only handle left-clicks
			if(!isDragging && draggedItem != null) {
				// If not dragging, handle it as a click for popup
				handleCanvasClick(event.getX(), event.getY());
			}
			draggedItem = null; // stop dragging the item
		}
	}
	
	/** Method handleCanvasClick - This handles mouse clicks on the canvas 
	 * It will display information about the item that has been click in a popup dialog
	 * 
	 * @param x - The x-coordinate of the click
	 * @param y - The y-coordinate of the click
	 */
	private void handleCanvasClick(double x, double y) {
		ArenaItem clickedItem = arena.findItemAt(x,  y);
		if (clickedItem != null) {
			// Create and display the dialog
			showItemDialog(clickedItem);
		}
	}
	
	/** Method showItemDialog - This displays a dialog with information about the clicked item
	 * 
	 * @param item - The ArenaItem to display information about
	 */
	public void showItemDialog(ArenaItem item) {
		// Create a new stage for the dialog
		Stage dialog = new Stage();
		dialog.setTitle("Item Information"); // sets title
		
		if (item instanceof NPC_Robot) { // if the item is an NPC
			return; // display nothing
		}
		
	    // Create labels for item information.
	    Label nameLabel = new Label("Name: " + item.getName()); // displays item name
	    Label descriptionLabel = new Label("Description: " + item.getDescription()); // displays item description
	    Label positionLabel = new Label("Position: (" + String.format("%.2f", item.getXPosition()) + ", " + String.format("%.2f", item.getYPosition()) + ")"); // displays item coordinates to 2d.p
	    
	    // Create a button to remove the item from the arena
	    Button removeButton = new Button("Remove");
	    removeButton.setOnAction(e -> {
	    	arena.scheduleRemoval(item); // place item in hashset for removal
	    	dialog.close(); // close the dialog
	    });
	    
	    // Create an image/animation viewer
	    ImageView imageView = new ImageView(); // image for static items.
	    if (item instanceof Robot) { // if the item is a type of robot
	    	Robot robot = (Robot) item; // cast item to robot
	    	Timeline animation = new Timeline( // create a new timeline
	    		new KeyFrame(Duration.millis(200), e-> { // sets time for 200ms for each key frame
	    			imageView.setImage(robot.getCurrentFrame()); // gets frame for robot
	    		})
	    	);
	    	animation.setCycleCount(Animation.INDEFINITE); // animation will repeat itself until dialog box closed
	    	animation.play(); // start the animation	    			
	    } else if (item instanceof Obstacle) { // if the item is an obstacle
	    	Obstacle obstacle = (Obstacle) item;
	    	imageView.setImage(obstacle.getImage()); // gets image for obstacle 
	    } else if (item instanceof PlayerRobot) { // if item is the Player's Robot
	    	PlayerRobot playerrobot = (PlayerRobot) item;
	    	imageView.setImage(playerrobot.getCurrentFrame());
	    }
	    imageView.setFitWidth(150); // set width
	    imageView.setFitHeight(150); // set height
	    
	    // Layout for the dialog
	    VBox layout = new VBox(10, imageView, nameLabel, descriptionLabel, positionLabel, removeButton);
	    layout.setAlignment(Pos.CENTER); // centre align
	    layout.setPadding(new Insets(10)); // leave space at the edges
	    
	    // Set up scene and stage
	    Scene scene = new Scene(layout, 600, 400); // set dimensions for dialog box
	    dialog.setScene(scene); 
	    dialog.showAndWait(); // display the dialog and wait for user interaction

	}
	
	/** Method addCanvasClickHandler - This adds mouse click handling to the canvas
	 * It allows items to be selected for further information/removal
	 */
	private void addCanvasClickHandler(Canvas canvas) {
		canvas.setOnMouseClicked(e -> {
			if (!isDragging) { // only handle clicks if dragging is false
				handleCanvasClick(e.getX(), e.getY()); // pass the clicked coordinates
			}
		});
	}	
	
	/** Method saveArenaToFile - This saves the current state of the arena to a file
	 * Opens a file chooser dialog for the user to specify the save location
	 */
	private void saveArenaToFile() {
		FileChooser fileChooser = new FileChooser();  // initialises new file chooser
		fileChooser.setTitle("Save Arena"); // sets title
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arena Files", "*.arena")); // restricts file types to only ones with extension ".arena"
		File file = fileChooser.showSaveDialog(null); // show the save dialog and store the selected file 
		
		if (file != null) { // check if user selected a file
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) { 
				oos.writeObject(arena); // serialise the robot arena
			} catch (IOException ex) {
				ex.printStackTrace(); // print stack trace if an IOException
			}
		}
	}
	
	/** Method loadArenaFromFile - This loads the state of the arena from a file selected by the user
	 * Opens a file chooser dialog for the user to select a saved arena file
	 */
	private void loadArenaFromFile() {
		FileChooser fileChooser = new FileChooser(); // initialises new file chooser
		fileChooser.setTitle("Load Arena"); // sets title
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arena File", "*.arena")); // restricts file types to only ones with extension ".arena"
		File file = fileChooser.showOpenDialog(null); 
		
		if (file != null) { // check if user selected a file 
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
				arena = (RobotArena) ois.readObject(); // deserialize the RobotArena
				arena.drawArena(canvas); // redraw the loaded arena on the canvas
			} catch (IOException | ClassNotFoundException ex) {
				ex.printStackTrace(); // print stack trace if an IOException 
			}
		}
	}
	
	/** Method togglePause - This toggles the animation between pause and play states
	 * Updates the button text and stops/starts the animation timer
	 * 
	 * @param button - The button to update its text based on the current state
	 */
	private void togglePause(Button button) {
		isPaused = !isPaused; // toggle the pause state
		
		if (isPaused) {
			timer.stop(); // toggle the pause state
			button.setText("Play"); // update button text
		} else {
			timer.start(); // toggle the play state
			button.setText("Pause"); // update button text
		}
	}
	
	/**
	 * Method getCanvas - Returns the instance of MyCanvas used in the SimulationGUI.
	 *
	 * @return The MyCanvas instance.
	 */
	public MyCanvas getCanvas() {
	    return this.canvas; // Return the canvas instance
	}
	
	/**
	 * Main method to launch the application.
	 * 
	 * @param args - Command-line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
