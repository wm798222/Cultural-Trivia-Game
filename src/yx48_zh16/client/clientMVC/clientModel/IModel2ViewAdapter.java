package yx48_zh16.client.clientMVC.clientModel;

import java.util.Collection;
import java.util.UUID;

import common.receivers.IApplication;
import common.receivers.IRoomMember;
import yx48_zh16.client.clientMiniMVC.IMainAccessMiniAdapter;

/**
 * The definition of the adapter for the main MVC model to update.
 *
 */
public interface IModel2ViewAdapter {

	/**
	 * Add a room.
	 * @param roomID - room ID
	 * @param roomName - name of the chat room
	 * @param userName - name of user
	 * @param currentMembers  - current members in the chat room
	 * @param applicationMe  - stub for me
	 * @return the auto-get-back mainAccessMiniAdapter upon giving the miniAccessMainAdapter.
	 */
	public IMainAccessMiniAdapter addRoom(UUID roomID, String roomName, String userName, Collection<IRoomMember> currentMembers, IApplication applicationMe);

	/**
	 * Initialize discovery server panel.
	 */
	public void startDiscoveryPanel();

	/**
	 * Add newly connected user to the list of connected users.
	 * @param newConnection - a newly connected user.
	 */
	public void updateConnectedUser(UserWrapper newConnection);

	/**
	 * Add a newly received room to the collection of all available rooms.
	 * @param availRoom - a newly received room
	 */
	public void updateAvailRooms(TeamIdentityWrapper availRoom);

	/**
	 * Add a newly connected room to the collection of all connected rooms.
	 * @param chatRoomMemberWrapper - a new connected room with a wrapper outside
	 */
	public void updateConnectedRooms(TeamWrapper chatRoomMemberWrapper);

	/**
	 * Delete a chat room from the list of available rooms
	 * @param chatRoomMemberWrapper - the chat room to be deleted.
	 */
	public void deleteFromConnectedChatRoom(TeamWrapper chatRoomMemberWrapper);

	/**
	 * Method for deleting a user from all connected users.
	 * @param userWrapper - the user to be deleted
	 */
	public void deleteFromConnectedUsers(UserWrapper userWrapper);

	/**
	 * Add a user to the collection of all available users.
	 * @param userWrapper - the user to be added with a wrapper outside.
	 */
	public void updateAvailableUser(UserWrapper userWrapper);

}
