package yx48_zh16.server.serverMiniMVC.serverMiniModel;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.UUID;
import java.util.function.Supplier;

import javax.swing.JComponent;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.IMiniModel2ViewAdapter;
import common.ICmd2ModelAdapter;
import common.msg.room.IAddMemberMsg;
import common.packet.RoomDataPacket;
import common.msg.IRoomMsg;
import common.msg.IStringMsg;
import common.receivers.IApplication;
import common.receivers.IRoomMember;
import provided.mixedData.MixedDataKey;
import provided.rmiUtils.IRMI_Defs;
import yx48_zh16.server.serverMVC.serverModel.Player;
import yx48_zh16.server.serverMVC.serverModel.Room;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.ITeamToMiniModelAdapter;

/**
 * Definition of the mini model that holds a team.
 *
 */
public class ServerMiniModel {
	
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
	@SuppressWarnings("rawtypes")
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
	 * @param serverStub server's stub
	 */
	@SuppressWarnings("rawtypes")
	public ServerMiniModel(UUID roomID, String roomName, String userName, Collection<IRoomMember> currentMembers, IMiniModel2ViewAdapter miniModelUpdateAdapter, IApplication serverStub) {
		
		// Create an IUser
		this.myUser = new Player(roomName, userName);
		try {
//			myUserStub = (IRoomMember) UnicastRemoteObject.exportObject((IRoomMember) myUser, IRMI_Defs.STUB_PORT_SERVER);
			myUserStub = (IRoomMember) UnicastRemoteObject.exportObject((IRoomMember) myUser, IRMI_Defs.STUB_PORT_SERVER);
			// Create an ITeam
			this.myTeam = new Room(roomID, roomName, myUserStub, userName, serverStub);
			this.myUser.setMsgProcessingAlgo(this.myTeam.getMessageProcessingAlgo());
			
			// Add the people to your list of stubs
			for (IRoomMember currentMember : currentMembers) {
				this.myTeam.addUser(currentMember);
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		new GameAccessServerAdapter(this);
		
		
		this._miniModelUpdateAdater = miniModelUpdateAdapter;
		this.cmdToModelAdpt = new ICmd2ModelAdapter() {
			

			@Override
			public void displayComponent(Supplier<JComponent> componentFac, String title) {
				// TODO Auto-generated method stub
				System.out.println("Displaying component in cmd2model adapter");
				System.out.println(componentFac.get());
				_miniModelUpdateAdater.displayComponent(componentFac.get());
				
			}

			@Override
			public void displayText(String text) {
				_miniModelUpdateAdater.displayMessage(text);

			}

			@Override
			public void sendMessageToMember(IRoomMsg message, IRoomMember member) {
				
					RoomDataPacket<IRoomMsg> msg = new RoomDataPacket<IRoomMsg>(message, myUserStub);
//					member.receiveMsg(member);
//					myTeam.sendMsg(msg);
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

			@SuppressWarnings("unchecked")
			@Override
			public <T> void putItemInDB(MixedDataKey<T> key, T value) {
				// TODO Auto-generated method stub
				_miniModelUpdateAdater.putInServerDB(key, value);
			}

			@SuppressWarnings("unchecked")
			@Override
			public <T> T getItemInDB(MixedDataKey<T> key) {
				// TODO Auto-generated method stub
				return (T) _miniModelUpdateAdater.getFromServerDB(key);
			}

		};
		
		myUser.setCRMtoMiniModelAdapter(new ITeamToMiniModelAdapter() {
			
			@Override
			public ICmd2ModelAdapter getCmdToModelAdapter() {
				return cmdToModelAdpt;
			}

		});
		
		myTeam.setCRMtoMiniModelAdapter(new ITeamToMiniModelAdapter() {
			
			@Override
			public ICmd2ModelAdapter getCmdToModelAdapter() {
				return cmdToModelAdpt;
			}

		});
	}

	/**
	 * Method processing the holder of chat room leaving the room.
	 */
	public void leave() {
//		System.out.println("Sending a leave message to " + myTeam.getPlayers().size() + "members");
//		RoomDataPacket<IRoomMsg> leaveMessage = new RoomDataPacket<IRoomMsg>(ILeaveMsg.make(this.myTeam.getMe()), this.myTeam.getMe());
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
//		TeamDataPacket<ITeamMsg> message;
//		try {
//			message = new TeamDataPacket<ITeamMsg>(new ImageMessage(imgName), this.myUserStub);
//			this.myTeam.sendTeamMsg(message);
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
	
	/**
	 * Method for sending a map to clients.
	 */
	public void sendMapMsg() {
		
		RoomDataPacket<IRoomMsg> message;
		try {
			System.out.println("This is try");
			IMapMessage mapMsg = IMapMessage.make(IMapMessage.GetID());
			System.out.println("mapMsg title is: " + mapMsg.getTitle());
			System.out.println("mapMsg is: " + mapMsg);
			message = new RoomDataPacket<IRoomMsg>(mapMsg, this.myUserStub);
			System.out.println("message is: " + message);
			this.myTeam.sendMsg(message);
			
//			this._miniModelUpdateAdater.displayComponent(new JLabel(((IMapMessage)message.getData()).getImage()));
		} catch (Exception e) {
			System.out.println("This is catch");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
//<<<<<<< .mine
//	public void sendGameMsg() {
//		
//		RoomDataPacket<IRoomMsg> message;
//		
//		
//		Collection<IRoomMember> members = this.myTeam.getMembers();
//		
//		if (members.size() < 1) {
////			this._miniModelUpdateAdater.displayNotEnoughPeopleWindow();
//		}
//		else {
//		
////			IApplication application = new Application();
//			
//			IRoomMember[] array = members.toArray(new IRoomMember[0]);
//
//			
//			for (int i = 0; i < members.size() / 2; i++) {
//				teamA.add(array[i]);
//				Team_A_List.add(i);
//			}
//			for (int i = members.size() / 2; i < members.size(); i++) {
//				teamB.add(array[i]);
//				Team_B_List.add(i);
//			}
//		
////			IApplication app = (IApplication) UnicastRemoteObject.exportObject((IApplication) application, IRMI_Defs.STUB_PORT_EXTRA);
//			
////			// create chat rooms for teamA and teamB and pass them in IStartGameMsg
////			this._miniModelUpdateAdater.createRoomForTeamAndGetRoom();
//			
//				
//			for (int i = 0; i < members.size() / 2; i++) {
//				IIdentity identity = new Identity();
//				
//				VirtualNetworkPlayers teammates = new VirtualNetworkPlayers(teamA, identity);
//				VirtualNetworkPlayers allPlayers = new VirtualNetworkPlayers(members, identity);
//				IStartGameMsg gameMsg = IStartGameMsg.make(teammates, allPlayers, this.serverStub);	
//				message = new RoomDataPacket<IRoomMsg>(gameMsg, this.myUserStub);	
//				try {
//					array[i].receiveMsg(message);
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					System.out.println("Exception when creating the game and assigning teams");
//					e.printStackTrace();
//				}
//			}	
//			
//			for (int i = members.size() / 2; i < members.size(); i++) {
//				IIdentity identity = new Identity();
//				
//				VirtualNetworkPlayers teammates = new VirtualNetworkPlayers(teamB, identity);
//				VirtualNetworkPlayers allPlayers = new VirtualNetworkPlayers(members, identity);
//				IStartGameMsg gameMsg = IStartGameMsg.make(teammates, allPlayers, this.serverStub);	
//				message = new RoomDataPacket<IRoomMsg>(gameMsg, this.myUserStub);	
//				try {
//					array[i].receiveMsg(message);
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					System.out.println("Exception when creating the game and assigning teams");
//					e.printStackTrace();
//				}
//			}
//			
//		}
//		
//		for (IRoomMember member : members) {
//			this.sendMessage(messageData);
//		}
		
		
		
		// divide them into two groups
//		if (members.size() < 2) {
//			this._miniModelUpdateAdater.displayNotEnoughPeopleWindow();
	
//////////////////////////////////////////////////////////////////////////////////////////////////

//	public void sendGameMsg() {
//		
//		RoomDataPacket<IRoomMsg> message;
//		
//		
//		Collection<IRoomMember> members = this.myTeam.getMembers();
//		
//		if (members.size() < 1) {
////			this._miniModelUpdateAdater.displayNotEnoughPeopleWindow();

//		}
//		else {
//		
////			IApplication application = new Application();
//			
//			IRoomMember[] array = members.toArray(new IRoomMember[0]);
//			Collection<IRoomMember> teamA = new ArrayList<IRoomMember>();
//			Collection<IRoomMember> teamB = new ArrayList<IRoomMember>();
//			
//			for (int i = 0; i < members.size() / 2; i++) {
//				teamA.add(array[i]);
//			}
//			for (int i = members.size() / 2; i < members.size(); i++) {
//				teamB.add(array[i]);
//			}
//		
////			IApplication app = (IApplication) UnicastRemoteObject.exportObject((IApplication) application, IRMI_Defs.STUB_PORT_EXTRA);
//			
////			// create chat rooms for teamA and teamB and pass them in IStartGameMsg
////			this._miniModelUpdateAdater.createRoomForTeamAndGetRoom();
//			
//				
//			for (int i = 0; i < members.size() / 2; i++) {
//				IIdentity identity = new Identity();
//				
//				VirtualNetworkPlayers teammates = new VirtualNetworkPlayers(teamA, identity);
//				VirtualNetworkPlayers allPlayers = new VirtualNetworkPlayers(members, identity);
//				IStartGameMsg gameMsg = IStartGameMsg.make(teammates, allPlayers, this.serverStub, "Game Adapter");	
//				message = new RoomDataPacket<IRoomMsg>(gameMsg, this.myUserStub);	
//				try {
//					array[i].receiveMsg(message);
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					System.out.println("Exception when creating the game and assigning teams");
//					e.printStackTrace();
//				}
//			}	
//			
//			for (int i = members.size() / 2; i < members.size(); i++) {
//				
//				MixedDataKey mddAdapterKey = new MixedDataKey()
//				
//				IIdentity identity = new Identity();
//				
//				VirtualNetworkPlayers teammates = new VirtualNetworkPlayers(teamB, identity);
//				VirtualNetworkPlayers allPlayers = new VirtualNetworkPlayers(members, identity);
//				IStartGameMsg gameMsg = IStartGameMsg.make(teammates, allPlayers, this.serverStub, "Game Adapter");	
//				message = new RoomDataPacket<IRoomMsg>(gameMsg, this.myUserStub);	
//				try {
//					array[i].receiveMsg(message);
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					System.out.println("Exception when creating the game and assigning teams");
//					e.printStackTrace();
//				}
//			}
//			
//		}
//		
////		for (IRoomMember member : members) {
////			this.sendMessage(messageData);
////		}
//		
//		
//		
//		// divide them into two groups
////		if (members.size() < 2) {
////			this._miniModelUpdateAdater.displayNotEnoughPeopleWindow();
////		}
////		else {
////			int size = members.size();
////			int numGroups = size % 3;
////			for (int i = 0; i < numGroups; i++) {
////				
////			}
////			
////			try {
//////				System.out.println("This is try");
////				IStartGameMsg mapMsg = IStartGameMsg.make();
////				System.out.println("mapMsg title is: " + mapMsg.getTitle());
////				System.out.println("mapMsg is: " + mapMsg);
////				message = new RoomDataPacket<IRoomMsg>(mapMsg, this.myUserStub);
////				System.out.println("message is: " + message);
////				this.myTeam.sendMsg(message);
////				
//////				this._miniModelUpdateAdater.displayComponent(new JLabel(((IMapMessage)message.getData()).getImage()));
////			} catch (Exception e) {
////				System.out.println("This is catch");
////				// TODO Auto-generated catch block
////				e.printStackTrace();
////			}
////		}
//	}


	
		
	}


