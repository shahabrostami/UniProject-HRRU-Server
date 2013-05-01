package main;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;
import java.awt.Color;
import java.awt.Font;

//Provided by http://slickrpg.blogspot.co.uk/2011/07/unicode-font.html //

public class BasicFont {
    private UnicodeFont font;
    
    public BasicFont(String fontName, int style, int size, Color color) throws SlickException {
        this(new Font(fontName, style, size), color);
    }
    public BasicFont(String fontName, int style, int size) throws SlickException {
        this(new Font(fontName, style, size));
    }
    public BasicFont(Font font) throws SlickException {
        this(font, Color.white);
    }
    
	@SuppressWarnings("unchecked")
	public BasicFont(Font font, Color color) throws SlickException {
        this.font = new UnicodeFont(font);
        ColorEffect colorEffect = new ColorEffect(color);
        this.font.getEffects().add(colorEffect);
        this.font.addNeheGlyphs();
        this.font.loadGlyphs();
    }
	
	@SuppressWarnings("unchecked")
	public void setColor(Color color) throws SlickException {
        font.getEffects().clear();
        font.getEffects().add(new ColorEffect(color));
        font.clearGlyphs();
        font.addNeheGlyphs();
        font.loadGlyphs();
    }
	
    public UnicodeFont get() {
        return font;
    }
}