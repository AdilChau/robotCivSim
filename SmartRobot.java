package robotSimGUI;

import java.util.ArrayList; // for boundary points
import java.util.List; // for boundary points

import javafx.scene.paint.Color; // For drawing the light beam
import javafx.scene.shape.ArcType; // Import ArcType for drawing rounded arcs

/** SmartRobot - This class represents a robot with a sensor to detect any nearby object.
 * This can include the boundary of the arena, obstacles, and other robots.
 * Extends the Robot class and includes some additional logic for the detection.
 */
public class SmartRobot extends Robot {
	private double sensorRange; // max distance the sensor can detect
	private double sensorAngle; // angle of the light beam (e.g., 60 degrees)
	private Color lightBeamColor;// current colour of the beam (will be yellow or red)
	
	/** Constructor for SmartRobot
	 * This initialises the robot with position, size, movement speed, and detection capabilities
	 * 
	 * @param xPosition - x-coordinate of the robot
	 * @param yPosition - y-coordinate of the robot
	 * @param radius - radius of the robot
	 * @param arena - reference to the RobotArena instance
	 */
	public SmartRobot(double xPosition, double yPosition, double radius, RobotArena arena) {
		super(xPosition, yPosition, radius, arena); // calls robot parent instructor
		this.sensorRange = 150;// default sensor range set to 150
		this.sensorAngle = 60; // default sensor angle set to 60 degrees
		this.lightBeamColor = Color.YELLOW; // default light beam colour set to yellow
	}
	
	/** Method draw - draws the smart robot on the canvas
	 * Displays the robot and its sensor beam
	 * The beam changes colour based on detection status
	 * 
	 * @param canvas - MyCanvas instance for drawing
	 */
	public void draw(MyCanvas canvas) {
		super.draw(canvas); // draw the robot body
		
		// Save the GraphicsContext state
		canvas.save();
		
		// Translate to the robot's position and rotate to face its direction
		canvas.translate(getXPosition(), getYPosition());
		double angle = Math.toDegrees(Math.atan2(dy, dx)); // calculate rotation angle
		canvas.rotate(angle);
		
		// Draw the light beam as a sector of a circle
		canvas.setFill(lightBeamColor); // Set the colour of the beam
		canvas.fillArc(
				-sensorRange, // x-coordinate of the top-left corner
				-sensorRange, // y-coordinate of the top-left corner
				sensorRange * 2, // width of the arc
				sensorRange * 2, // height of the arc
				-sensorAngle / 2, // starting angle of the beam
				sensorAngle, // sweep angle of the beam
				ArcType.ROUND // shape type (rounded arc)
			); 
	
		// Restore the GraphicsContext state
		canvas.restore();
	}
	
	/** Method update - Updates the smart robot's position and detection status
	 * The robot detects nearby objects and voids them intelligently
	 */
	@Override
	public void update() {
		// Call the parent class update to handle movement
		super.update();
		
		// Reset light beam colour to default (yellow)
		lightBeamColor = Color.YELLOW; 
		
		// Check for nearby objects
		for (ArenaItem item : arena.getItems()) {
			// Skip itself
			if (item == this) continue;
			
			// Calculate distance to the item
			double dx = item.getXPosition() - getXPosition();
			double dy = item.getYPosition() - getYPosition();
			double distance = Math.sqrt(dx * dx + dy * dy);
			
			// Check if the item is within the sensor range and angle
			if (distance <= sensorRange && isWithinSensorAngle(dx, dy)) {
				// Change beam colour to red and avoid the item
				lightBeamColor = Color.RED; // Change beam colour to red
				avoidObject(item);
				break; // Stop checking further after detecting an object
			}
		}
		
		// Check for boundaries with the light beam area
		for (double angle = -sensorAngle / 2; angle <= sensorAngle / 2; angle += 5) { // increment by 5 degrees
			// Calculate the position of the edge point of the beam at this angle
			double beamX = getXPosition() + sensorRange * Math.cos(Math.toRadians(angle + Math.toDegrees(Math.atan2(dy, dx))));
			double beamY = getYPosition() + sensorRange * Math.sin(Math.toRadians(angle + Math.toDegrees(Math.atan2(dy, dx))));
			
			// Check if the beam edge point is outside the arena boundaries
			if (beamX < 0 || beamX > arena.getWidth() || beamY < 0 || beamY > arena.getHeight()) {
				lightBeamColor = Color.RED; // Change beam colour to red
				this.dx = -this.dx; // reverse the x-direction
				this.dy = -this.dy; // reverse the y-direction
				break; // stop further checks
			}
		}
	}
	
	/** Method isWithinSensorAngle - This is a helper method to determine if an object is within the sensor's angle
	 * 
	 * @param dx - Difference in x-coordinates
	 * @param dy - Difference in y-coordinates
	 * @return true - if within angle, otherwise false
	 */
	private boolean isWithinSensorAngle(double dx, double dy) {
		double angleToItem = Math.toDegrees(Math.atan2(dy, dx)); // calculate angle to the item
		double robotAngle = Math.toDegrees(Math.atan2(this.dy, this.dx)); // calculate the robot's current angle
		
		// Normalise angles to the range [0, 360]
		angleToItem = (angleToItem + 360) % 360;
		robotAngle = (robotAngle + 360) % 360;
		
		double relativeAngle = Math.abs(angleToItem - robotAngle); // calculate the relative angle
		if (relativeAngle > 180) {
			relativeAngle = 360 - relativeAngle; // adjust for angles over 180 degrees
		}
		
		return relativeAngle <= sensorAngle / 2; // check if the relative angle is within the sensor's range
	}
	
	/** Method avoidObject - This is another helper method to adjust direction to avoid an object
	 * 
	 * @param item - The ArenaItem to avoid
	 */
	private void avoidObject(ArenaItem item) {
		// Adjust direction
		double dx = getXPosition() - item.getXPosition();
		double dy = getYPosition() - item.getYPosition();
		double angle = Math.atan2(dy, dx);
		this.dx = Math.cos(angle) * speed;
		this.dy = Math.sin(angle) * speed;
	}
}