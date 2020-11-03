package yx48_zh16.client.clientMVC.clientModel;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import common.cmd.AAppMsgCmd;
import common.identity.IRoomIdentity;
import common.msg.IAppMsg;
import common.msg.application.IAcceptInviteMsg;
import common.msg.application.IAddRoomMsg;
import common.msg.application.IConnectMsg;
import common.msg.application.IInviteToRoomMsg;
import common.msg.application.IQuitMsg;
import common.msg.application.IRequestToJoinMsg;
import common.msg.collection.IAddAppsMsg;
import common.msg.collection.IAddRoomInfosMsg;
import common.msg.collection.IRequestRoomsMsg;
import common.msg.room.ILeaveMsg;
import common.msg.status.IFailureMsg;
import common.msg.status.IStatusMsg;
import common.packet.AppDataPacket;
import common.packet.RoomDataPacket;
import common.receivers.IApplication;
import common.receivers.IRoomMember;
import common.virtualNetwork.IRoom;
import provided.datapacket.DataPacketAlgo;
import provided.datapacket.IDataPacketID;
import provided.discovery.IEndPointData;
import provided.discovery.impl.model.DiscoveryConnector;
import provided.discovery.impl.model.RemoteAPIStubFactory;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIUtils;
import yx48_zh16.client.clientMiniMVC.IMainAccessMiniAdapter;

/**
 * ChatApp model
 *
 */
public class ClientModel {
	
	/**
	 * Chat app Client
	 */
	private IApplication Client;
	
	/**
	 * Stub for the Client
	 */
	private IApplication ClientStub;
	
	/**
	 * Client name
	 */
	private String ClientName;
	
	/**
	 * Default command for the message processing algo
	 */
	private AAppMsgCmd<IAppMsg> defautlCmd = new AAppMsgCmd<IAppMsg>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -411957128823345214L;

		@Override
		public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
			// If it gets here, other Clients have sent something strange so send back a failure msg
			sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Wrong msg type!"), ClientStub));
			return null;
		}
		
	};
	
	/**
	 * Message processing visitor
	 */
	private DataPacketAlgo<Void, Void> ClientMessageProcessingAlgo;
	
	/**
	 * Available rooms
	 */
	private ArrayList<IRoomIdentity> availRooms = new ArrayList<IRoomIdentity>();
	
	/**
	 * Connected rooms
	 */
	private ArrayList<Room> connectedRooms = new ArrayList<Room>();
	
	/**
	 * Adapter for the Client to update
	 */
	private IUserUpdateAdapter ClientUpdateAdapter;
		
	/**
	 * RMIUtils object stored for reference.
	 */
	private RMIUtils rmiUtils;
	
	/**
	 * Local registry stored for reference.
	 */
	private Registry localRegistry;
	
	/**
	 * Model to view adapter
	 */
	IModel2ViewAdapter iModel2ViewAdapter;
	
	/**
	 * Storing connected Clients
	 */
	ArrayList<IApplication> connectedClientStubs = new ArrayList<IApplication>();
	
	/**
	 * Stubs of available Clients
	 */
	ArrayList<IApplication> availableClientStubs = new ArrayList<IApplication>();
	
	/**
	 * Discovery connector
	 */
	private DiscoveryConnector discConn;
	
	/**
	 * Remote API Stub Factory
	 */
	private RemoteAPIStubFactory<IApplication> remoteAPIStubFac;  // Substitute the API's connection-level Remote interface for "IApplicationEntity"
	
	/**
	 * Adapter for the main model to access the methods on the mini model
	 */
	private HashMap<UUID, IMainAccessMiniAdapter> _roomIdToMainAccessMiniMap = new HashMap<UUID, IMainAccessMiniAdapter>();
	
	/**
	 * Class constructor
	 * @param iModel2ViewAdapter - a model to view adapter
	 * @param ClientUpdateAdapter - Client adapter
	 */
	public ClientModel(IModel2ViewAdapter iModel2ViewAdapter, IUserUpdateAdapter ClientUpdateAdapter) {
		this.iModel2ViewAdapter = iModel2ViewAdapter;
		this.ClientUpdateAdapter = ClientUpdateAdapter;
	}
	
	/**
	 * Start the model
	 * @param Clientname - Client name 
	 */
	public void start(String Clientname) {
		this.ClientName = Clientname;
		startRMI();
	}
	
