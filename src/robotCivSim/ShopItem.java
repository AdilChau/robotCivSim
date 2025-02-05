package robotCivSim;

/**
 * Class ShopItem - Represents an item that is available for purchase at the shop
 */
public class ShopItem {
	private final String name;
	private final String imagePath;
	private final int cost;
	private final RobotType type;
	
	/** 
	 * Constructor for ShopItem class
	 * 
	 * @param name - name of the item
	 * @param imagePath - image for the item
	 * @param cost - cost of the item
	 * @param type - type of item
	 */
	public ShopItem(String name, String imagePath, int cost, RobotType type) {
		this.name = name;
		this.imagePath = imagePath;
		this.cost = cost;
		this.type = type;
	}
	
	/**
	 * Getters for Name, ImagePath, Cost, and Type
	 */
    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getCost() {
        return cost;
    }

    public RobotType getType() {
        return type;
    }
}
