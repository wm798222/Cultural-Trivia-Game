package yx48_zh16.server.serverMVC.serverModel;

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
import common.identity.IIdentity;
import common.identity.IRoomIdentity;
import common.msg.IAppMsg;
import common.msg.IRoomMsg;
import common.msg.application.IAcceptInviteMsg;
import common.msg.application.IAddRoomMsg;
import common.msg.application.IConnectMsg;
import common.msg.application.IInviteToRoomMsg;
import common.msg.application.IQuitMsg;
import common.msg.application.IRequestToJoinMsg;
import common.msg.status.IFailureMsg;
import common.msg.status.IStatusMsg;
import common.packet.AppDataPacket;
import common.packet.RoomDataPacket;
import common.receivers.IApplication;
import common.receivers.IRoomMember;
import common.virtualNetwork.IRoom;
import common.msg.collection.IAddAppsMsg;
import common.msg.collection.IAddRoomInfosMsg;
import common.msg.collection.IRequestRoomsMsg;
import common.msg.room.ILeaveMsg;
import provided.datapacket.DataPacketAlgo;
import provided.datapacket.IDataPacketID;
import provided.discovery.IEndPointData;
import provided.discovery.impl.model.DiscoveryConnector;
import provided.discovery.impl.model.RemoteAPIStubFactory;
import provided.mixedData.IMixedDataDictionary;
import provided.mixedData.MixedDataDictionary;
import provided.mixedData.MixedDataKey;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIUtils;
import yx48_zh16.server.serverMVC.serverModel.Room;
import yx48_zh16.server.serverMiniMVC.IMainAccessMiniAdapter;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.ILoseMsg;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.IStartGameMsg;
import yx48_zh16.realgame.model.IGameAdapter;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.IWinMsg;

/**
 * ChatApp model definitions.
 *
 */
public class ServerModel {
	
	/**
	 * Chat app Server
	 */
	private IApplication Server;
	
	/**
	 * Stub for the Server
	 */
	private IApplication ServerStub;
	
	/**
	 * Server name
	 */
	private String ServerName;
	

	/**
	 * App level users in team A
	 */
	private IApplication applicationMe;
	

	/**
	 * collection of app level connection of team A
	 */
	private Collection<IApplication> teamA;
	
	
	/**
	 * collection of app level connection of team B
	 */
	private Collection<IApplication> teamB;
	
	/**
	 * team room A
	 */
	private IRoom roomA;
	
	/**
	 * team room B
	 */
	private IRoom roomB;
	
	/**
	 * UUID for server
	 */
	private final UUID uuid = UUID.randomUUID();
	
	/**
	 * Map from room member to UUID
	 */
	private HashMap<IRoomMember, UUID> playerUUID;  
	
	/**
	 * Map from room member to string
	 */
	private HashMap<IRoomMember, String> playerTeam;
	
	/**
	 * Default command for the message processing algo
	 */
	private AAppMsgCmd<IAppMsg> defautlCmd = new AAppMsgCmd<IAppMsg>() {

		/**
		 * Serial Version UID
		 */
		private static final long serialVersionUID = 5040228491374010183L;

		@Override
		public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
			// If it gets here, other Servers have sent something strange so send back a failure msg
			sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Wrong msg type!"), ServerStub));
			return null;
		}
		
	};
	
	/**
	 * Message processing visitor
	 */
	private DataPacketAlgo<Void, Void> ServerMessageProcessingAlgo;
	
	/**
	 * Available rooms
	 */
	private ArrayList<IRoomIdentity> availRooms = new ArrayList<IRoomIdentity>();
	
	/**
	 * Connected rooms
	 */
	private ArrayList<Room> connectedRooms = new ArrayList<Room>();
	
	/**
	 * Adapter for the Server to update
	 */
	private IUserUpdateAdapter ServerUpdateAdapter;
		
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
	 * Storing connected Servers
	 */
	ArrayList<IApplication> connectedStubs = new ArrayList<IApplication>();
	
	/**
	 * Stubs of available Servers
	 */
	ArrayList<IApplication> availableServerStubs = new ArrayList<IApplication>();
	
	/**
	 * Discovery conncetor
	 */
	private DiscoveryConnector discConn;
	
	/**
	 * Remote API Stub Factory
	 */
	private RemoteAPIStubFactory<IApplication> remoteAPIStubFac;  // Substitute the API's connection-level Remote interface for "IConnectionEntity"
	
	/**
	 * Adapter for the main model to access the methods on the mini model
	 */
	private HashMap<UUID, IMainAccessMiniAdapter> _roomIdToMainAccessMiniMap = new HashMap<UUID, IMainAccessMiniAdapter>();
	
	
	/**
	 * server Database
	 */
	private IMixedDataDictionary serverDB = new MixedDataDictionary();
	
	/**
	 * Class constructor
	 * @param iModel2ViewAdapter - a model to view adapter
	 * @param ServerUpdateAdapter - Server adapter
	 */
	public ServerModel(IModel2ViewAdapter iModel2ViewAdapter, IUserUpdateAdapter ServerUpdateAdapter) {
		this.iModel2ViewAdapter = iModel2ViewAdapter;
		this.ServerUpdateAdapter = ServerUpdateAdapter;
		
		this.playerTeam = new HashMap<IRoomMember, String>();
		
		this.playerUUID = new HashMap<IRoomMember, UUID>();
	}
	
	/**
	 * Start the model
	 * @param Servername - Server name 
	 */
	public void start(String Servername) {
		this.ServerName = Servername;
		startRMI();
	}
	