//	/**
//	 * Send a message to an Client asynchronously
//	 * @param receiver - the msg receiver
//	 * @param msg - a msg to send
//	 */
//	public void sendMsgThread(IApplication receiver, AppDataPacket<? extends IAppMsg> msg) {
//		Thread msgThread = new Thread() {
//			public void run() {
//				try {
//					receiver.receiveApplicationMsg(msg);
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		};
//		msgThread.start();
//	}
	
	/**
	 * Send a message to an Client asynchronously
	 * @param receiver - the msg receiver
	 * @param msg - a msg to send
	 */
	public void sendMsgThread(IApplication receiver, AppDataPacket<? extends IAppMsg> msg) {
		Thread msgThread = new Thread() {
			public void run() {
				try {
					receiver.receiveMsg(msg);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		msgThread.start();
	}
	
	 /**
	  * Starting the RMI subsystem
	  */
	 private void startRMI() {
//		System.setProperty("java.security.policy","file:/provided/rmiUtils/security.policy");
		rmiUtils = new RMIUtils(text -> System.out.println(text));
		rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT_CLIENT);
		
//	    // instantiate the two utility classes now that the RMIUtils has been started:
	    try {
			this.discConn = new DiscoveryConnector(rmiUtils, this.ClientName, "Client");

		} catch (RemoteException e1) {
			e1.printStackTrace();
		} // getClientName() and getBoundName() are accessors for that info in the model
	    this.remoteAPIStubFac = new RemoteAPIStubFactory<IApplication>(rmiUtils); // Instantiate the API-specific factory, replacing the "IApplicationEntity" with the proper API-defined Remote interface
//	    
//	    // Do anything else that needs to be done to initialize the system...
//	    // The connection-level stub should be created and bound into the local Registry at this point, before the discovery panel is started.
	    Client = new IApplication() {

			@Override
			public void receiveMsg(AppDataPacket<? extends IAppMsg> msg) throws RemoteException {
				// TODO Auto-generated method stub
				System.out.println(msg.getData().getID());
				Thread msgThread = new Thread() {
					public void run() {
						msg.execute(ClientMessageProcessingAlgo);
					}
				};
				msgThread.start();
			}

			@Override
			public String getName() throws RemoteException {
				// TODO Auto-generated method stub
				return ClientName;
			}    	
		};
		
		localRegistry = rmiUtils.getLocalRegistry();
		try {
			ClientStub = (IApplication) UnicastRemoteObject.exportObject(Client, IRMI_Defs.STUB_PORT_CLIENT);
			localRegistry.bind("Client", ClientStub);
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		
		ClientMessageProcessingAlgo = new DataPacketAlgo<Void, Void>(defautlCmd) {
			
			/**
			 * Serial Version UID
			 */
			private static final long serialVersionUID = 8157031229389953675L;
			DataPacketAlgo<Void, Void> thisAlgo = this;
			{
				AAppMsgCmd<IAppMsg> connectMsgHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 46055148648415174L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						IApplication receivedUser = (IApplication) host.getSender();
			        	connectedClientStubs.add(receivedUser);
			        	iModel2ViewAdapter.updateConnectedUser(new UserWrapper(receivedUser, ClientUpdateAdapter));
				        //TODO: return status message
						return null;
					}
				};
				thisAlgo.setCmd(IConnectMsg.GetID(), connectMsgHandling);
							
				// Handle failure Msg
				AAppMsgCmd<IAppMsg> failureMsgHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -1483829431959778274L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						// TODO Auto-generated method stub
//						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Wrong msg type!"), ClientStub));
						return null;
					}
					
				};
				thisAlgo.setCmd(IFailureMsg.GetID(), failureMsgHandling);
				
				// Handle chat rooms requesting from the other Clients
				AAppMsgCmd<IAppMsg> requestChatRoomsHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -4395881900843821185L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host,
							Void... params) {
						//Send back IAddChatRoomsCollectionMsg
						ArrayList<IRoomIdentity> returnedRooms = new ArrayList<IRoomIdentity>();
						for (Room room : connectedRooms) {
//							Room newRoom = new Room(room.getID(), room.getName(), room.getMe(), ClientName);
//							for (IRoomMember currentMember : room.getMembers()) {
//								newRoom.addUser(currentMember);
//							}
//							newRoom.addUser(room.getMe());
							returnedRooms.add((IRoomIdentity) room.getInfo());
						}
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddRoomInfosMsg.make(returnedRooms), ClientStub));

						return null;
					}
				};
				thisAlgo.setCmd(IRequestRoomsMsg.GetID(), requestChatRoomsHandling);
				
				// Handle chat rooms receving from other Clients
				AAppMsgCmd<IAppMsg> receivingChatRoomsHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -6124753801384109887L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						System.out.println("Processing chatrooms received from sender");
						Collection<IRoomIdentity> newAvailableRoomsIdentity = ((IAddRoomInfosMsg) host.getData()).getCollection();
				        for (IRoomIdentity identity : newAvailableRoomsIdentity) {
				        	
				        	availRooms.add(identity);
				        	iModel2ViewAdapter.updateAvailRooms(new TeamIdentityWrapper(identity, host.getSender()));
				        }
