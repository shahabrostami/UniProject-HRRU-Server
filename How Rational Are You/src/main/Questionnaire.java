package main;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.kryonet.Client;

import conn.Packet.Packet26Feedback;

import TWLSlick.BasicTWLGameState;
import TWLSlick.RootPane;
import de.matthiasmann.twl.Button;
import de.matthiasmann.twl.DialogLayout;
import de.matthiasmann.twl.EditField;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.ToggleButton;

public class Questionnaire extends BasicTWLGameState {

	public Client client;
	private int enterState;
	Button btnBack, btnSubmit, btnPrevious, btnNext, btnExit;
	boolean next;
	
	Label lblComments, lblFinish;
	EditField taComments;
	
	// Question GUI
	DialogLayout surveyPanel;
	Image scorebg, questionbg;
	Label lblsa1, lbla1, lbln1, lbld1, lblsd1;
	Label lblsa2, lbla2, lbln2, lbld2, lblsd2;
	Label lblsa3, lbla3, lbln3, lbld3, lblsd3;
	Label lblsa4, lbla4, lbln4, lbld4, lblsd4;
	Label lblsa5, lbla5, lbln5, lbld5, lblsd5;
	Label lblsa6, lbla6, lbln6, lbld6, lblsd6;
	Label lblQuestion1, lblQuestion2, lblQuestion3, lblQuestion4, lblQuestion5, lblQuestion6;
	ToggleButton question1a, question1b, question1c, question1d, question1e;
	ToggleButton question2a, question2b, question2c, question2d, question2e;
	ToggleButton question3a, question3b, question3c, question3d, question3e;
	ToggleButton question4a, question4b, question4c, question4d, question4e;
	ToggleButton question5a, question5b, question5c, question5d, question5e;
	ToggleButton question6a, question6b, question6c, question6d, question6e;
	
	// Question variables
	int question1Answer, question2Answer, question3Answer, question4Answer, question5Answer, question6Answer;
	
	int gcw;
	int gch;
	
	// Ticker variables and font variables
	private int titleFontSize = 20;
	
	private Font loadFont, loadTitleFont;
	private BasicFont titleFont;
	
	private String start_message = "";
	private String full_start_message = "WHAT DID YOU THINK...?";
	private int full_start_counter = 0;
	private String ticker = "";
	private boolean tickerBoolean = true;
	private int clock3, clock2 = 0;
	
	public Questionnaire(int main) {
		client = HRRUClient.conn.getClient();
	}

	@Override
	public void enter(GameContainer gc, StateBasedGame sbg) throws SlickException {
		super.enter(gc, sbg);
		// RESET VARIABLES
		start_message = "";
		full_start_message =  "WHAT DID YOU THINK...?";
		full_start_counter = 0;
		ticker = "";
		tickerBoolean = true;
		clock2 = 0;
		clock3 = 0;
		enterState = 0;
		next = false;
		surveyPanel.setVisible(true);
		btnNext.setVisible(true);
		btnSubmit.setVisible(false);
		btnPrevious.setVisible(false);
		lblComments.setVisible(false);
		lblFinish.setVisible(false);
		taComments.setVisible(false);
		btnExit.setVisible(false);
		
		// SETUP GUI
		rootPane.removeAllChildren();
		rootPane.add(lblComments);
		rootPane.add(taComments);
		rootPane.add(btnBack);
		rootPane.add(btnSubmit);
		rootPane.add(btnNext);
		rootPane.add(btnPrevious);
		rootPane.add(surveyPanel);
		rootPane.add(lblFinish);
		rootPane.add(btnExit);
		rootPane.setTheme("");
	}

	@Override
	protected RootPane createRootPane() {
		assert rootPane == null : "RootPane already created";

		RootPane rp = new RootPane(this);
		rp.setTheme("");
		rp.getOrCreateActionMap().addMapping(this);
		return rp;
	}
	
	void submitAnswers() {
		int answers[] = new int[6];
		answers[0] = question1Answer;
		answers[1] = question2Answer;
		answers[2] = question3Answer;
		answers[3] = question4Answer;
		answers[4] = question5Answer;
		answers[5] = question5Answer;
		String otherComments = taComments.getText();
		
		Packet26Feedback sendFeedback = new Packet26Feedback();
		sendFeedback.feedback = answers;
		sendFeedback.otherComments = otherComments;
		client.sendTCP(sendFeedback);
	}
	
	void question1Disable() {
		question1a.setEnabled(true);
		question1b.setEnabled(true);
		question1c.setEnabled(true);
		question1d.setEnabled(true);
		question1e.setEnabled(true);
		question1a.setActive(false);
		question1b.setActive(false);
		question1c.setActive(false);
		question1d.setActive(false);
		question1e.setActive(false);
	}
	