//	/**
//	 * Send a message to an Server asynchronously
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
	 * Send a message to an Server asynchronously
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
		rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT_SERVER);
		
//	    // instantiate the two utility classes now that the RMIUtils has been started:
	    try {
			this.discConn = new DiscoveryConnector(rmiUtils, this.ServerName, "Server");

		} catch (RemoteException e1) {
			e1.printStackTrace();
		} // getServerName() and getBoundName() are accessors for that info in the model
	    this.remoteAPIStubFac = new RemoteAPIStubFactory<IApplication>(rmiUtils); // Instantiate the API-specific factory, replacing the "IConnectionEntity" with the proper API-defined Remote interface
//	    
//	    // Do anything else that needs to be done to initialize the system...
//	    // The connection-level stub should be created and bound into the local Registry at this point, before the discovery panel is started.
	    Server = new IApplication() {

			@Override
			public void receiveMsg(AppDataPacket<? extends IAppMsg> msg) throws RemoteException {
				// TODO Auto-generated method stub
				System.out.println(msg.getData().getID());
				Thread msgThread = new Thread() {
					public void run() {
						msg.execute(ServerMessageProcessingAlgo);
					}
				};
				msgThread.start();
			}

			@Override
			public String getName() throws RemoteException {
				// TODO Auto-generated method stub
				return ServerName;
			}    	
		};
		
		localRegistry = rmiUtils.getLocalRegistry();
		try {
			ServerStub = (IApplication) UnicastRemoteObject.exportObject(Server, IRMI_Defs.STUB_PORT_SERVER);
			localRegistry.bind("Server", ServerStub);
		} catch (RemoteException | AlreadyBoundException e) {
			e.printStackTrace();
		}
		
		ServerMessageProcessingAlgo = new DataPacketAlgo<Void, Void>(defautlCmd) {
			/**
			 * Serial Version UID
			 */
			private static final long serialVersionUID = 5655362907070481693L;
			
			DataPacketAlgo<Void, Void> thisAlgo = this;
			{
				AAppMsgCmd<IAppMsg> connectMsgHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -7803491214150239809L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						IApplication receivedUser = (IApplication) host.getSender();
			        	connectedStubs.add(receivedUser);
			        	iModel2ViewAdapter.updateConnectedUser(new UserWrapper(receivedUser, ServerUpdateAdapter));
				        //TODO: return status message
						return null;
					}
				};
				thisAlgo.setCmd(IConnectMsg.GetID(), connectMsgHandling);
								
				// Handle Success Msg
				AAppMsgCmd<IAppMsg> failureMsgHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 8200894285641341191L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						// TODO Auto-generated method stub
