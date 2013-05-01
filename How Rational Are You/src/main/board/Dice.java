package main.board;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Dice {
	// images from http://opengameart.org/content/dice
	Image[] one = {new Image("img/dice/front&side-1.png"), new Image("img/dice/front&side-1.png"), new Image("img/dice/angled-left&right-1.png"), new Image("img/dice/angled-left&right-1.png")};
	Image[] two = {new Image("img/dice/front-2.png"), new Image("img/dice/side-2.png"), new Image("img/dice/angled-right-2.png"), new Image("img/dice/angled-left-2.png")};
	Image[] three = {new Image("img/dice/front-3.png"), new Image("img/dice/side-3.png"), new Image("img/dice/angled-right-3.png"), new Image("img/dice/angled-left-3.png")};
	Image[] four = {new Image("img/dice/front&side-4.png"), new Image("img/dice/front&side-4.png"), new Image("img/dice/angled-left&right-4.png"), new Image("img/dice/angled-left&right-4.png")};
	Image[] five = {new Image("img/dice/front-5.png"), new Image("img/dice/side-5.png"), new Image("img/dice/angled-left&right-5.png"), new Image("img/dice/angled-left&right-5.png")};
	Image[] six = {new Image("img/dice/front-6.png"), new Image("img/dice/side-6.png"), new Image("img/dice/angled-right-6.png"), new Image("img/dice/angled-left-6.png")};
	
	int[] duration = {800,800,800,800};
	Animation oneRoll = new Animation(one,duration,true);
	Animation twoRoll = new Animation(two,duration,true);
	Animation threeRoll = new Animation(three,duration,true);
	Animation fourRoll = new Animation(four,duration,true);
	Animation fiveRoll = new Animation(five,duration,true);
	Animation sixRoll = new Animation(six,duration,true);
	
	// using rate of falling equation http://en.wikipedia.org/wiki/Equations_for_a_falling_body
	int[] fallingY = {0,1,4,9,16,25,36,49,64,81,100,81,64,49,36,49,64,81,100,81,64,49,64,81,100,81,64,64,81,100,81,81,100};
	
	public Animation dice;
	private int id;
	private int y = 0;
	private int currentNumber = 0;
	private int position = 0;
	
	public Dice(int id) throws SlickException
	{
		this.setId(id);
		this.dice = oneRoll;
		this.dice.setCurrentFrame(2);
	}
	
	public int getY()
	{
		return this.y;
	}
	
	public void rollDice()
	{
		if(getPosition() < 11)
			this.dice.setCurrentFrame(2);
		else if((getPosition() >= 11) &&(getPosition()%4==0))
		{
			this.dice.start();
			this.dice.setCurrentFrame(1);
			currentNumber = (int)(Math.random() * 5) + 1;
		}
				
		switch(currentNumber) 
		{
			case 1: this.dice = oneRoll; break;
			case 2: this.dice = twoRoll; break;
			case 3: this.dice = threeRoll; break;
			case 4: this.dice = fourRoll; break;
			case 5: this.dice = fiveRoll; break;
			case 6: this.dice = sixRoll; break;
		default: break;
		}
		this.y = fallingY[getPosition()];
		setPosition(getPosition() + 1);
		if(getPosition() == fallingY.length)
		{
			setPosition(0);
			this.dice.setCurrentFrame(0);
			this.dice.stop();
		}
	}
	
	public void reset()
	{
		this.y = 0;
	}
	
	public int getCurrentNumber(){
		return currentNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}