//				        sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(ISuccessMsg.make("Got chatrooms"), ClientStub));
						return null;
					}
				};
				thisAlgo.setCmd(IAddRoomInfosMsg.GetID(), receivingChatRoomsHandling);
				
				// Handle Clients requesting other Clients
				AAppMsgCmd<IAppMsg> requestClientsHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -9026467136691311457L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host,
							Void... params) {
						//Send back IAddClientsCollectionMsg
//						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddCollectionMsg.make(connectedClientStubs), ClientStub));

						return null;
					}
				};
				thisAlgo.setCmd(IAddAppsMsg.GetID(), requestClientsHandling);
				
//				// Handle Clients receiving from other Clients
//				AClientMsgCmd<IAppMsg> receivingClientsHandling = new AClientMsgCmd<IAppMsg>() {
//
//					@Override
//					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
//						System.out.println("Processing Clients received from sender");
//						Collection<IClient> newAvailableClients = ((IAddClientsCollectionMsg) host.getData()).getCollection();
//						System.out.println(newAvailableClients);
//				        for (IApplication Client : newAvailableClients) {
//				        	availableClientStubs.add(Client);
//				        	iModel2ViewAdapter.updateAvailableClient(new UserWrapper(Client, ClientUpdateAdapter));
//				        }
//				        
//				        //TODO: return status message
//						return null;
//					}
//
//				};
//				thisAlgo.setCmd(IAddClientsCollectionMsg.GetID(), receivingClientsHandling);
//				
//				
				// Handle invite to room 
				AAppMsgCmd<IAppMsg> inviteMsgHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -9026467136691311457L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						IRoomIdentity roomReceived = ((IInviteToRoomMsg) host.getData()).getRoom();
						
						System.out.println("In Model");
						System.out.println(roomReceived.getName());
						
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAcceptInviteMsg.make(roomReceived), ClientStub));
						
						return null;
						
					}
				};
				thisAlgo.setCmd(IInviteToRoomMsg.GetID(), inviteMsgHandling);
				
				// Handle request to join a team
				AAppMsgCmd<IAppMsg> requestToJoinHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 998706354345720984L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						// Send back the most updated version of the room for Client to join
						IMainAccessMiniAdapter adpt = _roomIdToMainAccessMiniMap.get(((IRequestToJoinMsg) host.getData()).getRoomIdentity().getID());
						
						// Send a failure msg if you are not part of the room anymore
						if (adpt == null) {
							sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Client gone from requested chat room"), ClientStub));
							return null;
						}
						Room roomRequested = adpt.getChatRoom();

						Room roomToSendBack = new Room(roomRequested.getInfo().getID(), roomRequested.getInfo().getName(), roomRequested.getMe(), ClientName, ClientStub);
						for (IRoomMember currentMember : roomRequested.getMembers()) {
							roomToSendBack.addUser(currentMember);
						}
						roomToSendBack.addUser(roomRequested.getMe());
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddRoomMsg.make((IRoom) roomToSendBack), ClientStub));

