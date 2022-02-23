package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import demo.CheckMoveLogic;
import demo.CommandDemo;
import structures.GameState;
import structures.basic.*;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		// hello this is a change
		
		gameState.gameInitalised = true;
		
		gameState.something = true;
		
		//draw tiles
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		makeTiles(out, gameState);
		
		//put avatars
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		putAvatars(out, gameState);
		
		//draw hand cards
		gameState.getHumanModel().initHand(out);
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//show mana and health
		showManaAndHealth(out, gameState);
		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);		
		
	}
	
	public void makeTiles(ActorRef out, GameState gameState) {
		Board board = gameState.getBoard();
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 5; j ++) {
				BasicCommands.drawTile(out, board.getTile(i, j), 0);
			}
		}
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}

	public void putAvatars(ActorRef out, GameState gameState) {
		Board board = gameState.getBoard();
		Avatar humanAvatar = gameState.getHumanAvatar();
		Avatar aiAvatar = gameState.getAIAvatar();
		//set avatar's position by tile and draw
		gameState.getHumanModel().setAndDrawAvatar(out, 1, 2);
		gameState.getAIModel().setAndDrawAvatar(out, 7, 2);
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		//set avatar's health and attack
		BasicCommands.setUnitHealth(out, humanAvatar, 20);
		BasicCommands.setUnitAttack(out, humanAvatar, 2);
		BasicCommands.setUnitHealth(out, aiAvatar, 20);
		BasicCommands.setUnitAttack(out, aiAvatar, 2);
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}
	
	public void showManaAndHealth(ActorRef out, GameState gameState) {
		//set mana and count round number
		gameState.setManaAndCountRound();
		//show them
		BasicCommands.setPlayer1Health(out, gameState.getHuman());
		BasicCommands.setPlayer2Health(out, gameState.getAI());
		BasicCommands.setPlayer1Mana(out, gameState.getHuman());
		BasicCommands.setPlayer2Mana(out, gameState.getAI());
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	}
}


