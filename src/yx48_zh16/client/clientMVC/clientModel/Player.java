package yx48_zh16.client.clientMVC.clientModel;

import java.rmi.RemoteException;

import common.cmd.ARoomMsgCmd;
import common.msg.IRoomMsg;
import common.packet.RoomDataPacket;
import common.receivers.IRoomMember;
import provided.datapacket.DataPacketAlgo;
import yx48_zh16.client.clientMiniMVC.clientMiniModel.ITeamToMiniModelAdapter;

/**
 * Definition of a user in a chat room level.
 *
 */
public class Player implements IRoomMember {
	
	/**
	 * The room name the chat user is in.
	 */
	@SuppressWarnings("unused")
	private String roomName;
	
	/**
	 * The user name.
	 */
	private String userName;
	
	/**
	 * The algorithm for processing messages.
	 */
	private DataPacketAlgo<Void, Void> messageProcessingAlgo;
	
	/**
	 * The adapter passed to the chat room for the mini model to update.
	 */
	transient ITeamToMiniModelAdapter adapter;
	
	/**
	 * Constructor of a chat user object.
	 * @param name the room name the user is in.
	 * @param username the user name.
	 */
	public Player(String name, String username) {
		this.roomName = name;
		this.userName = username;
	}

	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return this.userName;
	}

//	@Override
//	public ADataPacket receiveMsg(DataPacket<?, ?> msg) throws RemoteException {
//		if (messageProcessingAlgo.getCmd(msg.getData().getID()) != null) {
//			((AGameMsgCmd<IPlayerMsg>) messageProcessingAlgo.getCmd(msg.getData().getID())).setCmd2ModelAdpt(adapter.getCmdToModelAdapter());
//		}
//		System.out.println(msg.getData().getID());
//		msg.execute(messageProcessingAlgo);
//		return msg;
//	}
	
	/**
	 * Method for setting algoProcessing visitors.
	 * @param messageProcessingAlgo the message processing algorithms of type DataPacketAlgo.
	 */
	public void setMsgProcessingAlgo(DataPacketAlgo<Void, Void> messageProcessingAlgo) {
		this.messageProcessingAlgo = messageProcessingAlgo;
	}
	
	/**
	 * The setter method for the command to model adapter.
	 * @param adapter the new command to model adapter.
	 */
	public void setCRMtoMiniModelAdapter(ITeamToMiniModelAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * Getter method for the mini model adapter.
	 * @return the mini model adapter that represents the chat room.
	 */
	public ITeamToMiniModelAdapter getChatRoomToMiniModelAdapter() {
		return this.adapter;
	}

	@Override
	public void receiveMsg(RoomDataPacket<? extends IRoomMsg> msg) throws RemoteException {
		if (messageProcessingAlgo.getCmd(msg.getData().getID()) != null) {
			((ARoomMsgCmd<IRoomMsg>) messageProcessingAlgo.getCmd(msg.getData().getID())).setCmd2ModelAdpt(adapter.getCmdToModelAdapter());
		}
		System.out.println(msg.getData().getID());
		msg.execute(messageProcessingAlgo);
	}
	
}
