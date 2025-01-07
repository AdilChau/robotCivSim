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
import javafx.scene.control.Button; // import button
import javafx.scene.control.ToolBar; // import toolbar
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox; // for VBox layout
import javafx.geometry.Pos; // for alignment options

/** SimulationGUI - Is the Main GUI class for the robot simulation.
 * It extends JavaFX Application to create and manage the GUI.
 * 
 */
public class SimulationGUI extends Application { 
	private RobotArena arena; // RobotArena managing the simulation
	private MyCanvas canvas; // custom canvas for drawing
	
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
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				for (ArenaItem item : arena.getItems()) {
					item.update(); // Update the state of each item
				}
				
				// Draw the arena and items
				arena.drawArena(canvas);
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
	 * 
	 * @return ToolBar - The Created toolbar
	 */
	private ToolBar createToolBar() {
		// Create buttons
		Button addRobotButton = new Button("Add Robot"); // creates button to add robot
		Button addObstacleButton = new Button("Add Obstacle"); // create button to add obstacle
		
		// Set up the button actions
		addRobotButton.setOnAction(e -> addRobot()); // allows addRobot button to have an action
		addObstacleButton.setOnAction(e -> showObstacleMenu()); // opens obstacle selection menu
		
		// Add buttons to the toolbar
		ToolBar toolBar = new ToolBar(addRobotButton, addObstacleButton);
		return toolBar;
	}
	
	/** Method addRobot - adds a robot to the arena without overlapping any existing items
	 * Then redraws the canvas
	 * 
	 */
	private void addRobot() {
		double x, y; // initialise x and y coordinates
		do {
			x = 30 + Math.random() * (800 - 60); // random x-coordinate (ensures it fits within boundaries)
			y = 30 + Math.random() * (600 - 60); // random y-coordiante	 (ensures it fits within boundaries)
		} while (arena.checkOverlap(x, y, 20)); // check for overlap
		
		
		// Add a robot at a determined position
		arena.addItem(new Robot(x, y, 20, arena)); // using validated coordinates
		arena.drawArena(canvas); // redraw he arena
	}
	
	/** Method showObstacleMenu - Displays a popup  menu to choose obstacle type
	 * It Allows for the user to select a specific obstacle to add out of available options (e.g., Tree, Rock)
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
	
	
	/**
	 * Main method to launch the application.
	 * 
	 * @param args - Command-line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
