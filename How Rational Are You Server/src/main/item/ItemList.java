package main.item;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class ItemList  {

	private int size;
	private Item item[];
	
	public ItemList() throws SlickException {
		size = 12;
		item = new Item[size];
		
		item[0] = new Item(0, 50, 150, "Armor", "Armor", null);
		item[1] = new Item(1, 100, 200, "Axe", "Axe", null);
		item[2] = new Item(2, 50, 100, "Book", "Book", null);
		item[3] = new Item(3, 100, 150, "Bow", "Bow", null);
		item[4] = new Item(4, 50, 100, "Coin", "Coin", null);
		item[5] = new Item(5, 200, 250, "Double Axe", "Double Axe", null);
		item[6] = new Item(6, 150, 200, "Document", "Document", null);
		item[7] = new Item(7, 300, 400, "Gem", "Gem", null);
		item[8] = new Item(8, 200, 300, "Sword", "Sword", null);
		item[9] = new Item(9, 250, 300, "Tome", "Tome", null);
		item[10] = new Item(10, 50, 100, "Tools", "Tools", null);
		item[11] = new Item(11, 20, 80, "Wooden Sword", "Wooden Sword", null);
	}
	
	public Item[] getItems(){
		return item;	
	}
	
	public int getSize(){
		return size;
	}
}