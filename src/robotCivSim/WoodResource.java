package robotCivSim;

import javafx.scene.image.Image;

/**
 * WoodResource - Represents a dropped wood resource item in the arena.
 */
public class WoodResource extends ResourceItem {

    /**
     * Constructor for WoodResource
     * 
     * @param x - x-coordinate of the wood resource
     * @param y - y-coordinate of the wood resource
     * @param size - size of the wood resource
     */
    public WoodResource(double x, double y, double size) {
        super(x, y, size);
    }

    /**
     * Method getResourceImage - Provides the image for the wood resource
     * 
     * @return Image of the wood resource
     */
    @Override
    protected Image getResourceImage() {
        return new Image(getClass().getResource("/robotCivSim/Assets/wood.png").toExternalForm());
    }

    /**
     * Method destroy - Handles what happens when the wood resource is destroyed
     * In this case, it simply removes itself from the arena.
     */
    @Override
    public void destroy() {
        // Remove the resource from the arena
        if (getArena() != null) { // ensure the arena reference is set
            getArena().scheduleRemoval(this);
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
            double scaleFactor = 0.7; // specific scale for WoodResource
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