	void question2Disable() {
		question2a.setEnabled(true);
		question2b.setEnabled(true);
		question2c.setEnabled(true);
		question2d.setEnabled(true);
		question2e.setEnabled(true);
		question2a.setActive(false);
		question2b.setActive(false);
		question2c.setActive(false);
		question2d.setActive(false);
		question2e.setActive(false);
	}
	
	void question3Disable() {
		question3a.setEnabled(true);
		question3b.setEnabled(true);
		question3c.setEnabled(true);
		question3d.setEnabled(true);
		question3e.setEnabled(true);
		question3a.setActive(false);
		question3b.setActive(false);
		question3c.setActive(false);
		question3d.setActive(false);
		question3e.setActive(false);
	}
	
	void question4Disable() {
		question4a.setEnabled(true);
		question4b.setEnabled(true);
		question4c.setEnabled(true);
		question4d.setEnabled(true);
		question4e.setEnabled(true);
		question4a.setActive(false);
		question4b.setActive(false);
		question4c.setActive(false);
		question4d.setActive(false);
		question4e.setActive(false);
	}
	
	void question5Disable() {
		question5a.setEnabled(true);
		question5b.setEnabled(true);
		question5c.setEnabled(true);
		question5d.setEnabled(true);
		question5e.setEnabled(true);
		question5a.setActive(false);
		question5b.setActive(false);
		question5c.setActive(false);
		question5d.setActive(false);
		question5e.setActive(false);
	}
	
	void question6Disable() {
		question6a.setEnabled(true);
		question6b.setEnabled(true);
		question6c.setEnabled(true);
		question6d.setEnabled(true);
		question6e.setEnabled(true);
		question6a.setActive(false);
		question6b.setActive(false);
		question6c.setActive(false);
		question6d.setActive(false);
		question6e.setActive(false);
	}
	@Override
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException {
		gcw = gc.getWidth();
		gch = gc.getHeight();
		questionbg = new Image("simple/questionbg.png");
		
		// set up font variables
		try {
			loadFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,
					      org.newdawn.slick.util.ResourceLoader.getResourceAsStream("font/atari.ttf"));
		} catch (FontFormatException e) {
				e.printStackTrace();
		} catch (IOException e) {
				e.printStackTrace();
		}
		loadTitleFont = loadFont.deriveFont(Font.BOLD,titleFontSize);
		titleFont = new BasicFont(loadTitleFont);
		
		// text area
		taComments = new EditField();
		taComments.setPosition(100,150);
		taComments.setSize(600, 50);
		taComments.setMultiLine(true);
		taComments.setTheme("editfield2");
		lblComments = new Label("Do you have any other comments?");
		lblComments.setPosition(0, 100);
		lblComments.setSize(800, 50);
		lblComments.setTheme("questionatari9c");
		
		lblFinish = new Label("That's the end!\n\n Thank you for playing 'How Rational Are You'.\n\n\n If you have any enquiries please e-mail: \n\ngbshabz@gmail.com");
		lblFinish.setPosition(0,250);
		lblFinish.setSize(800, 100);
		lblFinish.setTheme("questionatari9c");
		
