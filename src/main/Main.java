package main;

public class Main {

	public static void main(String[] args) {
		
		//set up a controller with access to mainframe and model
		new Controller(new View(), new Model());
	}
}
