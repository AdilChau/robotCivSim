package robotCivSim;

import javafx.scene.image.Image;

/**
 * SwordEnemyRobot - A fast-moving enemy that destroys worker robots on contact.
 */

public class SwordEnemyRobot extends EnemyRobot {
    private static final double DEFAULT_SPEED = 2.5;
	
    
    /**
     * Constructor for SwordEnemyRobot
     * 
     * @param x - x position
     * @param y - y position
     * @param arena - reference to the RobotArena
     */
    public SwordEnemyRobot(double x, double y, RobotArena arena) {
    	super(x, y, 20, DEFAULT_SPEED, arena);
    }
    
    /**
     * Method loadFrames - Loads animation frames for the SwordEnemy
     */
    @Override
    protected void loadFrames() {
    	frames = new Image[] {
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame1.png"),
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame2.png"),
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame3.png"),
				new Image("file:src/robotCivSim/Assets/predatorRobotFrame4.png")
    	};
    }
    
    @Override
    public void destroy() {
        arena.scheduleRemoval(this); // schedule the enemy for removal after iteration
    }

}
