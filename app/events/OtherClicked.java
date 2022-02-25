package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Player;
import structures.basic.Tile;

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
//			for(int i = 0; i < 9; i++) {
//				for(int j = 0; j < 5; j++) {
//					if(gameState.getBoard().getTile(i, j).highlighted) {
//						Tile t = gameState.getBoard().getTile(i, j);
//						BasicCommands.drawTile(out, t, 0);
//						try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
//						//t.highlighted = false;
//					}
//				}
//			}
//			try {Thread.sleep(100);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.getHumanModel().highlightControl(out, 0);
			
			BasicCommands.drawCard(out, hModel.getCard(previousIndex-1), previousIndex, 0);
			//gameState.tempCardIndex = -1;
		}
		
	}

}


