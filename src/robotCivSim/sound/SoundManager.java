package robotCivSim.sound;

import javafx.scene.media.AudioClip; 
import java.net.URL; 
import java.util.HashMap;
import java.util.Map;

/**
 * SoundManage class - Manages all sound effects and music for the game
 * Loads and caches sound effects using JavaFX AudioClip
 * Provides centralised API to play sounds
 * Utilises Singleton design pattern for re-usability and ensuring a modular design
 */

public class SoundManager {
	private static SoundManager instance; // singleton design
	private Map<String, AudioClip> soundEffects;
	
	/**
	 * Private constructor for the Singleton. This loads all sound effects when then instance is created
	 */
	private SoundManager() {
		soundEffects = new HashMap<>();
		loadSounds();
	}
	
	/**
	 * Getter getInstance - returns the singleton instance of SoundManager.
	 * If it does not exist it creates it 
	 * 
	 * @return the singleton instance of SoundManager
	 */
	public static SoundManager getInstance() {
		if (instance == null) {
			instance = new SoundManager(); // create if it doesn't exist
		}
		return instance; // return singleton instance
	}
	
	/** 
	 * Method loadSounds() - This loads sounds effects into the SoundEffect map
	 * The method attempts to load each sound file from the resources folder and associates it with a key
	 */
	private void loadSounds() {
		// Load the "Chop" sound effect
		URL chopURL = getClass().getResource("/robotCivSim/SoundEffects/chop.wav");
		if (chopURL != null) {
			// Create an AudioClip from the URL and store it with the key "chop"
			soundEffects.put("chop", new AudioClip(chopURL.toExternalForm())); 
		} else {
			System.err.println("Could not load 'chop' sound");
		}
		
		// Load the "Mine" sound effect
		URL mineURL = getClass().getResource("/robotCivSim/SoundEffects/mine.wav");
		if (mineURL != null) {
			// Create an AudioClip from the URL and store it with the key "chop"
			soundEffects.put("mine", new AudioClip(mineURL.toExternalForm())); 
		} else {
			System.err.println("Could not load 'mine' sound");
		}
		
		// Load the "Grass" sound effect
		URL grassURL = getClass().getResource("/robotCivSim/SoundEffects/grass.wav");
		if (grassURL != null) {
			soundEffects.put("grass", new AudioClip(grassURL.toExternalForm()));
		} else {
			System.err.println("Could not load 'grass' sound");
		}
		
		// Load the "Item Collect" sound effect
		URL itemCollectURL = getClass().getResource("/robotCivSim/SoundEffects/itemCollect.wav");
		if (itemCollectURL != null) {
			soundEffects.put("itemCollect", new AudioClip(itemCollectURL.toExternalForm()));
		} else {
			System.err.println("Could not load 'itemCollect' sound");
		}
		
		// Load the "Item Collect" sound effect
		URL interactShopURL = getClass().getResource("/robotCivSim/SoundEffects/interactShop.wav");
		if (interactShopURL != null) {
			soundEffects.put("interactShop", new AudioClip(interactShopURL.toExternalForm()));
		} else {
			System.err.println("Could not load 'interactShop' sound");
		}
		
		
 	}
	

    /**
     * Method playSound - Plays the sound effect corresponding to the given key
     * <p>
     * For example, to play the chop sound effect, call:
     * SoundManager.getInstance().playSound("chop");
     *
     * @param soundKey - the key associated with the sound effect to play
     */
    public void playSound(String soundKey) {
        AudioClip clip = soundEffects.get(soundKey);
        if (clip != null) {
            // Play the audio clip
            clip.play();
        } else {
            System.err.println("Sound not found: " + soundKey);
        }
    }
    
    /**
     * Method stopSound - Stops the sound effect corresponding to the given key
     *
     * @param soundKey - the key associated with the sound effect to stop
     */
    public void stopSound(String soundKey) {
        AudioClip clip = soundEffects.get(soundKey);
        if (clip != null) {
            clip.stop();
        }
    }
    
   // TODO: Add more sound effects 
   // TODO: Add more methods to control the sounds (volume slider, stop the music etc..)
}
