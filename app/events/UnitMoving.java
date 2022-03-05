package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Indicates that a unit instance has started a move. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = “unitMoving”
 *   id = <unit id>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 * The main purpose of this class is to indicate to the player that a particular card is starting to move
 *	The action of this class is to send a text prompt to the player
 * @author Ether group
 */
public class UnitMoving implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int unitid = message.get("id").asInt();

		if ((unitid < 100)) {
			BasicCommands.addPlayer1Notification(out,"this " +unitid+ " card is moving",2);
		}else {
			BasicCommands.addPlayer1Notification(out,"this avatar is moving",2);
		}


	}



}
