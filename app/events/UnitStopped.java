package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Indicates that a unit instance has stopped moving. 
 * The event reports the unique id of the unit.
 * 
 * { 
 *   messageType = “unitStopped”
 *   id = <unit id>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 * The main purpose of this class is to indicate to the player that a particular card has stopped
 * 	The action of this class is to send a text prompt to the player
 * @author Ether group
 *
 */
public class UnitStopped implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		int unitid = message.get("id").asInt();


		if ((unitid < 100)) {



			BasicCommands.addPlayer1Notification(out,"this " +unitid+ " card has stopped",2);
		}else {
			BasicCommands.addPlayer1Notification(out,"this avatar has stopped",2);
		}




	}

}