//						sendMsgThread()
						
				        //TODO: return status message
						return null;
					}
				};
				thisAlgo.setCmd(IRequestToJoinMsg.GetID(), requestToJoinHandling);				
				
				// Handle add to room 
				AAppMsgCmd<IAppMsg> addToRoomMsgHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 8764959913989910418L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						System.out.println("Got added to a room");
						IRoom roomReceived = ((IAddRoomMsg) host.getData()).getRoom();
	
						IMainAccessMiniAdapter mainAccessMini = iModel2ViewAdapter.addRoom(roomReceived.getInfo().getID(), roomReceived.getInfo().getName(), ClientName, roomReceived.getMembers(), ClientStub);
						mainAccessMini.joinRoom();
						iModel2ViewAdapter.updateConnectedRooms(new TeamWrapper(mainAccessMini.getChatRoom(), host.getSender()));
						connectedRooms.add(mainAccessMini.getChatRoom());
						_roomIdToMainAccessMiniMap.put(mainAccessMini.getChatRoom().getInfo().getID(), mainAccessMini);
//				        sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(ISuccessMsg.make("Added to a chatroom"), ClientStub));

				        //TODO: return status message
						return null;
					}
				};
				thisAlgo.setCmd(IAddRoomMsg.GetID(), addToRoomMsgHandling);
				
				// Handle Client quitting
				AAppMsgCmd<IQuitMsg> quitMsgHandling = new AAppMsgCmd<IQuitMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 6192231362815737517L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IQuitMsg> host, Void... params) {
						// TODO: remove Client from the dropdown from the view
						connectedClientStubs.remove(host.getSender());
						iModel2ViewAdapter.deleteFromConnectedUsers(new UserWrapper(host.getSender(), ClientUpdateAdapter));
						return null;
					}
				};
				thisAlgo.setCmd(IQuitMsg.GetID(), quitMsgHandling);
				
				// Handle accept invite
				AAppMsgCmd<IAcceptInviteMsg> acceptInviteHandling = new AAppMsgCmd<IAcceptInviteMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -7887974910335043832L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAcceptInviteMsg> host, Void... params) {
						// TODO Auto-generated method stub
						IMainAccessMiniAdapter adpt = _roomIdToMainAccessMiniMap.get(((IAcceptInviteMsg) host.getData()).getRoomIdentity().getID());
						
						// Send a failure msg if you are not part of the room anymore
						if (adpt == null) {
							sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Client gone from requested chat room"), ClientStub));
							return null;
						}
						Room roomRequested = adpt.getChatRoom();

						Room roomToSendBack = new Room(roomRequested.getInfo().getID(), roomRequested.getInfo().getName(), roomRequested.getMe(), ClientName, ClientStub);
						for (IRoomMember currentMember : roomRequested.getMembers()) {
							roomToSendBack.addUser(currentMember);
						}
						roomToSendBack.addUser(roomRequested.getMe());
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddRoomMsg.make((IRoom) roomToSendBack), ClientStub));
						return null;
					}
					
				};
				thisAlgo.setCmd(IAcceptInviteMsg.GetID(), acceptInviteHandling);
				
				
				AAppMsgCmd<IStatusMsg> statusHandling = new AAppMsgCmd<IStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 2121151928430196717L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						
						System.out.println(host.getData().getDescription());
						return null;
					}
					
				};
				thisAlgo.setCmd(IStatusMsg.GetID(), statusHandling);
			}
			
		};
		
	    iModel2ViewAdapter.startDiscoveryPanel(); // Call out to the view to start the discovery server panel now that the rmiUtils has started.
	}
	
	/**
	 * Quit chatapp
	 */
	public void quit() {
		shutdown();
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		for (IRoom room : this.connectedRooms) {
			RoomDataPacket<ILeaveMsg> leaveMessage = new RoomDataPacket<ILeaveMsg>(ILeaveMsg.make(((Room)room).getMe()), ((Room)room).getMe());
			threadList = ((Room)room).sendChatRoomMsg(leaveMessage);
		}
		for (IApplication Client : this.connectedClientStubs) {
			sendMsgThread(Client, new AppDataPacket<IAppMsg>(IQuitMsg.make(this.ClientStub), this.ClientStub));
		}
		for (Thread thread : threadList) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		rmiUtils.stopRMI();
		System.exit(0);
	}

	
	/**
	 * Create a chat room
	 * @param roomName - name of the room to be created
	 */
	public void createTeam(String roomName) {
				
		IMainAccessMiniAdapter mainAccessMini = iModel2ViewAdapter.addRoom(null, roomName, this.ClientName, new ArrayList<IRoomMember>(), ClientStub);
		this.iModel2ViewAdapter.updateConnectedRooms(new TeamWrapper(mainAccessMini.getChatRoom(), this.Client));
		this.connectedRooms.add(mainAccessMini.getChatRoom());
		this._roomIdToMainAccessMiniMap.put(mainAccessMini.getChatRoom().getInfo().getID(), mainAccessMini);
		
		mainAccessMini.getChatRoom().startTimer();

	}
	
	/**
	 * Delete the chat room from the model
	 * @param miniAdapter - mini adapter to be deleted
	 */
	public void delete(IMainAccessMiniAdapter miniAdapter) {
		this._roomIdToMainAccessMiniMap.remove(miniAdapter.getChatRoom().getInfo().getID());

	}
	
	 /**
	  * The controller would connect the IDiscoveryPanelAdapter.connectToDiscoveryServer() call to this method.
	 * @param category - category to connect to
	 * @param watchOnly - whether to watch only
	 * @param updateFn  - an update function
	  */
	 public void connectToDiscoveryServer(String category, boolean watchOnly, Consumer<Iterable<IEndPointData>> updateFn) {
	     try {
		 this.discConn.connectToDiscoveryServer(category, watchOnly, updateFn);

	     } catch (RemoteException e) {
	        System.err.println("[ChatModel.connectToDiscoveryServer("+category+")] Exception: "+e);
	        e.printStackTrace();
	     }
	 }

	/**
	 * The controller would connect the IDiscoveryPanelAdapter.connectToEndPoint() call to this method.
	 * Be sure to substitute the API's connection-level Remote interface for "IApplicationEntity".
	 * @param endPt - an endpoint to retrieve
	 */
	public void connectToEndPoint(IEndPointData endPt) {    
	    try {
	        IApplication newConnection = remoteAPIStubFac.get(endPt);
	        this.connectedClientStubs.add(newConnection);
	        iModel2ViewAdapter.updateConnectedUser(new UserWrapper(newConnection, ClientUpdateAdapter));
	        //Auto connect back
	        sendMsgThread(newConnection, new AppDataPacket<IConnectMsg>(IConnectMsg.make(ClientStub, this.ClientName), ClientStub));
	        
	    } catch (Exception e) {
	        System.err.println("[ChatModel.connectTo("+endPt+")] Exception while retrieving stub: "+e);
	        e.printStackTrace();
	    } 
	 }
	
	/**
	 * Get chat rooms
	 * @param user - Client to get chat rooms from
	 */
	public void getChatRooms(IApplication user) {

		try {
			System.out.println("sent request chatrooms to" + user.getName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMsgThread(user, new AppDataPacket<IRequestRoomsMsg>(IRequestRoomsMsg.make(), ClientStub));
	}
	
//	/**
//	 * Get Clients 
//	 * @param connection - an Client to get other Clients from
//	 */
//	public void getClientsFromClient(IApplication connection) {
//		sendMsgThread(connection, new AppDataPacket<IRequestClientsCollectionMsg>(IRequestClientsCollectionMsg.make(), ClientStub));
//	}

	 /**
	  * Shutting down the system
	  */
	 private void shutdown() {
	   if(null != this.discConn) {  // just in case one was never made
	       this.discConn.disconnect();  // tells the main discovery server to remove the associated endpoint.
	   }

	   // Do rest of shutdown processes, e.g. leave rooms, disconnect from connection-level stubs, etc.
	   // Shutdown the rmiUtils LAST.
	 }
	 
	 
	 /**
	  * Join a room
	  * @param chatRoom - a room to join
	  */
	public void joinRoom(TeamIdentityWrapper chatRoom) {
		//TODO: requesting the most updated chatroom
		try {
			System.out.println("Trying to join room created by "+ chatRoom.getCreator().getName());
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		sendMsgThread(chatRoom.getCreator(), new AppDataPacket<IAppMsg>(IRequestToJoinMsg.make(chatRoom.getRoomIdentity()), ClientStub));
	}
	
	/**
	 * Delete a chat room from the list of connected rooms
	 * @param chatroom - a chat room to delete
	 */
	public void deleteFromChatRoom(Room chatroom) {
		this.connectedRooms.remove(chatroom);
		iModel2ViewAdapter.deleteFromConnectedChatRoom(new TeamWrapper(chatroom, null));
	}

	/**
	 * Invite an Client to a room
	 * @param Client2 - Client to invite
	 * @param chatRoom - chat room
	 */
	public void inviteToRoom(IApplication Client2, Room chatRoom) {

		Room roomToInvite = new Room(chatRoom.getInfo().getID(), chatRoom.getInfo().getName(), chatRoom.getMe(), ClientName, ClientStub);
		for (IRoomMember currentMember : chatRoom.getMembers()) {
			roomToInvite.addUser(currentMember);;
		}
		roomToInvite.addUser (chatRoom.getMe());
		sendMsgThread(Client2, new AppDataPacket<IAppMsg>(IInviteToRoomMsg.make((IRoomIdentity) roomToInvite.getInfo()), this.ClientStub));

	}

	/**
	 * Connect to an Client
	 * @param user - an Client to connect to
	 */
	public void connectToClient(IApplication user) {
		connectedClientStubs.add(user);
        iModel2ViewAdapter.updateConnectedUser(new UserWrapper(user, ClientUpdateAdapter));
        //Auto connect back
        sendMsgThread(user, new AppDataPacket<IConnectMsg>(IConnectMsg.make(ClientStub, this.ClientName), ClientStub));
	}
	
	/**
	 * Get the list of connected Clients
	 * @return - a list of IClient
	 */
	public ArrayList<IApplication> getConnectedClients() {
		return this.connectedClientStubs;
	}
	
	/**
	 * Get the Client update adapter
	 * @return - an Client update adapter
	 */
	public IUserUpdateAdapter getClientUpdateAdapter() {
		return this.ClientUpdateAdapter;
	}
	
	/**
	 * Get this Client
	 * @return - an IClient
	 */
	public IApplication getClient() {
		return this.Client;
	}

	/**
	 * Get the list of connected chatrooms
	 * @return - a list of chat rooms
	 */
	public ArrayList<Room> getConnectedChatRooms() {
		// TODO Auto-generated method stub
		return this.connectedRooms;
	}



}
