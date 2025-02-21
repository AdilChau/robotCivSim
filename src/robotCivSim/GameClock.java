package robotCivSim;

import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 * GameClock - Handles the simulation's day-night cycle
 * Progresses time and notifies the arena of transitions between day and night
 */
public class GameClock {
	private RobotArena arena;
	private int currentHour = 0;
	private boolean isNight = false;
	private static final int TOTAL_SIMULATION_MINUTES = 10; // 10 mins is the full 24hr cycle
	private static final int HOURS_IN_A_DAY = 24;
	private static final long TICK_INTERVAL = (TOTAL_SIMULATION_MINUTES * 60 * 1000) / HOURS_IN_A_DAY; // Milliseconds per in-game hour
	
	/**
	 * Constructor for the GameClock class
	 * 
	 * @param arena - Reference to the RobotArena
	 */
	public GameClock(RobotArena arena) {
		this.arena = arena;
		startClock();
	}
	
	/**
	 * Method startClock - This starts the game clock, and advances the hour every few sconds
	 */
	private void startClock() {
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(TICK_INTERVAL);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 	
				
				Platform.runLater(() -> updateTime());
			}
		}).start();
	}
	
	/** 
	 * Method updateTime - Updates the time of the simulation
	 * It applies changes to the game world
	 */
	private void updateTime() {
		currentHour = (currentHour + 1) % HOURS_IN_A_DAY;
		boolean wasNight = isNight; // update the flag
		isNight = (currentHour >= 18 || currentHour < 6);
		
		arena.setTimeOfDay(currentHour);
		
		if (isNight && !wasNight) {
			arena.spawnEnemyRobots(); // spawn enemies at night
		} else if (!isNight && wasNight) {
			arena.removeEnemyRobots(); // remove enemies at day
		}
	}
	
	 /**
     * Method getCurrentHour - Gets the current hour in the game
     * 
     * @return current hour (0-23)
     */
    public int getCurrentHour() {
        return currentHour;
    }

    /**
     * Method isNight - Checks if it is currently night
     * 
     * @return true if it is night time
     */
    public boolean isNight() {
        return isNight;
    }
	
}
