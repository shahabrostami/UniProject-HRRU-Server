package main;

import org.newdawn.slick.Image;

public class ResultPercentage implements Comparable<ResultPercentage>{
	private String name; // name of achievement
	private Image imgRank; // the achievements rank
	private int percentage; // the percentage gained for this achievement
	private String achievementDescription; // the description for this achievement
	// create comparable implementation for the achievements
	public ResultPercentage(String name, Image imgRank, int percentage, String achievementDescription)
	{
		super();
		this.setName(name);
		this.setImgRank(imgRank);
		this.setPercentage(percentage);
		this.setAchievementDescription(achievementDescription);
	}
	@Override
	public int compareTo(ResultPercentage resultPercentage) {
		// TODO Auto-generated method stub
		int percentage = ((ResultPercentage)resultPercentage).getPercentage();
		return percentage - this.percentage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Image getImgRank() {
		return imgRank;
	}
	public void setImgRank(Image imgRank) {
		this.imgRank = imgRank;
	}
	public int getPercentage() {
		return percentage;
	}
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}
	public String getAchievementDescription() {
		return achievementDescription;
	}
	public void setAchievementDescription(String achievementDescription) {
		this.achievementDescription = achievementDescription;
	}

}
