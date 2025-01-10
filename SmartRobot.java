package robotSimGUI;

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
	 * The robot detects nearby objects and avoids them intelligently by steerimg
	 */
	@Override
	public void update() {
		// Call the parent class update to handle movement
		super.update();
		
		// Reset light beam colour to default (yellow)
		lightBeamColor = Color.YELLOW; 
		
		boolean obstacleDetected = false; // tracks if an obstacle/boundary is detected
		double bestSteeringAngle = 0; // best direction to steer the robot
		
		// Check for nearby objects or items
		for (ArenaItem item : arena.getItems()) {
			// Skip itself
			if (item == this) continue;
			
			// Calculate distance to the item
			double dxToItem = item.getXPosition() - getXPosition();
			double dyToItem = item.getYPosition() - getYPosition();
			double distance = Math.sqrt(dxToItem * dxToItem + dyToItem * dyToItem);
			
			// Check if the item is within the sensor range and angle
			if (distance <= sensorRange && isWithinSensorAngle(dxToItem, dyToItem)) {
				// change beam colour to red and avoid the item
				lightBeamColor = Color.RED; // change beam colour to red
				obstacleDetected = true; // change to true
				
				// Calculate the steering angle to avoid the obstacle
				double angleToItem = Math.atan2(dyToItem, dxToItem); // angle to the detected item
				double robotAngle = Math.atan2(this.dy, this.dx); // robot's current angle
				double relativeAngle = angleToItem - robotAngle;
				
				// Normalise relative angle to range [-PI, PI]
				if (relativeAngle > Math.PI) relativeAngle -= 2 * Math.PI;
				if (relativeAngle < -Math.PI) relativeAngle += 2 * Math.PI;
				
				// Steer away from the obstacle (opposite direction of relative angle)
				bestSteeringAngle += (relativeAngle > 0 ? -10 : 10); // steer away
				
			}
		}
		
		// Check for boundaries with the light beam area
		for (double angle = -sensorAngle / 2; angle <= sensorAngle / 2; angle += 5) { // increment by 5 degrees
			// Calculate the position of the edge point of the beam at this angle
			double beamX = getXPosition() + sensorRange * Math.cos(Math.toRadians(angle + Math.toDegrees(Math.atan2(dy, dx))));
			double beamY = getYPosition() + sensorRange * Math.sin(Math.toRadians(angle + Math.toDegrees(Math.atan2(dy, dx))));
			
			// Check if the beam edge point is outside the arena boundaries
			if (beamX < 0 || beamX > arena.getWidth() || beamY < 0 || beamY > arena.getHeight()) {
				lightBeamColor = Color.RED; // change beam colour to red
				obstacleDetected = true; // change to true
				
				// Steer away from the obstacle (opposite direction of relative angle)
				bestSteeringAngle += (angle > 0 ? -10 : 10); // steer away
				
			}
		}
		
		// Adjust the robot's direction if an obstacle or boundary is detected
		if(obstacleDetected) {
			// Gradually rotate the direction of which the robot is heading
			double currentAngle = Math.atan2(dy, dx); // current angle of movement in radians
			double newAngle = currentAngle + Math.toRadians(bestSteeringAngle / 2); // adjust by half of the best angle
			
			dx = Math.cos(newAngle) * speed; // update dx based on the new angle
			dy = Math.sin(newAngle) * speed; // update dy based on the new angle 
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
}