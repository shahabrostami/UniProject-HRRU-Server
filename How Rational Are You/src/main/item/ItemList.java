package main.item;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class ItemList  {

	private int size;
	private Item item[];
	
	public ItemList() throws SlickException {
		size = 12;
		item = new Item[size];
		
		item[0] = new Item(0, 50, 150, "Armor", "Maximilian Armor.", new Image("simple/items/armorbg.png"));
		item[1] = new Item(1, 100, 200, "Axe", "Dane Axe.", new Image("simple/items/axebg.png"));
		item[2] = new Item(2, 50, 100, "Book", "Siegfried Sassoon's Memoirs of an Infantry Officer.", new Image("simple/items/bookbg.png"));
		item[3] = new Item(3, 100, 150, "Bow", "Pre 1920 South American Amazonian Native Indian Bow.", new Image("simple/items/bowbg.png"));
		item[4] = new Item(4, 50, 100, "Coin", "This U.S. copper half cent was minted in 1796.", new Image("simple/items/coinbg.png"));
		item[5] = new Item(5, 200, 250, "Double Axe", "An extremely rare double axe head.", new Image("simple/items/dblaxebg.png"));
		item[6] = new Item(6, 150, 200, "Document", "Spanish 1806 Sello Tercero Cadiz 136 Maravedis.", new Image("simple/items/documentbg.png"));
		item[7] = new Item(7, 300, 400, "Gem", "Demantoid Gem of Ancient Anglo-Saxons.", new Image("simple/items/gembg.png"));
		item[8] = new Item(8, 200, 300, "Sword", "Turkish Ottoman Sword Kilij.", new Image("simple/items/swordbg.png"));
		item[9] = new Item(9, 250, 300, "Tome", "A rare Tome, just like the one from those movies.", new Image("simple/items/tome.png"));
		item[10] = new Item(10, 50, 100, "Tools", "An ordinary set of tools.", new Image("simple/items/toolsbg.png"));
		item[11] = new Item(11, 20, 80, "Wooden Sword", "A sword, made of wood.", new Image("simple/items/woodenswordbg.png"));
	}
	
	public Item[] getItems(){
		return item;	
	}
	
	public int getSize(){
		return size;
	}
}