package yx48_zh16.server.serverMVC.serverModel;

import common.receivers.IApplication;
import common.virtualNetwork.IRoom;

/**
 * Wrapper for the chat room
 *
 */
public class TeamWrapper {
	
	/**
	 * The chat room
	 */
	private IRoom iTeam;
	
	/**
	 * Person who sent this room
	 */
	private IApplication teamCreator;
	
	/**
	 * Class constructor
	 * @param iChatRoom - a chat room
	 * @param iConnection - a user who created the room
	 */
	public TeamWrapper(IRoom iChatRoom, IApplication iConnection) {
		this.iTeam = iChatRoom;
		this.teamCreator = iConnection;
	}
	
	@Override
	public String toString() {
		return this.iTeam.getInfo().getName();
	}

	/**
	 * Get the chat room
	 * @return the chat room this wrapper is holding
	 */
	public IRoom getChatRoom() {
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
