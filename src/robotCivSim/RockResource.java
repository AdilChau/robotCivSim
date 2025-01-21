package robotCivSim;

import javafx.scene.image.Image;

/**
 * RockResource - Represents a dropped rock resource item in the arena.
 */
public class RockResource extends ResourceItem {

    /**
     * Constructor for WoodResource
     * 
     * @param x - x-coordinate of the wood resource
     * @param y - y-coordinate of the wood resource
     * @param size - size of the wood resource
     */
    public RockResource(double x, double y, double size) {
        super(x, y, size);
    }

    /**
     * Method getResourceImage - Provides the image for the rock resource
     * 
     * @return Image of the rock resource
     */
    @Override
    protected Image getResourceImage() {
        return new Image(getClass().getResource("/robotCivSim/Assets/rock.png").toExternalForm());
    }

    /**
     * Method destroy - Handles what happens when the rock resource is destroyed
     * In this case, it simply removes itself from the arena.
     */
    @Override
    public void destroy() {
        // Remove the resource from the arena
        if (getArena() != null) { // ensure the arena reference is set
            getArena().removeItem(this);
        }
    }
    
	/**
	 * Method Draw - to draw the resource item
	 * 
	 * @param canvas - MyCanvas object used for rendering
	 */
    @Override
    public void draw(MyCanvas canvas) {
        Image resourceImage = getResourceImage();
        if (resourceImage != null) {
            double scaleFactor = 0.7; // specific scale for RockResource
            double scaledWidth = getRadius() * 2 * scaleFactor;
            double scaledHeight = getRadius() * 2 * scaleFactor;

            canvas.drawImage(resourceImage,
                    getXPosition() - scaledWidth / 2,
                    getYPosition() - scaledHeight / 2,
                    scaledWidth,
                    scaledHeight);
        }
    }

}
