package yx48_zh16.client.clientMiniMVC.clientMiniModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JComponent;

import yx48_zh16.client.clientMiniMVC.clientMiniModel.IMiniModel2ViewAdapter;
import common.ICmd2ModelAdapter;
import common.msg.IRoomMsg;
import common.msg.IStringMsg;
import common.msg.room.IAddMemberMsg;
import common.packet.RoomDataPacket;
import common.receivers.IApplication;
import common.receivers.IRoomMember;
import provided.mixedData.IMixedDataDictionary;
import provided.mixedData.MixedDataDictionary;
import provided.mixedData.MixedDataKey;
import provided.rmiUtils.IRMI_Defs;
import yx48_zh16.client.clientMVC.clientModel.Player;
import yx48_zh16.client.clientMVC.clientModel.Room;

/**
 * Definition of the mini model that holds a chat room.
 *
 */
public class ClientMiniModel {
	
	/**
	 * client DB
	 */
	private IMixedDataDictionary mdd;
	
	/**
	 * The user holding this local chat room.
	 */
	private Player myUser;
	
	/**
	 * The exported stub of the user.
	 */
	private IRoomMember myUserStub;
	
	/**
	 * The chat room that the mini model holds.
	 */
	private Room myTeam;
	
	/**
	 * Adapter for the model to communicate to the world.
	 */
	private IMiniModel2ViewAdapter _miniModelUpdateAdater;

	/**
	 * Adapter for the command to communicate to the model.
	 */
	private ICmd2ModelAdapter cmdToModelAdpt;
	
