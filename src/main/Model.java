package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Model {
	
	public static final int INCORRECT = 0;
	public static final int PARTCORRECT = 1;
	public static final int CORRECT = 2;
	public static final int BLANK = 3;

	//final answer
	private String answer;
	
	//current guess (whtehr partial or not)
	private String guess = "";
	
	//previous guesses
	private String[] guesses = new String[6];
	
	//the color that each position will be assigned
	private int[][] correct = new int[6][5];

	//keyboard character status
	private Map<Character, Integer> keyStatus = new HashMap<Character, Integer>();

	//current guess number
	private int turn = 0;
	
	//getters
	public String getAnswer() { return answer; }
	public String getGuess() { return guess; }
	public String[] getGuesses() { return guesses; }
	public int[][] getCorrect() { return correct; }
	public Map<Character, Integer> getKeyStatus() { return keyStatus; }
	public int getTurn() { return turn; }
	
	public Model() {
		//get answers scannner
		File answersFile = new File("src/answers.txt");
		Scanner answers = null;
		try { answers = new Scanner(answersFile); }
		catch (FileNotFoundException e) { e.printStackTrace(); }
		
		//get line before actual answer
		int index = (int) (Math.random() * answersFile.length() / 6) - 1;
		for (int i = 0; i < index; i++)
			answers.nextLine();
		
		//get actual answer and close scanner
		answer = answers.nextLine();
		answers.close();
		
		//instantiate keyStatus for resets
		for (char c : "abcdefghijklmnopqrstuvwxyz".toCharArray())
			keyStatus.put(c, BLANK);
		
		//cheat
		System.out.println(answer);
	}
	
	//handle string key events
	public void keyEvent(String event) {
		switch (event) {
		case "enter": if(validate()) guess(); break;
		case "backspace": if (guess.length() > 0) guess = guess.substring(0, guess.length()-1); break;
		case "delete": if (guess.length() > 0) guess = guess.substring(0, guess.length()-1); break;
		default:
			//add new character to guess
			if (event.length() > 1) break;
			if (guess.length() < 5) guess += event;
			break;
		}
	}

	//check if current word is a real word
	public boolean validate() {
		if (guess.length() < 5) return false;
		
		File allowedFile = new File("src/allowed.txt");
		Scanner allowed = null;
		try { allowed = new Scanner(allowedFile); }
		catch (FileNotFoundException e) { e.printStackTrace(); }

		String match = allowed.findWithinHorizon(guess, 0);
		
		//compare it against the massive list of words
		return match != null;
	}
	
	//score the current guess
	public void guess() {
		
		//get character counts
		HashMap<Character, Integer> chars = new HashMap<Character, Integer>();
		for (int i = 0; i < guess.length(); i++) {
			char c = answer.charAt(i);
			chars.put(c, chars.get(c) == null ? 1 : chars.get(c) + 1);
		}
		
		//get greens
		boolean[] greens = new boolean[5];
		for (int i = 0; i < guess.length(); i++) {
			char c = guess.charAt(i);
			if (c == answer.charAt(i)) {
				greens[i] = true;
				chars.put(c, chars.get(c) - 1);
			}
			else greens[i] = false;
		}
		
		//get yellows
		boolean[] yellows = new boolean[5];
		for (int i = 0; i < guess.length(); i++) {
			if (greens[i]) continue;
			char c = guess.charAt(i);
			if (answer.contains("" + c) && chars.get(c) > 0) {
				yellows[i] = true;
				chars.put(c, chars.get(c) - 1);
			}
		}
		
		//update gamestate and answerstate
		for (int i = 0; i < 5; i++) {
			char c = guess.charAt(i);
			if (greens[i]){
				correct[turn][i] = CORRECT;
				keyStatus.put(c, CORRECT);
			}
			else if (yellows[i]){
				correct[turn][i] = PARTCORRECT;
				if(keyStatus.get(c) != CORRECT) keyStatus.put(c, PARTCORRECT);
			}
			else {
				correct[turn][i] = INCORRECT;
				if(keyStatus.get(c) == BLANK) keyStatus.put(c, INCORRECT);
			}
		}
		guesses[turn] = guess;
		
		//reset for next turn
		turn++;
		guess = "";
	}
}