//						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Wrong msg type!"), ServerStub));
						return null;
					}
					
				};
				thisAlgo.setCmd(IFailureMsg.GetID(), failureMsgHandling);
				
				
				// Handle chat rooms requesting from the other Servers
				AAppMsgCmd<IAppMsg> requestChatRoomsHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 7953530063973563458L;


					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host,
							Void... params) {
						//Send back IAddChatRoomsCollectionMsg
						ArrayList<IRoomIdentity> returnedRooms = new ArrayList<IRoomIdentity>();
						for (Room room : connectedRooms) {
							Room newRoom = new Room(room.getInfo().getID(), room.getInfo().getName(), room.getMe(), ServerName, applicationMe);
							for (IRoomMember currentMember : room.getMembers()) {
								newRoom.addUser(currentMember);
							}
							newRoom.addUser(room.getMe());
							returnedRooms.add((IRoomIdentity) newRoom.getInfo());
						}
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddRoomInfosMsg.make(returnedRooms), ServerStub));

						//TODO: return status message
						return null;
					}
				};
				thisAlgo.setCmd(IRequestRoomsMsg.GetID(), requestChatRoomsHandling);
				 
				// Handle chat rooms receving from other Servers
				AAppMsgCmd<IAppMsg> receivingChatRoomsHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 3378133120737688455L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						System.out.println("Processing chatrooms received from sender");
						Collection<IRoomIdentity> newAvailableRooms = ((IAddRoomInfosMsg) host.getData()).getCollection();
				        for (IRoomIdentity room : newAvailableRooms) {
				        	availRooms.add(room);
				        	iModel2ViewAdapter.updateAvailRooms(new TeamIdentityWrapper(room, host.getSender()));
				        }
//				        sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(ISuccessMsg.make("Got chatrooms"), ServerStub));
				        //TODO: return status message
						return null;
					}
				};
				thisAlgo.setCmd(IAddRoomInfosMsg.GetID(), receivingChatRoomsHandling);
				
				// Handle Servers requesting other Servers
				AAppMsgCmd<IAppMsg> requestAppsHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = -8161837691392405634L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host,
							Void... params) {
						//Send back IAddServersCollectionMsg
//						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddAppsMsg.make(connectedStubs), ServerStub));

						return null;
					}
				};
				
				thisAlgo.setCmd(IAddAppsMsg.GetID(), requestAppsHandling);
				
//				// Handle Servers receiving from other Servers
//				AAppMsgCmd<IAppMsg> receivingServersHandling = new AppMsgCmd<IAppMsg>() {
//
//					@Override
//					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
//						System.out.println("Processing Servers received from sender");
//						Collection<IApplication> newAvailableServers = ((IAddServersCollectionMsg) host.getData()).getCollection();
//						System.out.println(newAvailableServers);
//				        for (IApplication Server : newAvailableServers) {
//				        	availableServerStubs.add(Server);
//				        	iModel2ViewAdapter.updateAvailableServer(new UserWrapper(Server, ServerUpdateAdapter));
//				        }
//				        
//				        //TODO: return status message
//						return null;
//					}
//
//				};
//				thisAlgo.setCmd(IAddCollectionsMsg.GetID(), receivingServersHandling);
//				
//				
				// Handle invite to room 
				AAppMsgCmd<IAppMsg> inviteMsgHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -8161837691392405634L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						IRoomIdentity roomReceived = ((IInviteToRoomMsg) host.getData()).getRoom();
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAcceptInviteMsg.make(roomReceived), ServerStub));
						return null;
						
//						IMainAccessMiniAdapter mainAccessMini = iModel2ViewAdapter.addRoom(roomReceived.getId(), roomReceived.getName(), ServerName, roomReceived.getChatServers());
//						mainAccessMini.joinRoom();
//						iModel2ViewAdapter.updateConnectedRooms(new TeamWrapper(mainAccessMini.getChatRoom(), host.getSender()));
//						connectedRooms.add(mainAccessMini.getChatRoom());
//						_roomIdToMainAccessMiniMap.put(mainAccessMini.getChatRoom().getId(), mainAccessMini);						
//				        sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(ISuccessMsg.make("Accept invitation"), ServerStub));
//				        //TODO: return status message
//						return null;
					}
				};
				thisAlgo.setCmd(IInviteToRoomMsg.GetID(), inviteMsgHandling);
				
				// Handle request to join a team
				AAppMsgCmd<IAppMsg> requestToJoinHandling = new AAppMsgCmd<IAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 998706354345720984L;

					@SuppressWarnings("unused")
					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAppMsg> host, Void... params) {
						// Send back the most updated version of the room for Client to join
						IMainAccessMiniAdapter adpt = _roomIdToMainAccessMiniMap.get(((IRequestToJoinMsg) host.getData()).getRoomIdentity().getID());
						System.out.println("Server processing Request to join from");
						System.out.println(adpt.getChatRoom().getInfo().getName());
						
						// Send a failure msg if you are not part of the room anymore
						if (adpt == null) {
							sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Client gone from requested chat room"), ServerStub));
							return null;
						}
						Room roomRequested = adpt.getChatRoom();

						Room roomToSendBack = new Room(roomRequested.getInfo().getID(), roomRequested.getInfo().getName(), roomRequested.getMe(), ServerName, applicationMe);
						for (IRoomMember currentMember : roomRequested.getMembers()) {
							roomToSendBack.addUser(currentMember);
						}
						roomToSendBack.addUser(roomRequested.getMe());
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddRoomMsg.make((IRoom) roomToSendBack), ServerStub));
						
				        //TODO: return status message
						return null;
					}
				};
				thisAlgo.setCmd(IRequestToJoinMsg.GetID(), requestToJoinHandling);
