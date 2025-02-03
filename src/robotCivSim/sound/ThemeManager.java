package robotCivSim.sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

/**
 * The ThemeManager class manages background themes for the robotCivSim
 * It loads and plays media files using JavaFX's Media and MediaPlayer classes
 * Two themes are supported: MenuTheme and GameTheme
 */
public class ThemeManager {
	private static ThemeManager instance;
	private MediaPlayer menuThemePlayer;
	private MediaPlayer gameThemePlayer;
	
	/**
	 * Constructs a ThemeManager by loading the menu and game theme media files
	 * Ensures that the theme media files are located on the classpath
	 */
	public ThemeManager() {
		// Load the menu theme
		URL menuURL = getClass().getResource("/robotCivSim/SoundEffects/menuTheme.wav");
		if (menuURL != null) {
			Media menuMedia = new Media(menuURL.toExternalForm());
			menuThemePlayer = new MediaPlayer(menuMedia);
			// Loop the menu theme indefinitely
			menuThemePlayer.setCycleCount(MediaPlayer.INDEFINITE);
		} else {
			System.err.println("Could not load menu theme");
		}
		
		// Load the game theme
		URL gameURL = getClass().getResource("/robotCivSim/SoundEffects/gameTheme.wav");
		if (gameURL != null) {
			Media gameMedia = new Media(gameURL.toExternalForm());
			gameThemePlayer = new MediaPlayer(gameMedia);
			// Loop the game theme indefinitely
			gameThemePlayer.setCycleCount(MediaPlayer.INDEFINITE);
		} else {
			System.err.println("Could not load game theme");
		}
	}
	
    /**
     * Getter getInstance - Returns the singleton instance of ThemeManager
     *
     * @return - the single instance of ThemeManager
     */
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
	
    /**
     * Method playMenuTheme - Plays the menu theme
     * If the game theme is currently playing, it stops it before starting the menu theme
     */
    public void playMenuTheme() {
        if (gameThemePlayer != null) {
            gameThemePlayer.stop();
        }
        if (menuThemePlayer != null) {
            menuThemePlayer.play();
        }
    }

    /**
     * Method playGameTheme - Plays the game theme
     * If the menu theme is currently playing, it stops it before starting the game theme
     */
    public void playGameTheme() {
        if (menuThemePlayer != null) {
            menuThemePlayer.stop();
        }
        if (gameThemePlayer != null) {
            gameThemePlayer.play();
        }
    }

    /**
     * Method stopThemes - Stops any currently playing theme
     */
    public void stopThemes() {
        if (menuThemePlayer != null) {
            menuThemePlayer.stop();
        }
        if (gameThemePlayer != null) {
            gameThemePlayer.stop();
        }
    }
}
