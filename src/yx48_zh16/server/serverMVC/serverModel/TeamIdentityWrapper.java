package yx48_zh16.server.serverMVC.serverModel;

import common.identity.IRoomIdentity;
import common.receivers.IApplication;

/**
 * Wrapper for the chat room
 *
 */
public class TeamIdentityWrapper {
	
	/**
	 * The chat room
	 */
	private IRoomIdentity iTeam;
	
	/**
	 * Person who sent this room
	 */
	private IApplication teamCreator;
	
	/**
	 * Class constructor
	 * @param iChatRoom - a chat room
	 * @param iConnection - a user who created the room
	 */
	public TeamIdentityWrapper(IRoomIdentity iChatRoom, IApplication iConnection) {
		this.iTeam = iChatRoom;
		this.teamCreator = iConnection;
	}
	
	@Override
	public String toString() {
		return this.iTeam.getName();
	}

	/**
	 * Get the chat room
	 * @return the chat room this wrapper is holding
	 */
	public IRoomIdentity getRoomIdentity() {
		return this.iTeam;
	}
	
	/**
	 * Get the person who sent this room
	 * @return - a user
	 */
	public IApplication getCreator() {
		return this.teamCreator;
	}
	
}
