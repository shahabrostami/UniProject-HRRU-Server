package main;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Character  {
	// set up character variables for player
	private int id, positionx, positiony;
	private String name;
	private Image characterImage;
	private SpriteSheet characterSheet = new SpriteSheet("simple/characters.png", 36,36);
	// initialise characters
	public Character(int id, int positionx, int positiony, String name) throws SlickException {
		this.setId(id);
		this.setCharacterImage(characterSheet.getSprite(positionx, positiony));
		this.positionx = positionx; 
		this.positiony = positiony; 
		this.setName(name);
	}
	// get and set variables for character
	public Image getCharacterImage() {
		return characterImage;
	}

	public void setCharacterImage(Image characterImage) {
		this.characterImage = characterImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public int getPositionx() {
		return positionx;
	}

	public int getPositiony() {
		return positiony;
	}

}