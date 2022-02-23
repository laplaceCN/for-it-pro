package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//clear waiting state token
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
		
	}

}