		//btn back
		btnBack = new Button("Back to Results");
		btnBack.setSize(700, 30);
		btnBack.setPosition(50,565);
		btnBack.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 1;
			}
		});
		btnBack.setTheme("menubutton");
		
		//btn next
		btnNext = new Button("Next Page");
		btnNext.setSize(700, 30);
		btnNext.setPosition(50,530);
		btnNext.addCallback(new Runnable() {
			@Override
			public void run() {
				next = true;
				surveyPanel.setVisible(false);
				btnNext.setVisible(false);
				btnSubmit.setVisible(true);
				btnPrevious.setVisible(true);
				lblComments.setVisible(true);
				taComments.setVisible(true);
				
			}
		});
		btnNext.setTheme("menubutton");
		
		//btn previous
		btnPrevious = new Button("Previous Page");
		btnPrevious.setSize(700, 30);
		btnPrevious.setPosition(50,530);
		btnPrevious.addCallback(new Runnable() {
			@Override
			public void run() {
				next = false;
				surveyPanel.setVisible(true);
				btnNext.setVisible(true);
				btnSubmit.setVisible(false);
				btnPrevious.setVisible(false);
				lblComments.setVisible(false);
				taComments.setVisible(false);
				lblFinish.setVisible(false);
			}
		});
		btnPrevious.setTheme("menubutton");
		
		//btn next
		btnSubmit = new Button("Submit Answers");
		btnSubmit.setSize(700, 30);
		btnSubmit.setPosition(50,495);
		btnSubmit.addCallback(new Runnable() {
			@Override
			public void run() {
				submitAnswers();
				btnExit.setVisible(true);
				btnBack.setVisible(false);
				btnSubmit.setVisible(false);
				btnPrevious.setVisible(false);
				lblComments.setVisible(false);
				taComments.setVisible(false);
				lblFinish.setVisible(true);
			}
		});
		btnSubmit.setTheme("menubutton");
		
		//btn next
		btnExit = new Button("Exit");
		btnExit.setSize(700, 30);
		btnExit.setPosition(50,565);
		btnExit.addCallback(new Runnable() {
			@Override
			public void run() {
				enterState = 3;
			}
		});
		btnExit.setTheme("menubutton");
		
		// QUESTION 1 TOGGLES
		question1a = new ToggleButton("");
		question1a.setTheme("radiobutton");
		question1a.setSize(20,20);
		question1a.addCallback(new Runnable() {
			@Override
			public void run() {
				question1Answer = 1;
				question1Disable();
				question1a.setEnabled(false);
				question1a.setActive(true);
				}
		});
		
		question1b = new ToggleButton("");
		question1b.setTheme("radiobutton");
		question1b.setSize(20,20);
		question1b.addCallback(new Runnable() {
			@Override
			public void run() {
				question1Answer = 2;
				question1Disable();
				question1b.setEnabled(false);
				question1b.setActive(true);
				}
		});
		
		question1c = new ToggleButton("");
		question1c.setTheme("radiobutton");
		question1c.setSize(20,20);
		question1c.addCallback(new Runnable() {
			@Override
			public void run() {
				question1Answer = 3;
				question1Disable();
				question1c.setEnabled(false);
				question1c.setActive(true);
				}
		});
		
		question1d = new ToggleButton("");
		question1d.setTheme("radiobutton");
		question1d.setSize(20,20);
		question1d.addCallback(new Runnable() {
			@Override
			public void run() {
				question1Answer = 4;
				question1Disable();
				question1d.setEnabled(false);
				question1d.setActive(true);
				}
		});
		
		question1e = new ToggleButton("");
		question1e.setTheme("radiobutton");
		question1e.setSize(20,20);
		question1e.addCallback(new Runnable() {
			@Override
			public void run() {
				question1Answer = 5;
				question1Disable();
				question1e.setEnabled(false);
				question1e.setActive(true);
				}
		});
		
		// QUESTION 2
		question2a = new ToggleButton("");
		question2a.setTheme("radiobutton");
		question2a.setSize(20,20);
		question2a.addCallback(new Runnable() {
			@Override
			public void run() {
				question2Answer = 1;
				question2Disable();
				question2a.setEnabled(false);
				question2a.setActive(true);
				}
		});
		
		question2b = new ToggleButton("");
		question2b.setTheme("radiobutton");
		question2b.setSize(20,20);
		question2b.addCallback(new Runnable() {
			@Override
			public void run() {
				question2Answer = 2;
				question2Disable();
				question2b.setEnabled(false);
				question2b.setActive(true);
				}
		});
		
		question2c = new ToggleButton("");
		question2c.setTheme("radiobutton");
		question2c.setSize(20,20);
		question2c.addCallback(new Runnable() {
			@Override
			public void run() {
				question2Answer = 3;
				question2Disable();
				question2c.setEnabled(false);
				question2c.setActive(true);
				}
		});
		
		question2d = new ToggleButton("");
		question2d.setTheme("radiobutton");
		question2d.setSize(20,20);
		question2d.addCallback(new Runnable() {
			@Override
			public void run() {
				question2Answer = 4;
				question2Disable();
				question2d.setEnabled(false);
				question2d.setActive(true);
				}
		});
		
		question2e = new ToggleButton("");
		question2e.setTheme("radiobutton");
		question2e.setSize(20,20);
		question2e.addCallback(new Runnable() {
			@Override
			public void run() {
				question2Answer = 5;
				question2Disable();
				question2e.setEnabled(false);
				question2e.setActive(true);
				}
		});
		
		// QUESTION 3
		question3a = new ToggleButton("");
		question3a.setTheme("radiobutton");
		question3a.setSize(20,20);
		question3a.addCallback(new Runnable() {
			@Override
			public void run() {
				question3Answer = 1;
				question3Disable();
				question3a.setEnabled(false);
				question3a.setActive(true);
				}
		});
		
		question3b = new ToggleButton("");
		question3b.setTheme("radiobutton");
		question3b.setSize(20,20);
		question3b.addCallback(new Runnable() {
			@Override
			public void run() {
				question3Answer = 2;
				question3Disable();
				question3b.setEnabled(false);
				question3b.setActive(true);
				}
		});
		
		question3c = new ToggleButton("");
		question3c.setTheme("radiobutton");
		question3c.setSize(20,20);
		question3c.addCallback(new Runnable() {
			@Override
			public void run() {
				question3Answer = 3;
				question3Disable();
				question3c.setEnabled(false);
				question3c.setActive(true);
				}
		});
		
		question3d = new ToggleButton("");
		question3d.setTheme("radiobutton");
		question3d.setSize(20,20);
		question3d.addCallback(new Runnable() {
			@Override
			public void run() {
				question3Answer = 4;
				question3Disable();
				question3d.setEnabled(false);
				question3d.setActive(true);
				}
		});
		
		question3e = new ToggleButton("");
		question3e.setTheme("radiobutton");
		question3e.setSize(20,20);
		question3e.addCallback(new Runnable() {
			@Override
			public void run() {
				question3Answer = 5;
				question3Disable();
				question3e.setEnabled(false);
				question3e.setActive(true);
				}
		});
		
		// QUESTION 4
		question4a = new ToggleButton("");
		question4a.setTheme("radiobutton");
		question4a.setSize(20,20);
		question4a.addCallback(new Runnable() {
			@Override
			public void run() {
				question4Answer = 1;
				question4Disable();
				question4a.setEnabled(false);
				question4a.setActive(true);
				}
		});
		
		question4b = new ToggleButton("");
		question4b.setTheme("radiobutton");
		question4b.setSize(20,20);
		question4b.addCallback(new Runnable() {
			@Override
			public void run() {
				question4Answer = 2;
				question4Disable();
				question4b.setEnabled(false);
				question4b.setActive(true);
				}
		});
		
		question4c = new ToggleButton("");
		question4c.setTheme("radiobutton");
		question4c.setSize(20,20);
		question4c.addCallback(new Runnable() {
			@Override
			public void run() {
				question4Answer = 3;
				question4Disable();
				question4c.setEnabled(false);
				question4c.setActive(true);
				}
		});
		
		question4d = new ToggleButton("");
		question4d.setTheme("radiobutton");
		question4d.setSize(20,20);
		question4d.addCallback(new Runnable() {
			@Override
			public void run() {
				question4Answer = 4;
				question4Disable();
				question4d.setEnabled(false);
				question4d.setActive(true);
				}
		});
		
		question4e = new ToggleButton("");
		question4e.setTheme("radiobutton");
		question4e.setSize(20,20);
		question4e.addCallback(new Runnable() {
			@Override
			public void run() {
				question4Answer = 5;
				question4Disable();
				question4e.setEnabled(false);
				question4e.setActive(true);
				}
		});
		
		// QUESTION 5
		question5a = new ToggleButton("");
		question5a.setTheme("radiobutton");
		question5a.setSize(20,20);
		question5a.addCallback(new Runnable() {
			@Override
			public void run() {
				question5Answer = 1;
				question5Disable();
				question5a.setEnabled(false);
				question5a.setActive(true);
				}
		});
		
		question5b = new ToggleButton("");
		question5b.setTheme("radiobutton");
		question5b.setSize(20,20);
		question5b.addCallback(new Runnable() {
			@Override
			public void run() {
				question5Answer = 2;
				question5Disable();
				question5b.setEnabled(false);
				question5b.setActive(true);
				}
		});
		
		question5c = new ToggleButton("");
		question5c.setTheme("radiobutton");
		question5c.setSize(20,20);
		question5c.addCallback(new Runnable() {
			@Override
			public void run() {
				question5Answer = 3;
				question5Disable();
				question5c.setEnabled(false);
				question5c.setActive(true);
				}
		});
		
		question5d = new ToggleButton("");
		question5d.setTheme("radiobutton");
		question5d.setSize(20,20);
		question5d.addCallback(new Runnable() {
			@Override
			public void run() {
				question5Answer = 4;
				question5Disable();
				question5d.setEnabled(false);
				question5d.setActive(true);
				}
		});
		
		question5e = new ToggleButton("");
		question5e.setTheme("radiobutton");
		question5e.setSize(20,20);
		question5e.addCallback(new Runnable() {
			@Override
			public void run() {
				question5Answer = 5;
				question5Disable();
				question5e.setEnabled(false);
				question5e.setActive(true);
				}
		});
		
		// QUESTION 6
		question6a = new ToggleButton("");
		question6a.setTheme("radiobutton");
		question6a.setSize(20,20);
		question6a.addCallback(new Runnable() {
			@Override
			public void run() {
				question6Answer = 1;
				question6Disable();
				question6a.setEnabled(false);
				question6a.setActive(true);
				}
		});
		
		question6b = new ToggleButton("");
		question6b.setTheme("radiobutton");
		question6b.setSize(20,20);
		question6b.addCallback(new Runnable() {
			@Override
			public void run() {
				question6Answer = 2;
				question6Disable();
				question6b.setEnabled(false);
				question6b.setActive(true);
				}
		});
		
		question6c = new ToggleButton("");
		question6c.setTheme("radiobutton");
		question6c.setSize(20,20);
		question6c.addCallback(new Runnable() {
			@Override
			public void run() {
				question6Answer = 3;
				question6Disable();
				question6c.setEnabled(false);
				question6c.setActive(true);
				}
		});
		
		question6d = new ToggleButton("");
		question6d.setTheme("radiobutton");
		question6d.setSize(20,20);
		question6d.addCallback(new Runnable() {
			@Override
			public void run() {
				question6Answer = 4;
				question6Disable();
				question6d.setEnabled(false);
				question6d.setActive(true);
				}
		});
		
		question6e = new ToggleButton("");
		question6e.setTheme("radiobutton");
		question6e.setSize(20,20);
		question6e.addCallback(new Runnable() {
			@Override
			public void run() {
				question6Answer = 5;
				question6Disable();
				question6e.setEnabled(false);
				question6e.setActive(true);
				}
		});
		
		Label gap1 = new Label("");
		Label gap2 = new Label("");
		Label gap3 = new Label("");
		Label gap4 = new Label("");
		Label gap5 = new Label("");
		Label gap6 = new Label("");
		
		lblQuestion1 = new Label("The game was an enjoyable and fun experience overall.");
		lblQuestion1.setTheme("questionatari9c");
		lblQuestion2 = new Label("The game was competitive and engaging.");
		lblQuestion2.setTheme("questionatari9c");
		lblQuestion3 = new Label("I have gained knowledge in rational and logical thinking.");
		lblQuestion3.setTheme("questionatari9c");
		lblQuestion4 = new Label("The feedback I received was accurate and useful.");
		lblQuestion4.setTheme("questionatari9c");
		lblQuestion5 = new Label("I was not confused or lost during the game.");
		lblQuestion5.setTheme("questionatari9c");
		lblQuestion6 = new Label("I would play the game again.");
		lblQuestion6.setTheme("questionatari9c");
		
		surveyPanel = new DialogLayout();
		surveyPanel.setPosition(140, 60);
		surveyPanel.setSize(520, 500);

		// QUESTION 1 SETUP
		lblsa1 = new Label("strongly\nagree");
		lblsa1.setTheme("questionatari8");
		lbla1 = new Label("agree");
		lbla1.setTheme("questionatari8");
		lbln1 = new Label("neutral");
		lbln1.setTheme("questionatari8");
		lbld1 = new Label("disagree");
		lbld1.setTheme("questionatari8");
		lblsd1 = new Label("strongly\ndisagree");
		lblsd1.setTheme("questionatari8");
	
		DialogLayout.Group hSa1 = surveyPanel.createSequentialGroup(lblsa1).addGap(40);
		DialogLayout.Group hA1 =  surveyPanel.createSequentialGroup(lbla1).addGap(55);
		DialogLayout.Group hN1 =  surveyPanel.createSequentialGroup(lbln1).addGap(55);
		DialogLayout.Group hD1 = surveyPanel.createSequentialGroup(lbld1).addGap(40);
		DialogLayout.Group hSd1 = surveyPanel.createSequentialGroup(lblsd1).addGap(40);
		DialogLayout.Group hLabels1 = surveyPanel.createSequentialGroup(hSd1,hD1,hN1,hA1,hSa1);
		
		DialogLayout.Group hGap1 = surveyPanel.createSequentialGroup(gap1).addGap(20);
		DialogLayout.Group hQuestion1a = surveyPanel.createSequentialGroup(question1a).addGap(100);
		DialogLayout.Group hQuestion1b =  surveyPanel.createSequentialGroup(question1b).addGap(100);
		DialogLayout.Group hQuestion1c =  surveyPanel.createSequentialGroup(question1c).addGap(100);
		DialogLayout.Group hQuestion1d = surveyPanel.createSequentialGroup(question1d).addGap(100);
		DialogLayout.Group hQuestion1e = surveyPanel.createSequentialGroup(question1e).addGap(100);
		DialogLayout.Group hQuestion1 = surveyPanel.createSequentialGroup(hGap1, hQuestion1a, hQuestion1b,hQuestion1c,hQuestion1d,hQuestion1e);
		
		// QUESTION 2 SETUP
		lblsa2 = new Label("strongly\nagree");
		lblsa2.setTheme("questionatari8");
		lbla2 = new Label("agree");
		lbla2.setTheme("questionatari8");
		lbln2 = new Label("neutral");
		lbln2.setTheme("questionatari8");
		lbld2 = new Label("disagree");
		lbld2.setTheme("questionatari8");
		lblsd2 = new Label("strongly\ndisagree");
		lblsd2.setTheme("questionatari8");
		
		DialogLayout.Group hSa2 = surveyPanel.createSequentialGroup(lblsa2).addGap(40);
		DialogLayout.Group hA2 =  surveyPanel.createSequentialGroup(lbla2).addGap(55);
		DialogLayout.Group hN2 =  surveyPanel.createSequentialGroup(lbln2).addGap(55);
		DialogLayout.Group hD2 = surveyPanel.createSequentialGroup(lbld2).addGap(40);
		DialogLayout.Group hSd2 = surveyPanel.createSequentialGroup(lblsd2).addGap(40);
		DialogLayout.Group hLabels2 = surveyPanel.createSequentialGroup(hSd2,hD2,hN2,hA2,hSa2);

		DialogLayout.Group hGap2 = surveyPanel.createSequentialGroup(gap2).addGap(20);
		DialogLayout.Group hQuestion2a = surveyPanel.createSequentialGroup(question2a).addGap(100);
		DialogLayout.Group hQuestion2b =  surveyPanel.createSequentialGroup(question2b).addGap(100);
		DialogLayout.Group hQuestion2c =  surveyPanel.createSequentialGroup(question2c).addGap(100);
		DialogLayout.Group hQuestion2d = surveyPanel.createSequentialGroup(question2d).addGap(100);
		DialogLayout.Group hQuestion2e = surveyPanel.createSequentialGroup(question2e).addGap(100);
		DialogLayout.Group hQuestion2 = surveyPanel.createSequentialGroup(hGap2, hQuestion2a, hQuestion2b,hQuestion2c,hQuestion2d,hQuestion2e);
		
		// QUESTION 3 SETUP
		lblsa3 = new Label("strongly\nagree");
		lblsa3.setTheme("questionatari8");
		lbla3 = new Label("agree");
		lbla3.setTheme("questionatari8");
		lbln3 = new Label("neutral");
		lbln3.setTheme("questionatari8");
		lbld3 = new Label("disagree");
		lbld3.setTheme("questionatari8");
		lblsd3 = new Label("strongly\ndisagree");
		lblsd3.setTheme("questionatari8");

		DialogLayout.Group hSa3 = surveyPanel.createSequentialGroup(lblsa3).addGap(40);
		DialogLayout.Group hA3 =  surveyPanel.createSequentialGroup(lbla3).addGap(55);
		DialogLayout.Group hN3 =  surveyPanel.createSequentialGroup(lbln3).addGap(55);
		DialogLayout.Group hD3 = surveyPanel.createSequentialGroup(lbld3).addGap(40);
		DialogLayout.Group hSd3 = surveyPanel.createSequentialGroup(lblsd3).addGap(40);
		DialogLayout.Group hLabels3 = surveyPanel.createSequentialGroup(hSd3,hD3,hN3,hA3,hSa3);

		DialogLayout.Group hGap3 = surveyPanel.createSequentialGroup(gap3).addGap(20);
		DialogLayout.Group hQuestion3a = surveyPanel.createSequentialGroup(question3a).addGap(100);
		DialogLayout.Group hQuestion3b =  surveyPanel.createSequentialGroup(question3b).addGap(100);
		DialogLayout.Group hQuestion3c =  surveyPanel.createSequentialGroup(question3c).addGap(100);
		DialogLayout.Group hQuestion3d = surveyPanel.createSequentialGroup(question3d).addGap(100);
		DialogLayout.Group hQuestion3e = surveyPanel.createSequentialGroup(question3e).addGap(100);
		DialogLayout.Group hQuestion3 = surveyPanel.createSequentialGroup(hGap3, hQuestion3a, hQuestion3b,hQuestion3c,hQuestion3d,hQuestion3e);
		
		//QUESTION 4 SETUP
		lblsa4 = new Label("strongly\nagree");
		lblsa4.setTheme("questionatari8");
		lbla4 = new Label("agree");
		lbla4.setTheme("questionatari8");
		lbln4 = new Label("neutral");
		lbln4.setTheme("questionatari8");
		lbld4 = new Label("disagree");
		lbld4.setTheme("questionatari8");
		lblsd4 = new Label("strongly\ndisagree");
		lblsd4.setTheme("questionatari8");

		DialogLayout.Group hSa4 = surveyPanel.createSequentialGroup(lblsa4).addGap(40);
		DialogLayout.Group hA4 =  surveyPanel.createSequentialGroup(lbla4).addGap(55);
		DialogLayout.Group hN4 =  surveyPanel.createSequentialGroup(lbln4).addGap(55);
		DialogLayout.Group hD4 = surveyPanel.createSequentialGroup(lbld4).addGap(40);
		DialogLayout.Group hSd4 = surveyPanel.createSequentialGroup(lblsd4).addGap(40);
		DialogLayout.Group hLabels4 = surveyPanel.createSequentialGroup(hSd4,hD4,hN4,hA4,hSa4);

		DialogLayout.Group hGap4 = surveyPanel.createSequentialGroup(gap4).addGap(20);
		DialogLayout.Group hQuestion4a = surveyPanel.createSequentialGroup(question4a).addGap(100);
		DialogLayout.Group hQuestion4b =  surveyPanel.createSequentialGroup(question4b).addGap(100);
		DialogLayout.Group hQuestion4c =  surveyPanel.createSequentialGroup(question4c).addGap(100);
		DialogLayout.Group hQuestion4d = surveyPanel.createSequentialGroup(question4d).addGap(100);
		DialogLayout.Group hQuestion4e = surveyPanel.createSequentialGroup(question4e).addGap(100);
		DialogLayout.Group hQuestion4 = surveyPanel.createSequentialGroup(hGap4, hQuestion4a, hQuestion4b,hQuestion4c,hQuestion4d,hQuestion4e);
	
		//QUESTION 5 SETUP
		lblsa5 = new Label("strongly\nagree");
		lblsa5.setTheme("questionatari8");
		lbla5 = new Label("agree");
		lbla5.setTheme("questionatari8");
		lbln5 = new Label("neutral");
		lbln5.setTheme("questionatari8");
		lbld5 = new Label("disagree");
		lbld5.setTheme("questionatari8");
		lblsd5 = new Label("strongly\ndisagree");
		lblsd5.setTheme("questionatari8");

		DialogLayout.Group hSa5 = surveyPanel.createSequentialGroup(lblsa5).addGap(40);
		DialogLayout.Group hA5 =  surveyPanel.createSequentialGroup(lbla5).addGap(55);
		DialogLayout.Group hn5 =  surveyPanel.createSequentialGroup(lbln5).addGap(55);
		DialogLayout.Group hD5 = surveyPanel.createSequentialGroup(lbld5).addGap(40);
		DialogLayout.Group hSd5 = surveyPanel.createSequentialGroup(lblsd5).addGap(40);
		DialogLayout.Group hLabels5 = surveyPanel.createSequentialGroup(hSd5,hD5,hn5,hA5,hSa5);

		DialogLayout.Group hGap5 = surveyPanel.createSequentialGroup(gap5).addGap(20);
		DialogLayout.Group hQuestion5a = surveyPanel.createSequentialGroup(question5a).addGap(100);
		DialogLayout.Group hQuestion5b =  surveyPanel.createSequentialGroup(question5b).addGap(100);
		DialogLayout.Group hQuestion5c =  surveyPanel.createSequentialGroup(question5c).addGap(100);
		DialogLayout.Group hQuestion5d = surveyPanel.createSequentialGroup(question5d).addGap(100);
		DialogLayout.Group hQuestion5e = surveyPanel.createSequentialGroup(question5e).addGap(100);
		DialogLayout.Group hQuestion5 = surveyPanel.createSequentialGroup(hGap5, hQuestion5a, hQuestion5b,hQuestion5c,hQuestion5d,hQuestion5e);
		
		// QUESTION 6 SETUP
		lblsa6 = new Label("strongly\nagree");
		lblsa6.setTheme("questionatari8");
		lbla6 = new Label("agree");
		lbla6.setTheme("questionatari8");
		lbln6 = new Label("neutral");
		lbln6.setTheme("questionatari8");
		lbld6 = new Label("disagree");
		lbld6.setTheme("questionatari8");
		lblsd6 = new Label("strongly\ndisagree");
		lblsd6.setTheme("questionatari8");

		DialogLayout.Group hSa6 = surveyPanel.createSequentialGroup(lblsa6).addGap(40);
		DialogLayout.Group hA6 =  surveyPanel.createSequentialGroup(lbla6).addGap(55);
		DialogLayout.Group hn6 =  surveyPanel.createSequentialGroup(lbln6).addGap(55);
		DialogLayout.Group hD6 = surveyPanel.createSequentialGroup(lbld6).addGap(40);
		DialogLayout.Group hSd6 = surveyPanel.createSequentialGroup(lblsd6).addGap(40);
		DialogLayout.Group hLabels6 = surveyPanel.createSequentialGroup(hSd6,hD6,hn6,hA6,hSa6);

		DialogLayout.Group hGap6 = surveyPanel.createSequentialGroup(gap6).addGap(20);
		DialogLayout.Group hQuestion6a = surveyPanel.createSequentialGroup(question6a).addGap(100);
		DialogLayout.Group hQuestion6b =  surveyPanel.createSequentialGroup(question6b).addGap(100);
		DialogLayout.Group hQuestion6c =  surveyPanel.createSequentialGroup(question6c).addGap(100);
		DialogLayout.Group hQuestion6d = surveyPanel.createSequentialGroup(question6d).addGap(100);
		DialogLayout.Group hQuestion6e = surveyPanel.createSequentialGroup(question6e).addGap(100);
		DialogLayout.Group hQuestion6 = surveyPanel.createSequentialGroup(hGap6, hQuestion6a, hQuestion6b,hQuestion6c,hQuestion6d,hQuestion6e);
	
		
		surveyPanel.setHorizontalGroup(surveyPanel.createParallelGroup()
				.addWidget(lblQuestion1)
				.addWidget(lblQuestion2)
				.addWidget(lblQuestion3)
				.addWidget(lblQuestion4)
				.addWidget(lblQuestion5)
				.addWidget(lblQuestion6)
				.addGroup(surveyPanel.createParallelGroup(hLabels1, hQuestion1, hLabels2, hQuestion2, hLabels3, hQuestion3,
						hLabels4, hQuestion4, hLabels5, hQuestion5, hLabels6, hQuestion6)));
		
		surveyPanel.setVerticalGroup(surveyPanel.createSequentialGroup()
				.addWidget(lblQuestion1)
				.addGroup(surveyPanel.createParallelGroup(gap1, question1a, question1b,question1c,question1d,question1e))
				.addGroup(surveyPanel.createParallelGroup(lblsd1,lbld1,lbln1,lbla1,lblsa1))
				.addGap(25).addWidget(lblQuestion2)
				.addGroup(surveyPanel.createParallelGroup(gap2, question2a, question2b,question2c,question2d,question2e))
				.addGroup(surveyPanel.createParallelGroup(lblsd2,lbld2,lbln2,lbla2,lblsa2))
				.addGap(25).addWidget(lblQuestion3)
				.addGroup(surveyPanel.createParallelGroup(gap3, question3a, question3b,question3c,question3d,question3e))
				.addGroup(surveyPanel.createParallelGroup(lblsd3,lbld3,lbln3,lbla3,lblsa3))
				.addGap(25).addWidget(lblQuestion4)
				.addGroup(surveyPanel.createParallelGroup(gap4, question4a, question4b,question4c,question4d,question4e))
				.addGroup(surveyPanel.createParallelGroup(lblsd4,lbld4,lbln4,lbla4,lblsa4))
				.addGap(25).addWidget(lblQuestion5)
				.addGroup(surveyPanel.createParallelGroup(gap5, question5a, question5b,question5c,question5d,question5e))
				.addGroup(surveyPanel.createParallelGroup(lblsd5,lbld5,lbln5,lbla5,lblsa5))
				.addGap(25).addWidget(lblQuestion6)
				.addGroup(surveyPanel.createParallelGroup(gap6, question6a, question6b,question6c,question6d,question6e))
				.addGroup(surveyPanel.createParallelGroup(lblsd6,lbld6,lbln6,lbla6,lblsa6)));
		
		
		question1c.setActive(true);
		question2c.setActive(true);
		question3c.setActive(true);
		question4c.setActive(true);
		question5c.setActive(true);
		question6c.setActive(true);
		question1c.setEnabled(false);
		question2c.setEnabled(false);
		question3c.setEnabled(false);
		question4c.setEnabled(false);
		question5c.setEnabled(false);
		question6c.setEnabled(false);
		
		question1Answer = 3; 
		question2Answer = 3;
		question3Answer = 3;
		question4Answer = 3;
		question5Answer = 3;
		question6Answer = 3;
		// set player variable
		/*
		playerID = HRRUClient.cs.getPlayer();
		if(playerID == 1)
		{
			playerScore = HRRUClient.cs.getP1().getScore();
			playerName = HRRUClient.cs.getP1().getName();
		}
		else
		{
			playerScore = HRRUClient.cs.getP2().getScore();
			playerName = HRRUClient.cs.getP2().getName();
		}
		scores = HRRUClient.cs.getScores();
		*/
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException {
		g.drawImage(questionbg, 0, 0);
		g.setFont(titleFont.get());
		g.drawString("> " + start_message + "" + ticker, 50, 25);
		if(!next)
		{
			g.drawImage(new Image("simple/surveyLine.png"), 171,85);
			g.drawImage(new Image("simple/surveyLine.png"), 171,166);
			g.drawImage(new Image("simple/surveyLine.png"), 171,247);
			g.drawImage(new Image("simple/surveyLine.png"), 171,328);
			g.drawImage(new Image("simple/surveyLine.png"), 171,409);
			g.drawImage(new Image("simple/surveyLine.png"), 171,490);
		}
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
		clock3 += delta;
		clock2 += delta;
		// full message ticker
		if(clock3 > 100){
			if(full_start_counter < full_start_message.length())
			{
				start_message += full_start_message.substring(full_start_counter, full_start_counter+1);
				full_start_counter++;
				clock3-=100;
			}
		}
		// ticker symbol
		if(clock2>999)
		{
			clock2-=1000;
			if(tickerBoolean) 
			{
				ticker = "|";
				tickerBoolean = false;
			}
			else
			{
				ticker = "";
				tickerBoolean = true;
			}
		}
		if(enterState == 1)
			sbg.enterState(17);
		else if(enterState == 3)
			gc.exit();
	}

	@Override
	public int getID() {
		return 20;
	}

}