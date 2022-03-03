package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import controllers.AutoControl;
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
		
		//create ai controller
		gameState.autoControl = new AutoControl(out, gameState.getBoard(), gameState.getAIModel(), gameState.getAI());
		
		//draw tiles
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.getBoardModel().makeTiles(out);
		
		//put avatars
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.getHumanModel().setAndDrawAvatar(out, 1, 2);
		gameState.getAIModel().setAndDrawAvatar(out, 7, 2);
		
		//draw hand cards
		gameState.getHumanModel().initHand(out);
		gameState.getAIModel().initHand(out);
		//let the buffer rest
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//show mana and health
		gameState.getBoardModel().showStatisticsAndCountRound(out, gameState);
		
		// User 1 makes a change
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution
		//CheckMoveLogic.executeDemo(out);		
		
	}
	
}


