package main;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class View {
	
	//color class for easy access
	public static class Theme {
		public static final Color YELLOW = new Color(201, 180, 88);
		public static final Color GREEN = new Color(106, 170, 100);
		public static final Color SHADED = new Color(120, 124, 126);
		public static final Color CLEAN = new Color(211, 214, 218);
		public static final Color WHITE = new Color(255, 255, 255);
		public static final Color BLACK = new Color(0, 0, 0);
		public static Color toColor(int c) {
			switch(c) {
			case Model.CORRECT: return Theme.GREEN;
			case Model.INCORRECT: return  Theme.SHADED;
			case Model.PARTCORRECT: return Theme.YELLOW;
			default: return Theme.CLEAN;
			}		
		}
	}
	
	private JFrame frame;
	private Keyboard keyboard;
	private Guesses guesses;

	//to allow the controller to add listeners
	public JFrame getFrame() { return frame; }
	public Keyboard getKeyboard() { return keyboard; }
	public Guesses getGuesses() { return guesses; }
	
	public View() {

		//instantiate variables
		frame = new JFrame();
		keyboard = new Keyboard();
		guesses = new Guesses();
		
		//do normal swing stuff
		frame.setTitle("Wordle");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
		frame.setLayout(new BorderLayout());
		frame.setSize(new Dimension(700, 900));
		
		//create the two views
		frame.add(keyboard, BorderLayout.SOUTH);
		frame.add(guesses, BorderLayout.CENTER);
		
		//more normal swing stuff
		frame.setVisible(true);
	}
	
	

	/** Keyboard that can be clicked and changes color and stuff */
	public class Keyboard extends JPanel {
		
		private static final long serialVersionUID = -1119221842949667292L; //to quiet error
		
		///buttons keeps all the buttons by their character
		private Map<String, KeyboardButton> buttons = new HashMap<String, KeyboardButton>();
		public Map<String, KeyboardButton> getButtons() { return buttons; }
		
		//create keyboard
		public Keyboard() {
			setLayout(new GridLayout(3, 1));

			//keyboard layout
			//logic for ENTER and DELETE are done in KeyboardButton
			for (String[] keys: new String[][] {
				{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"},
				{"A", "S", "D", "F", "G", "H", "J", "K", "L"},
				{"ENTER", "Z", "X", "C", "V", "B", "N", "M", "DELETE"}
			}) {
				JPanel row = new JPanel();
				row.setBackground(Theme.WHITE);
				row.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 6));
				add(row);
				
				for (String key : keys) {
					KeyboardButton button = new KeyboardButton(key);
					buttons.put(key, button);
					row.add(button);
				}
			}
		}
		
		//update colors for all buttons
		public void update(Map<Character, Integer> keyStatus) {
			
			//just calls the update method for each of them
			//has to convert from character to string to actually index buttons
			//it must be like this because of ENTER and DELETE (can't be chars)
			for (char key : keyStatus.keySet())
				buttons.get(("" + key).toUpperCase()).update(keyStatus.get(key));
		}
		
		public class KeyboardButton extends JPanel {

			private static final long serialVersionUID = -2987594965279587797L;
			private JLabel label;
			
			public KeyboardButton(String key) {
				setBackground(Theme.CLEAN);
				setLayout(new BorderLayout());
				setPreferredSize(new Dimension((key.equals("DELETE") || key.equals("ENTER")) ? 65 : 43, 58));

				//JLabel with antialiasing
				//not an actual class because it's never used again
				label = new JLabel(key, JLabel.CENTER) {
					private static final long serialVersionUID = 4147159913028889729L;
					protected void paintComponent(Graphics g) {
						Graphics2D graphics = (Graphics2D) g;
						graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						super.paintComponent(graphics);
					}
				};
				label.setFont(new Font("Clear Sans", Font.BOLD, 13));
				label.setForeground(Theme.BLACK);
				
				add(label);
			}
			
			//its base is a JPanel but that doesn't have rounded borders so now it does
			@Override
			protected void paintComponent(Graphics g) {
				int width = getWidth();
				int height = getHeight();
				Graphics2D graphics = (Graphics2D) g;
				graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				graphics.setColor(getBackground());
				graphics.fillRoundRect(0, 0, width-1, height-1, 8, 8);
			}
			
			public void update(int c) {
				setBackground(Theme.toColor(c));
				label.setForeground(c != Model.BLANK ? Theme.WHITE : Theme.BLACK);
			}
		}
	}

	/** Displays all the guesses and stuff */
 	public class Guesses extends JPanel {
		
		private static final long serialVersionUID = -298004967293635662L;
		private String text = "";
		private String guess = "";
		private String[] guesses = new String[6];
		private int[][] correct = new int[6][5];
		private int turn = 0;
		
		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D graphics = (Graphics2D) g; //cast graphics for antialiasing
			graphics.setStroke(new BasicStroke(2)); //stroke for boxes
			graphics.setFont(new Font("Clear Sans", Font.BOLD, 32)); //font for big text
			FontMetrics fm = graphics.getFontMetrics(graphics.getFont()); //fontmetrics for later
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 

			//draw background
			graphics.setColor(Theme.WHITE);
			graphics.fillRect(0, 0, getWidth(), getHeight());
			
			//center
			int startx = (getWidth() - 62 * 5 - 5 * 4) / 2;
			int starty = (getHeight() - 62 * 6 - 5 * 5) / 2;

			//draw tiles
			for(int i = 0; i < 6; i++) {
				for (int j = 0; j < 5; j++) {
					
					//this is past guesses, we do filled boxes and white text
					if (i < turn) {
						String character = ("" + guesses[i].charAt(j)).toUpperCase();

						graphics.setColor(Theme.toColor(correct[i][j]));
						graphics.fillRect(startx + j * 67, starty + i * 67, 62, 62);

						int width = fm.stringWidth(character);
						int height = fm.getHeight();

						graphics.setColor(Theme.WHITE);
						graphics.drawString(character,
							startx + j * 67 + (62 - width) / 2,
							starty + i * 67 + 62 - height / 2
						);
					}
					
					//this is partial guess, we paint character and do darker box than otherwise
					else if (i == turn && j < guess.length()) {
						String character = ("" + guess.charAt(j)).toUpperCase();

						graphics.setColor(Theme.SHADED);
						graphics.drawRect(startx + j * 67, starty + i * 67, 62, 62);

						int width = fm.stringWidth(character);
						int height = fm.getHeight();

						graphics.setColor(Theme.BLACK);
						graphics.drawString(character,
							startx + j * 67 + (62 - width) / 2,
							starty + i * 67 + 62 - height / 2
						);
					}
					
					//otherwise it's just empty box with clean border
					else {
						graphics.setColor(Theme.CLEAN);
						graphics.drawRect(startx + j * 67, starty + i * 67, 62, 62);
					}
				}
			}
			
			//if there's no auxiliary text we can just be done
			if (text.equals("")) return;
			
			//draw auxiliary text
			graphics.setFont(new Font("Clear Sans", Font.BOLD, 16));
			fm = graphics.getFontMetrics(graphics.getFont());
			
			//the output of fm.getHeight() is actually 19 when we explicitly specified it to
			//be 16 when defining the class. I don't know why this happens, maybe DPI stuff?
			//regardless, when you subtract 5 from this it cooperates perfectly and when you
			//don't it's weirdly offset
			int width = fm.stringWidth(text);
			int height = fm.getHeight() - 5;

			//draw black rounded background
			graphics.setColor(Theme.BLACK);
			graphics.fillRoundRect(
				(getWidth() - width - 32) / 2,
				(starty - height - 32) / 2,
				width + 32,
				height + 32,
				8, 8
			);
			
			//draw text
			graphics.setColor(Theme.WHITE);
			graphics.drawString(text,
				(getWidth() - width) / 2,
				(starty + height) / 2
			);
		}
		
		//since paintComponent uses these values to draw it, updating them and repainting is all it takes
		//TODO: Since these are leashed I probably don't actually have to update them rather than just
		//binding them at the start of each game. Do that maybe?
		public void update(String guess, String[] guesses, int[][] correct, int turn) {
			this.guess = guess;
			this.guesses = guesses;
			this.correct = correct;
			this.turn = turn;
			repaint();
		}
		
		//sets auxiliary text (winning, losing)
		//"" shows nothing
		public void setText(String text) {
			this.text = text;
			repaint();
		}
	}
 }