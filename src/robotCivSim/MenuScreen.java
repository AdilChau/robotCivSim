package robotCivSim;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * MenuScreen - Represents the initial start menu screen with a "Start" button.
 * More options to be implemented later
 */
public class MenuScreen {
	private Stage stage;
	private SimulationGUI simulationGUI;
	
	/** 
	 * Constructor for MenuScreen
	 * 
	 * @param stage - This is the primary stage for the application
	 * @param simulationGUI - The main simulation GUI instance
	 */
	public MenuScreen(Stage stage, SimulationGUI simulationGUI) {
		this.stage = stage; // initialise stage
		this.simulationGUI = simulationGUI;
	}
	
	/** 
	 * Method show - Displays he menu screen with a "Start" button
	 */
	public void show() {
		// Create the "Start" button
		Button startButton = new Button("START"); 
        startButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;");
        startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-font-size: 22px; -fx-background-color: #45a049; " +
                "-fx-text-fill: white; -fx-padding: 12px 24px; -fx-border-radius: 5; -fx-background-radius: 5;"));
        startButton.setOnMouseExited(e -> startButton.setStyle("-fx-font-size: 20px; -fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; -fx-padding: 10px 20px; -fx-border-radius: 5; -fx-background-radius: 5;"));
        
        // Define button action to transition into the default simulation screen
        startButton.setOnAction(e -> {
        	simulationGUI.initialiseSimulation(stage);
        });
        
        // Create layout and set the background colour
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: #f4f4f4;");
        root.getChildren().add(startButton);
        
        // Set up the scene
        Scene menuScene = new Scene(root, 800, 600);
        stage.setScene(menuScene);
        stage.setTitle("Robot Simulation Menu");
        stage.show();
	}
}