	/**
	 * Constructor of the mini model.
	 * @param roomID the ID of the chat room held.
	 * @param roomName the name of the chat room held.
	 * @param userName the user holding the chat room.
	 * @param currentMembers the collection of members in the chat room.
	 * @param miniModelUpdateAdapter adapter for the model to update its information.
	 * @param applicationMe application me
	 */
	public ClientMiniModel(UUID roomID, String roomName, String userName, Collection<IRoomMember> currentMembers, IMiniModel2ViewAdapter miniModelUpdateAdapter, IApplication applicationMe) {
		
		// Create an IUser
		this.myUser = new Player(roomName, userName);
		try {
			myUserStub = (IRoomMember) UnicastRemoteObject.exportObject((IRoomMember) myUser, IRMI_Defs.STUB_PORT_CLIENT);
			// Create an ITeam
			this.myTeam = new Room(roomID, roomName, myUserStub, userName, applicationMe);
			this.myUser.setMsgProcessingAlgo(this.myTeam.getMessageProcessingAlgo());
			
			// Add the people to your list of stubs
			for (IRoomMember currentMember : currentMembers) {
				this.myTeam.addUser(currentMember);
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		this.mdd = new MixedDataDictionary();
		
		
		this._miniModelUpdateAdater = miniModelUpdateAdapter;
		this.cmdToModelAdpt = new ICmd2ModelAdapter() {
			

			@Override
			public void displayComponent(Supplier<JComponent> componentFac, String title) {
				// TODO Auto-generated method stub
				System.out.println("Displaying component in cmd2model adapter");
//				System.out.println(title);
				_miniModelUpdateAdater.displayComponent(componentFac.get());
				
			}

			@Override
			public void displayText(String text) {
				_miniModelUpdateAdater.displayMessage(text);

			}

			@Override
			public void sendMessageToMember(IRoomMsg message, IRoomMember member) {
				RoomDataPacket<IRoomMsg> msg = new RoomDataPacket<IRoomMsg>(message, myUserStub);
				try {
					member.receiveMsg(msg);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void sendMessageToAll(IRoomMsg message) {
				RoomDataPacket<IRoomMsg> msg = new RoomDataPacket<IRoomMsg>(message, myUserStub);
//				member.receiveMsg(member);
				myTeam.sendMsg(msg);
			}

			@Override
			public String getAPIKey() {
				// TODO Auto-generated method stub
				return "AIzaSyBLiQTpVmEZdf1Nu-u1oeuPa9PM6bfweeI";
			}

			@Override
			public <T> void putItemInDB(MixedDataKey<T> key, T value) {
				// TODO Auto-generated method stub
				mdd.put(key, value);
			}

			@Override
			public <T> T getItemInDB(MixedDataKey<T> key) {
				// TODO Auto-generated method stub
				return mdd.get(key);
			}

		};
		
		myUser.setCRMtoMiniModelAdapter(new ITeamToMiniModelAdapter() {
			
			@Override
			public ICmd2ModelAdapter getCmdToModelAdapter() {
				return cmdToModelAdpt;
			}

			@Override
			public void leave() {
				// TODO Auto-generated method stub
				_miniModelUpdateAdater.leave();
			}

		});
		
		myTeam.setCRMtoMiniModelAdapter(new ITeamToMiniModelAdapter() {
			
			@Override
			public ICmd2ModelAdapter getCmdToModelAdapter() {
				return cmdToModelAdpt;
			}

			@Override
			public void leave() {
				// TODO Auto-generated method stub
				_miniModelUpdateAdater.leave();
			}

		});
	}

	/**
	 * Method processing the holder of chat room leaving the room.
	 */
	public void leave() {
//		System.out.println("Sending a leave message to " + myTeam.getPlayers().size() + "members");
//		GameDataPacket<IPlayerMsg> leaveMessage = new GameDataPacket<IPlayerMsg>(ILeaveMsg.make(this.myTeam.getMe()), this.myTeam.getMe());
//		this.myTeam.sendTeamMsg(leaveMessage);
	}
	
	/**
	 * Getter method of the chat room.
	 * @return the chat room held in the mini MVC.
	 */
	public Room getTeam() {
		return myTeam;
	}
	
	/**
	 * Getter method of the user holding the chat room.
	 * @return the user holding the chat room.
	 */
	public Player getUser() {
		return myUser;
	}
	
	/**
	 * Process the user sending messages to all other members in the chatroom.
	 * @param messageData the message to be sent to all other members.
	 */

	public void sendMessage(IRoomMsg messageData) {
		RoomDataPacket<IRoomMsg> message = new RoomDataPacket<IRoomMsg>(messageData, this.myUserStub);
		this.myTeam.sendMsg(message);
		
	}
	
	/**
	 * Starter method for the mini model.
	 */
	public void start() {
		
	}

	/**
	 * Tells all members to add me as a new member when the user joins.
	 */
	public void joinRoom() {
		try {
			sendMessage(IAddMemberMsg.make(this.myUserStub, this.myUserStub.getName()));
			System.out.println("CLIENT -- ADD MEMBER SENT");
//			System.out.println()
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			System.out.println("Failure when sending IAddMemberMsg");
			e.printStackTrace();
		}
	}

	/**
	 * Sends the message to other members in the chat room.
	 * @param text the String messages to be sent.
	 */
	public void sendTextMsg(String text) {
		try {
			this._miniModelUpdateAdater.displayMessage(myUser.getName() + ": " + text);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RoomDataPacket<IRoomMsg> message = new RoomDataPacket<IRoomMsg>(IStringMsg.make(text), this.myUserStub);
		this.myTeam.sendChatRoomMsg(message);
		
	}
	
	/**
	 * Method for sending an image to members in the chat room.
	 * @param imgName the file name of the image stored in "images" package.
	 */
	public void sendImageMsg(String imgName) {
//		RoomDataPacket<IRoomMsg> message;
//		try {
//			message = new RoomDataPacket<IRoomMsg>(new ImageMessage(imgName), this.myUserStub);
//			this.myTeam.sendMsg(message);
//			
//			this._miniModelUpdateAdater.displayComponent(new JLabel(((ImageMessage)message.getData()).getImage()));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Error sending image!");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}



}
