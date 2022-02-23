package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();
		
		if (gameState.cardClickedAndWaiting == true) {
			gameState.cardClickedAndWaiting = false;
			//stop showing available tiles
			gameState.getHumanModel().showAvailables(out, gameState.tempCardIndex, 0);
			//use the card
			boolean flag = gameState.getHumanModel().useSelectedCard(out, gameState.tempCardIndex, tilex, tiley);
			//在这里让函数返回一个布尔值的目的是为了判断放置卡牌是否成功。如果没有成功则不删除卡牌。

			//remove card from hand
			if(flag == true) {gameState.getHumanModel().deleteHandCard(out, gameState.tempCardIndex);}
		}
		
	}

}
