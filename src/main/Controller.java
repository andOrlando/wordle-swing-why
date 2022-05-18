package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;

import main.View.Keyboard.KeyboardButton;

public class Controller {
	
	private View view;
	private Model model;
	private boolean gameOver = false;
	
	public Controller(View a, Model b) {
		view = a;
		model = b;
	
		//add keylistener to frame
		view.getFrame().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				update(KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());
			}
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
		});
		
		//add click listeners to keyboard
		Map<String, KeyboardButton> buttons = view.getKeyboard().getButtons();
		for (String key : buttons.keySet()) {
			buttons.get(key).addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent e) {
					update(key.toLowerCase());
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseReleased(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
		}
	}
	
	public void update(String key) {
		if (gameOver) {
			gameOver = false;
			model = new Model();
			view.getGuesses().setText("");
		}
		else if (!gameOver) model.keyEvent(key);
	
		view.getGuesses().update(model.getGuess(), model.getGuesses(), model.getCorrect(), model.getTurn());
		view.getKeyboard().update(model.getKeyStatus());
	
		if (model.getTurn() > 0 && model.getGuesses()[model.getTurn() - 1].equals(model.getAnswer())) {
			gameOver = true;
			view.getGuesses().setText("You win!");
		}
		else if (model.getTurn() == 6) {
			gameOver = true;
			view.getGuesses().setText(model.getAnswer().toUpperCase());
		}
	}
}
