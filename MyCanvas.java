package robotSimGUI;

//Import necessary built-in classes for MyCanvas
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import javafx.scene.image.Image; // to add assets

/** MyCanvas - Is a utility class for drawing on a JavaFX canvas.
 * It will provide methods to render shapes, clear the canvas, and display text.
 */
public class MyCanvas {
	private int xCanvasSize; // width of the canvas
	private int yCanvasSize; // height of the canvas
	private GraphicsContext gc; // GraphicsContext for drawing 
	
	/** Constructor for MyCanvas
	 * Initialises the GraphicsConext and canvas dimensions
	 * 
	 *  @param gc - GraphicsContext and canvas dimensions
	 *  @param xCanvasSize - Width of the canvas
	 *  @param yCanvasSize - Height of the canvas
	 */
	public MyCanvas(GraphicsContext gc, int xCanvasSize, int yCanvasSize) {
		this.gc = gc; // initialise graphics context
		this.xCanvasSize = xCanvasSize; // initialise width of canvas
		this.yCanvasSize = yCanvasSize; // initialise height of canvas
	}
	
	/** Method clearCanvas - Clears the entire canvas
	 * By removing all drawings
	 */
	public void clearCanvas() {
		gc.clearRect(0, 0, xCanvasSize, yCanvasSize); // clear the entire canvas
	}
	
	/** Method drawCirle - Draws a filled circle at a specified position with a given radius and colour
	 * 
	 * @param x - x-coordinate of the circle's centre
	 * @param y - y-coordinate of the circle's centre
	 * @param radius - Radius of the circle
	 * @param colour - Fill colour of the circle
	 */
	public void drawCircle(double x, double y, double radius, Color color) {
		gc.setFill(color); // set fill colour
		gc.fillOval(x - radius, y - radius, radius * 2, radius * 2); // draws filled circle
	}
	
	/** Method drawText - Displays text at a specified x,y position
	 * 
	 * @param x - x-coordinate for the text
	 * @param y - y-coordinate for the text
	 * @param text - String of text to display
	 */
	public void drawText(double x, double y, String text) {
		gc.setTextAlign(TextAlignment.CENTER); // align text horizontally
		gc.setTextBaseline(VPos.CENTER); // align text vertically
		gc.setFill(Color.BLACK); // set the text colour
		gc.fillText(text, x, y); // draw the text
	}
	
	/** Method drawRectangle - draws a rectangle with specified position, size, and colour
	 * 
     * @param x - x-coordinate of the rectangle's top-left corner
     * @param y - y-coordinate of the rectangle's top-left corner
     * @param width - width of the rectangle
     * @param height - height of the rectangle
     * @param fillColour - fill colour of the rectangle
     * @param strokeColor - Stroke (border) colour of te rectangle
     * @param strokWidth - Width of the stroke (border)
	 */
	public void drawRectangle(double x, double y, double width, double height, Color fillColor, Color strokeColor, double strokeWidth) {
		if (fillColor != null) {	
			gc.setFill(fillColor); // set fill colour
			gc.fillRect(x, y, width, height); // draw filled rectangle
		}
		
		gc.setStroke(strokeColor); // set the stroke colour
		gc.setLineWidth(strokeWidth); // set the stroke width
		gc.strokeRect(x, y, width, height); // draw the rectangle border
	}
	
	/** Method fillBackground - Fills the entire canvas with a specified colour
	 * 
	 * @param color - The colour to fill the canvas with
	 */
	public void fillBackground(Color color) {
		gc.setFill(color); // set the fill colour
		gc.fillRect(0, 0, xCanvasSize, yCanvasSize); // Fill the entire canvas 
	}
	
	/** Method getCanvasWidth - returns the width of the canvas
	 * 
	 * @return Canvas width
	 */
	public int getCanvasWidth() {
		return xCanvasSize; // returns width of canvas
	}
	
	/** Method getCanvasHeight - returns the height of the canvas
	 * 
	 * @return Canvas height
	 */
	public int getCanvasHeight() {
		return yCanvasSize; // returns height of canvas
	}
	
	/** Method drawIamge - Draws an image on the canvas at a sepcified position
	 * With a specified size
	 * 
	 * @param image - The image object to draw
	 * @param x - x-coordinate for the top-left corner of the image
	 * @param y - y-coordinate for the top-left corner of the image
	 * @param width - The width to draw the image
	 * @param height - The height to draw the image
	 */
	public void drawImage(Image image, double x, double y, double width, double height) {
		gc.drawImage(image, x, y, width, height); // draw the image with specified parameters
	}
	
	/** Method save - Saves the current state of the GraphicsContext
	 * 
	 */
	public void save() {
		gc.save();
	}
	
	/** Method restore - Restores the previous state of the GraphicsContext
	 * 
	 */
	public void restore() {
		gc.restore();
	}
	
	/** Method Translate - Translates the origin of the GraphicsContext to a new position
	 * 
	 * @param x - New x-coordinate for the origin
	 * @param y - New y-coordinate for the origin
	 */
	public void translate(double x, double y) {
		gc.translate(x, y);
	}
	
	/** Method Rotate - Rotates the GraphicsContext
	 * 
	 * @param angle - Angle of rotation in degrees
	 */
	public void rotate(double angle) {
		gc.rotate(angle);
	}
}
