package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Avatar;
import structures.basic.Board;
import structures.basic.Player;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 * The main purpose of this class is to do what the player wants to do to end the turn.
 * The main action of this class is to reset the game state between turns and call the methods of the AI.
 *
 * @author Ether group
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		BasicCommands.addPlayer1Notification(out, "EndTurnClicked!", 2);
		gameState.getHumanModel().drawOneCard(out);



		gameState.getBoardModel().perTurnSkill();


		//清楚暂存器状态
		Player hModel = gameState.getHuman();
		if(gameState.cardClickedAndWaiting) {
			gameState.cardClickedAndWaiting = false;
			int previousIndex = gameState.tempCardIndex;
			//let the buffer rest
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.getHumanModel().showAvailables(out, previousIndex, 0);
			//let the buffer rest
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.drawCard(out, hModel.getCard(previousIndex-1), previousIndex, 0);
			//gameState.tempCardIndex = -1;
		}

		gameState.autoControl.autoPlay();
		gameState.getAIModel().drawOneCard(out);
		gameState.getBoardModel().showStatisticsAndCountRound(out,gameState);
		gameState.getBoardModel().recoverCardState();

	}



}
