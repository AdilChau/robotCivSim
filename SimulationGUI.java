package robotSimGUI;

import javafx.animation.AnimationTimer;
// Import necessary built-in classes for GUI
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.Button; // import button
import javafx.scene.control.ToolBar; // import toolbar
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox; // for VBox layout
import javafx.geometry.Pos; // for alignment options
import java.io.*; // for saving and loading files

/** SimulationGUI - Is the Main GUI class for the robot simulation.
 * It extends JavaFX Application to create and manage the GUI.
 * 
 */
public class SimulationGUI extends Application { 
	private RobotArena arena; // RobotArena managing the simulation
	private MyCanvas canvas; // custom canvas for drawing
	private AnimationTimer timer; // animation timer for controlling simulation 
	private boolean isPaused = false; // tracks if the animation is paused or running
	private double speedMultiplier = 1.0; // multiplier for animation speed
	
	/** Method Start - sets up the JavaFX stage (window) and Scene (content)
	 * 
	 * @param stage - Primary stage for the application
	 */
	@Override
	public void start(Stage stage) {
		// Initialise the RobotArena with default dimensions
		arena = new RobotArena(800, 600);
		
		// Add sample items for testing
		arena.addItem(new Obstacle(200, 200, 30, "tree")); // add an obstacle
		arena.addItem(new Robot(400, 300, 20, arena)); // add a robot
		arena.addItem(new SmartRobot(100, 100, 20, arena)); // add a SmartRobot
		
		// Set up the GUI Layout
		BorderPane root = new BorderPane(); // root layout for the GUI
		
		// Create and set up the canvas
		Canvas drawCanvas = new Canvas(800, 600); // size matches the RobotArena
		GraphicsContext gc = drawCanvas.getGraphicsContext2D();
		canvas = new MyCanvas(gc, 800, 600);
		
		root.setCenter(drawCanvas); // add the canvas to the centre of the layout
		
		// Combine both the menu bar and toolbar into a VBox to avoid clashing
		MenuBar menuBar = createMenuBar(); // create the menu bar
		ToolBar toolBar = createToolBar(); // create the toolbar
		javafx.scene.layout.VBox topContainer = new javafx.scene.layout.VBox(menuBar, toolBar);
		
		root.setTop(topContainer); // add the canvas to the centre of the layout
		
		// Set up the scene and stage
		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.setTitle("Robot Simulation"); // changes title
		stage.show();
		
		// Animation Timer for movement
		timer = new AnimationTimer() {
			private long lastUpdate = 0; // to control speed
			
			@Override
			public void handle(long now) {
				if (lastUpdate == 0 || now - lastUpdate >= (long)(16_666_667 / speedMultiplier)) {
					for (ArenaItem item : arena.getItems()) {
						item.update(); // Update the state of each item
					}
					arena.drawArena(canvas); // draw the arena and items
					lastUpdate = now; // reset the last update
				}
			}
		};
		timer.start();
	}
	
	/** CreateMenuBar method sets up the menu bar with some placeholder options
	 * 
	 * @return MenuBar - The create menu bar.
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
		
		// Configuration menu
		Menu configMenu = new Menu("Configuration"); // initialises configuration menu option
		MenuItem configureArena = new MenuItem("Configure Arena"); // initialises configure arena option
		configMenu.getItems().add(configureArena);
		
		// Help menu
		Menu helpMenu = new Menu ("Help"); // initialises help menu option
		MenuItem aboutItem = new MenuItem("About"); // initialises about menu option
		helpMenu.getItems().add(aboutItem);
		
		// Add menus to the menu bar
		menuBar.getMenus().addAll(fileMenu, configMenu, helpMenu);
		
		return menuBar;
	}
	
	/** Method ToolBar - creates a toolbar with buttons for user interaction
	 * Adds options to add robots, obstacles, and reset the canvas
	 * 
	 * @return ToolBar - The Created toolbar
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
			speedLabel.setText(String.format("Speed: %.1fx", speedMultiplier));; // update the speed label
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
	
	/** Method addRobot - adds a robot of the specified type to the arena without overlapping any existing items
	 * Validates the position to ensure no collisions with existing entities
	 * 
	 * @param type - The type of robot to add (e.g., "Basic", "Smart")
	 */
	private void addRobot(RobotType type) {
		double x, y; // initialise x and y coordinates
		do {
			x = 30 + Math.random() * (800 - 60); // random x-coordinate (ensures it fits within boundaries)
			y = 30 + Math.random() * (600 - 60); // random y-coordiante	 (ensures it fits within boundaries)
		} while (arena.checkOverlap(x, y, 20)); // check for overlap
		
		
		// Add the specified type of robot
		if (type == RobotType.BASIC ) {
			arena.addItem(new Robot(x, y, 20, arena)); // add basic robot using validated coordinates
		} else if (type == RobotType.SMART) {
			arena.addItem(new SmartRobot(x, y, 20, arena)); // add smart robot using validated coordinates
		} else if (type == RobotType.PREDATOR) {
			arena.addItem(new PredatorRobot(x, y, 20, arena)); // add predator robot using validated coordinates
		}
		
		// Redraw the arena
		arena.drawArena(canvas);


	}
	
	/** Method showRobotMeny - Displays a popup menu to choose robot type 
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
		
		// Layout for the buttons
		VBox layout = new VBox(10, basicRobotButton, smartRobotButton, predatorRobotButton); // vertical box layout with spacing
		layout.setAlignment(Pos.CENTER); // center-align the buttons 
		Scene scene = new Scene(layout, 200, 150); // create the scene with specified size
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
		layout.setAlignment(Pos.CENTER); // center align the buttons
		Scene scene = new Scene(layout, 200, 150); // create the scene with specified size
		dialog.setScene(scene);
		dialog.showAndWait(); // show the dialog and wait for the user interaction
	}
	
	/** Method addSpecificObstacle - Adds a specific obstacle to the arena
	 * Based on the type passed as a parameter
	 * Ensures there is no overlap with existing items
	 * 
	 * @param type - The type of obstacle to add (e.g., "tree" or "rock")
	 */
	private void addSpecificObstacle(String type) {
		double x, y; // initialise x and y coordinates
		do {
			x = 30 + Math.random() * (800 - 60); // random x-coordiante (ensures it fits within boundaries)
			y = 30 + Math.random() * (600 - 60); // random y-coordinate (ensures it fits within boundaries)
		} while (arena.checkOverlap(x, y, 30)); // check for overlap with existing items
		
		// Add the appropriate obstacle based on the type
		if (type.equals("tree")) {
			arena.addItem(new Obstacle(x, y, 30, "tree"));
		} else if (type.equals("rock")) {
			arena.addItem(new Obstacle(x, y, 30, "rock"));
		}
		
		arena.drawArena(canvas); // redraw the arena
	}
	
	
	/** Method resetCanvas - This resets the canvas and arena to a blank state
	 * Removes all entities from the arena and clears the canvas
	 */
	public void resetCanvas() {
		arena.clearArena(); // clear all items in the arena
		arena.drawArena(canvas); // redraw the empty arena (with borders only)
	}
	
	
	/** Method saveArenaToFile - Saves the current state of the arena to a file
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
	
	/** Method loadArenaFromFile - Loads the state of the arena from a file selected by the user
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
	
	/** Method togglePause - Toggles the animation between pause and play states
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
	 * Main method to launch the application.
	 * 
	 * @param args - Command-line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