//				
//				
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
											
						IMainAccessMiniAdapter mainAccessMini = iModel2ViewAdapter.addRoom(roomReceived.getInfo().getID(), roomReceived.getInfo().getName(), ServerName, roomReceived.getMembers(), ServerStub);
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
//				
				// Handle Server quitting
				AAppMsgCmd<IQuitMsg> quitMsgHandling = new AAppMsgCmd<IQuitMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -4152367258131289407L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IQuitMsg> host, Void... params) {
						// TODO: remove Server from the dropdown from the view
						connectedStubs.remove(host.getSender());
						iModel2ViewAdapter.deleteFromConnectedUsers(new UserWrapper(host.getSender(), ServerUpdateAdapter));
						return null;
					}
				};
				thisAlgo.setCmd(IQuitMsg.GetID(), quitMsgHandling);
				
				
				// Handle accept invite
				AAppMsgCmd<IAcceptInviteMsg> acceptInviteHandling = new AAppMsgCmd<IAcceptInviteMsg>() {
					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -4519400905984844131L;

					@Override
					public Void apply(IDataPacketID index, AppDataPacket<IAcceptInviteMsg> host, Void... params) {
						// TODO Auto-generated method stub
						IMainAccessMiniAdapter adpt = _roomIdToMainAccessMiniMap.get(((IAcceptInviteMsg) host.getData()).getRoomIdentity().getID());
						
						// Send a failure msg if you are not part of the room anymore
						if (adpt == null) {
							sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IFailureMsg.make("Client gone from requested chat room"), ServerStub));
							return null;
						}
						Room roomRequested = adpt.getChatRoom();

						
						Room roomToSendBack = new Room(roomRequested.getInfo().getID(), roomRequested.getInfo().getName(), roomRequested.getMe(), ServerName, applicationMe);
						for (IRoomMember currentMember : roomRequested.getMembers()) {
							roomToSendBack.addUser(currentMember);
						}
						roomToSendBack.addUser(roomRequested.getMe());
						System.out.println("Server AcceptInvite handling from");
						try {
							System.out.println(roomRequested.getMe().getName());
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						sendMsgThread(host.getSender(), new AppDataPacket<IAppMsg>(IAddRoomMsg.make((IRoom) roomToSendBack), ServerStub));
						return null;
					}
					
				};
				thisAlgo.setCmd(IAcceptInviteMsg.GetID(), acceptInviteHandling);
				
				
				AAppMsgCmd<IStatusMsg> statusHandling = new AAppMsgCmd<IStatusMsg>() {

					/**
					 * serial ID
					 */
					private static final long serialVersionUID = 5147264133922018051L;

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
		for (IApplication Client : this.connectedStubs) {
			sendMsgThread(Client, new AppDataPacket<IAppMsg>(IQuitMsg.make(this.ServerStub), this.ServerStub));
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
				
		IMainAccessMiniAdapter mainAccessMini = iModel2ViewAdapter.addRoom(null, roomName, this.ServerName, new ArrayList<IRoomMember>(), this.ServerStub);
		this.iModel2ViewAdapter.updateConnectedRooms(new TeamWrapper(mainAccessMini.getChatRoom(), this.Server));
		this.connectedRooms.add(mainAccessMini.getChatRoom());
		this._roomIdToMainAccessMiniMap.put(mainAccessMini.getChatRoom().getInfo().getID(), mainAccessMini);
		System.out.println(_roomIdToMainAccessMiniMap.size());

	}
	
	/**
	 * create team and return teams
	 * @param roomName - name of the room
	 * @return - room created
	 */
	public IRoom createAndReturnTeam(String roomName) {
		
		IMainAccessMiniAdapter mainAccessMini = iModel2ViewAdapter.addRoom(UUID.randomUUID(), roomName, this.ServerName, new ArrayList<IRoomMember>(), this.ServerStub);
		this.iModel2ViewAdapter.updateConnectedRooms(new TeamWrapper(mainAccessMini.getChatRoom(), this.Server));
		this.connectedRooms.add(mainAccessMini.getChatRoom());
		this._roomIdToMainAccessMiniMap.put(mainAccessMini.getChatRoom().getInfo().getID(), mainAccessMini);
		System.out.println(_roomIdToMainAccessMiniMap.size());
		
		return mainAccessMini.getChatRoom();
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
	 * Be sure to substitute the API's connection-level Remote interface for "IConnectionEntity".
	 * @param endPt - an endpoint to retrieve
	 */
	public void connectToEndPoint(IEndPointData endPt) {    
	    try {
	        IApplication newConnection = remoteAPIStubFac.get(endPt);
	        this.connectedStubs.add(newConnection);
	        iModel2ViewAdapter.updateConnectedUser(new UserWrapper(newConnection, ServerUpdateAdapter));
	        //Auto connect back
	        sendMsgThread(newConnection, new AppDataPacket<IConnectMsg>(IConnectMsg.make(ServerStub, this.ServerName), ServerStub));
	        
	    } catch (Exception e) {
	        System.err.println("[ChatModel.connectTo("+endPt+")] Exception while retrieving stub: "+e);
	        e.printStackTrace();
	    } 
	 }
	
	/**
	 * Get chat rooms
	 * @param user - Server to get chat rooms from
	 */
	public void getChatRooms(IApplication user) {

		try {
			System.out.println("sent request chatrooms to" + user.getName());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendMsgThread(user, new AppDataPacket<IRequestRoomsMsg>(IRequestRoomsMsg.make(), ServerStub));
	}
	
//	/**
//	 * Get Servers 
//	 * @param connection - an Server to get other Servers from
//	 */
//	public void getServersFromServer(IApplication connection) {
//		sendMsgThread(connection, new AppDataPacket<IRequestServersCollectionMsg>(IRequestServersCollectionMsg.make(), ServerStub));
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
		sendMsgThread(chatRoom.getCreator(), new AppDataPacket<IAppMsg>(IRequestToJoinMsg.make(chatRoom.getRoomIdentity()), ServerStub));
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
	 * Invite an Server to a room
	 * @param Server2 - Server to invite
	 * @param chatRoom - chat room
	 */
	public void inviteToRoom(IApplication Server2, Room chatRoom) {
		
		System.out.println("IN server model");
		System.out.println(chatRoom.getInfo().getName());

		Room roomToInvite = new Room(chatRoom.getInfo().getID(), chatRoom.getInfo().getName(), chatRoom.getMe(), ServerName, applicationMe);
		for (IRoomMember currentMember : chatRoom.getMembers()) {
			roomToInvite.addUser(currentMember);;
		}
		roomToInvite.addUser (chatRoom.getMe());
		sendMsgThread(Server2, new AppDataPacket<IAppMsg>(IInviteToRoomMsg.make((IRoomIdentity) roomToInvite.getInfo()), this.ServerStub));

	}

	/**
	 * Connect to an Server
	 * @param user - an Server to connect to
	 */
	public void connectToServer(IApplication user) {
		connectedStubs.add(user);
        iModel2ViewAdapter.updateConnectedUser(new UserWrapper(user, ServerUpdateAdapter));
        //Auto connect back
        sendMsgThread(user, new AppDataPacket<IConnectMsg>(IConnectMsg.make(ServerStub, this.ServerName), ServerStub));
	}
	
	/**
	 * Get the list of connected Servers
	 * @return - a list of IServer
	 */
	public ArrayList<IApplication> getConnectedServers() {
		return this.connectedStubs;
	}
	
	/**
	 * Get the Server update adapter
	 * @return - an Server update adapter
	 */
	public IUserUpdateAdapter getServerUpdateAdapter() {
		return this.ServerUpdateAdapter;
	}
	
	/**
	 * Get this Server
	 * @return - an IServer
	 */
	public IApplication getServer() {
		return this.Server;
	}

	/**
	 * Get the list of connected chatrooms
	 * @return - a list of chat rooms
	 */
	public ArrayList<Room> getConnectedChatRooms() {
		// TODO Auto-generated method stub
		return this.connectedRooms;
	}
	
	
	/**
	 * Method to send game message
	 */
	public void sendGameMsg() {
		
		Collection<IApplication> members = this.connectedStubs;
		
		if (members.size() < 1) {
//			this._miniModelUpdateAdater.displayNotEnoughPeopleWindow();
		}
		else {
			
			ArrayList<IApplication> arrayMembers = new ArrayList<IApplication>();
			for (IApplication i : members) {
				arrayMembers.add(i);
			}
			
			this.teamA = new ArrayList<IApplication>();
			this.teamB = new ArrayList<IApplication>();
			
			for (int i = 0; i < arrayMembers.size() / 2; i++) {
				teamA.add(arrayMembers.get(i));
			}
			for (int i = arrayMembers.size() / 2; i < arrayMembers.size(); i++) {
				teamB.add(arrayMembers.get(i));
			}			
			
			// create a room for team A B and invite all of them
			this.roomA = this.createAndReturnTeam("Team A");
			this.roomB = this.createAndReturnTeam("Team B");
			
			// Team AAAAA
			for (int i = 0; i < arrayMembers.size() / 2; i++) {
				// invite the person to room A first
				try {
					arrayMembers.get(i).receiveMsg(new AppDataPacket<IAppMsg>(IInviteToRoomMsg.make(roomA.getInfo()), this.ServerStub));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
				    System.out.println("Exception when inivting people to teams in the server model.");
					e.printStackTrace();
				}
	
			}		
			
			// Team BBBBB		
			for (int i = arrayMembers.size() / 2; i < arrayMembers.size(); i++) {
				
				// invite the person to room A first
				try {
					arrayMembers.get(i).receiveMsg(new AppDataPacket<IAppMsg>(IInviteToRoomMsg.make(roomB.getInfo()), this.ServerStub));
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					System.out.println("Exception when inivting people to teams in the server model.");
					e.printStackTrace();
				}
				
			}	
		}
		

	}
	
	
	/**
	 * Method to start Game
	 */
	@SuppressWarnings("rawtypes")
	public void startGame() {
		
		//generate mdd key
		MixedDataKey gameAdapterKey = this.generateMddKey("Start Game Adapter", IGameAdapter.class);
		
		for (IRoomMember i : roomA.getMembers()) {
			
			UUID thisUUID = UUID.randomUUID();
			this.playerUUID.put(i, thisUUID);
			
			this.playerTeam.put(i, "A");
			
			IIdentity identity = new Identity();
			
			VirtualNetworkApplications teammates = new VirtualNetworkApplications(teamA, identity);
			VirtualNetworkApplications allPlayers = new VirtualNetworkApplications(this.connectedStubs, identity);
			
			IStartGameMsg gameMsg = IStartGameMsg.make(teammates, allPlayers, i, gameAdapterKey, thisUUID, ((Room)roomA).getMe());	
			RoomDataPacket<IRoomMsg> message = new RoomDataPacket<IRoomMsg>(gameMsg, ((Room)roomA).getMe());	
			try {
				i.receiveMsg(message);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				System.out.println("Exception when creating and starting games in the server model.");
				e.printStackTrace();
			}
		}	
		
		for (IRoomMember i : roomB.getMembers()) {
			
			UUID thisUUID = UUID.randomUUID();
			this.playerUUID.put(i, thisUUID);
			
			this.playerTeam.put(i, "B");
			
			IIdentity identity = new Identity();
			
			VirtualNetworkApplications teammates = new VirtualNetworkApplications(teamB, identity);
			VirtualNetworkApplications allPlayers = new VirtualNetworkApplications(this.connectedStubs, identity);
			
			IStartGameMsg gameMsg = IStartGameMsg.make(teammates, allPlayers, i, gameAdapterKey, thisUUID, ((Room)roomA).getMe());	
			RoomDataPacket<IRoomMsg> message = new RoomDataPacket<IRoomMsg>(gameMsg, ((Room)roomB).getMe());	
			try {
				i.receiveMsg(message);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				System.out.println("Exception when creating and starting games in the server model.");
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Method to generate MDD key
	 * @param name - name for key
	 * @param type - class of the item
	 * @return mdd key
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MixedDataKey<?> generateMddKey(String name, Class<?> type) {
		return new MixedDataKey(this.uuid, name, type);
	}

	/**
	 * Method to end game
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void endGame() {
		System.out.println("enter END GAME");
		// get team A and B list		
		int team_A_Score = 0;
		int team_B_Score = 0;
		
		for (IRoomMember i : roomA.getMembers()) {
			MixedDataKey key = new MixedDataKey(this.playerUUID.get(i), "SubmitScoreCmd", Integer.class);

			if (!((key).equals(null))) { 
				if (!(serverDB.get(key)== null)) {
					// j should be = playerIndex
					team_A_Score += (int)serverDB.get(key);
					} 		
				} else {
					team_A_Score += 0;
			}
		}
		
		System.out.println("1st for loop");
		
		for (IRoomMember i : roomB.getMembers()) {
			MixedDataKey key = new MixedDataKey(this.playerUUID.get(i), "SubmitScoreCmd", Integer.class);
			
			System.out.println("keyBBB: " + key);
			System.out.println("(BBBBserverDB.get(key).equals(null)): " + (serverDB.get(key) == null));
			if (!((key).equals(null))) { 
				if (!(serverDB.get(key)== null)) {
					// j should be = playerIndex
					team_B_Score += (int)serverDB.get(key);
					} 		
				} else {
					team_B_Score += 0;
			}
		}
		
		System.out.println("team A score "+ team_A_Score);
		System.out.println("team B score "+ team_B_Score);
		
		System.out.println("team A member size: "+ roomA.getMembers().size());
		System.out.println("team B member size: "+ roomB.getMembers().size());
		
		if (team_A_Score > team_B_Score) {
			System.out.println("case1 ");
			IWinMsg winMsg = IWinMsg.make();	
			RoomDataPacket<IRoomMsg> message = new RoomDataPacket<IRoomMsg>(winMsg, ((Room)roomA).getMe());
			((Room)this.roomA).sendMsg(message);
			
			ILoseMsg lostMsg = ILoseMsg.make();	
			RoomDataPacket<IRoomMsg> message2 = new RoomDataPacket<IRoomMsg>(lostMsg, ((Room)roomB).getMe());
			((Room)this.roomB).sendMsg(message2);

		}
		else if (team_A_Score < team_B_Score) {
			System.out.println("case2 ");
			IWinMsg winMsg = IWinMsg.make();	
			RoomDataPacket<IRoomMsg> message = new RoomDataPacket<IRoomMsg>(winMsg, ((Room)roomB).getMe());
			((Room)this.roomB).sendMsg(message);
			// send room B win Message and room A lose Message
			ILoseMsg lostMsg = ILoseMsg.make();	
			RoomDataPacket<IRoomMsg> message2 = new RoomDataPacket<IRoomMsg>(lostMsg, ((Room)roomA).getMe());
			((Room)this.roomA).sendMsg(message2);

		} else {
			System.out.println("case3 ");
			// send both room win Message
			IWinMsg winMsg = IWinMsg.make();	
			RoomDataPacket<IRoomMsg> messageA = new RoomDataPacket<IRoomMsg>(winMsg, ((Room)roomA).getMe());
			((Room)this.roomA).sendMsg(messageA);	
			RoomDataPacket<IRoomMsg> messageB = new RoomDataPacket<IRoomMsg>(winMsg, ((Room)roomB).getMe());
			((Room)this.roomB).sendMsg(messageB);
		}
			
	}

	/**
	 * Put item in DB
	 * @param <T> - Type 
	 * @param key - key for this item
	 * @param value - value of this item
	 */
	public <T> void putInDB(MixedDataKey<T> key, T value) {
		// TODO Auto-generated method stub
		this.serverDB.put(key, value);
	}

	/**
	 * get item from DB
	 * @param <T> - Type
	 * @param key - key for this item
	 * @return T type
	 */
	public <T> T getFromDB(MixedDataKey<T> key) {
		// TODO Auto-generated method stub
		return this.serverDB.get(key);
	}
		

}
