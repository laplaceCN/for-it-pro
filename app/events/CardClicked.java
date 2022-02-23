package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int handPosition = message.get("position").asInt();
		Player hModel = gameState.getHuman();
		if(hModel.getCard(handPosition-1).getManacost() > gameState.getHuman().getMana()) {
			if(gameState.cardClickedAndWaiting == true) {
				gameState.cardClickedAndWaiting = false;
				gameState.getHumanModel().showAvailables(out, gameState.tempCardIndex, 0);
			}
			BasicCommands.addPlayer1Notification(out, "Not enough manas to use this card.", 2);
			//let the buffer rest
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.addPlayer1Notification(out, "Please select again.", 2);
		} else {
			if(!gameState.cardClickedAndWaiting) {
				gameState.cardClickedAndWaiting = true;
			} else {
				int previousIndex = gameState.tempCardIndex;
				BasicCommands.drawCard(out, hModel.getCard(previousIndex-1), previousIndex, 0);
			}
			gameState.tempCardIndex = handPosition;
			//let the buffer rest
			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			BasicCommands.drawCard(out, hModel.getCard(handPosition-1), handPosition, 1);
			gameState.getHumanModel().showAvailables(out, handPosition, 1);
		}
	}

}
