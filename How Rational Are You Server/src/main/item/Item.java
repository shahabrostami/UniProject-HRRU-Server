package main.item;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Item {
	// initiate item objects for server record
	private int minValue;
	private int maxValue;
	private String name;
	private String description;
	private Image itemImage;
	
	public Item(int id, int minValue, int maxValue, String name, String description, Image itemImage) throws SlickException {
		this.setMinValue(minValue);
		this.setMaxValue(maxValue);
		this.setName(name);
		this.setDescription(description);
		this.setItemImage(itemImage);
	}

	public int getMinValue() {
		return minValue;
	}

	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Image getItemImage() {
		return itemImage;
	}

	public void setItemImage(Image itemImage) {
		this.itemImage = itemImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}