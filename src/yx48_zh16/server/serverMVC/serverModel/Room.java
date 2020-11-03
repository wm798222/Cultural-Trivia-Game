package yx48_zh16.server.serverMVC.serverModel;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import common.cmd.ARoomMsgCmd;
import common.identity.IRoomIdentity;
import common.msg.IRoomMsg;
import common.msg.IStringMsg;
import common.msg.room.IAddCommandMsg;
import common.msg.room.ILeaveMsg;
import common.msg.room.IProvideAppMsg;
import common.msg.room.IRequestAppMsg;
import common.msg.room.IAddMemberMsg;
import common.msg.room.IRequestCommandMsg;
import common.msg.room.ISyncRoomMsg;
import common.msg.status.IFailureMsg;
import common.msg.status.IStatusMsg;
import common.msg.status.network.IRemoteExceptionMsg;
import common.msg.status.network.IRoomCheckStatusMsg;
import common.msg.status.network.IRoomOkStatusMsg;
import common.msg.status.network.IServerCheckStatusMsg;
import common.msg.status.network.IServerOkStatusMsg;
import common.packet.RoomDataPacket;
import common.receivers.IApplication;
import common.receivers.IRoomMember;
import common.virtualNetwork.IRoom;
import provided.datapacket.DataPacketAlgo;
import provided.datapacket.IDataPacketID;
import yx48_zh16.realgame.model.ISubmitScoreMsg;
import yx48_zh16.realgame.model.SubmitScoreMsgCmd;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.IMapMessage;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.ITeamToMiniModelAdapter;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.MapMsgCmd;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.ILoseMsg;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.IStartGameMsg;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.IWinMsg;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.LoseMsgCmd;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.StartGameMsgCmd;
import yx48_zh16.server.serverMiniMVC.serverMiniModel.msg.WinMsgCmd;

/**
 * An implementation of IChatRoom.
 *
 */
public class Room implements IRoom {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 3100632532042638212L;

	/**
	 * The chat user represents this person
	 */
	private IRoomMember myUserStub;
	
	/**
	 * Stubs of other users in the room
	 */
	private ArrayList<IRoomMember> memberStubs = new ArrayList<IRoomMember>();

	/** 
	 * Default command
	 */
	private ARoomMsgCmd<IRoomMsg> defautlCmd;
	
	/**
	 * Message processing algo
	 */
	private DataPacketAlgo<Void, Void> messageProcessingAlgo;
	
	/**
	 * name of the user
	 */
	@SuppressWarnings("unused")
	private String playerName;
	
	/**
	 * identity of this room
	 */
	private IRoomIdentity roomIdentity;
	
	
	/**
	 * Caching unknown messages
	 */
	private ArrayList<RoomDataPacket<IRoomMsg>> unknownMsgs = new ArrayList<RoomDataPacket<IRoomMsg>>();
	
	/**
	 * Adapter to get the cmd to model adapter
	 */
	transient ITeamToMiniModelAdapter adapter;
	
	/**
	 * Class constructor
	 * @param teamID - room ID
	 * @param teamName - room name
	 * @param myUserStub - this user in this chat room
	 * @param playerName - user name
	 * @param applicationMe my app level connection
	 */
	@SuppressWarnings("unused")
	public Room(UUID teamID, String teamName, IRoomMember myUserStub, String playerName, IApplication applicationMe) {
		this.playerName = playerName;
		if (teamID != null) {
		}
		this.roomIdentity = new IRoomIdentity() {

			/**
			 * Serial Version UID
			 */
			private static final long serialVersionUID = -2264085586733999947L;

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return teamName;
			}

			@Override
			public UUID getID() {
				// TODO Auto-generated method stub
				return teamID;
			}
			
		};
		
		this.myUserStub = myUserStub;
		
		this.defautlCmd = new ARoomMsgCmd<IRoomMsg>() {

			/**
			 * Serial Version UID
			 */
			private static final long serialVersionUID = 582880661103298045L;

			@Override
			public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
				// TODO Auto-generated method stub
				//Cache the unknown message
				unknownMsgs.add(host);
				
				//TODO: handle returned result, status message?
				try {
					((IRoomMember) host.getSender()).receiveMsg(new RoomDataPacket<IRequestCommandMsg>(IRequestCommandMsg.make(host.getData().getID()), myUserStub));
					System.out.println("This is client send request: " + new RoomDataPacket<IRequestCommandMsg>(IRequestCommandMsg.make(host.getData().getID()), myUserStub));
				} catch (RemoteException e) {
					e.printStackTrace();
					System.out.println("Fucking catch");
				}
				return null;
			}
			
		};
		messageProcessingAlgo = new DataPacketAlgo<Void, Void>(this.defautlCmd){

			/**
			 * 
			 */
			private static final long serialVersionUID = -914816364690355000L;
			DataPacketAlgo<Void, Void> mpa = this;
			{
//				ARoomMsgCmd<IRoomMsg> connectionMsgCmd = new ARoomMsgCmd<IRoomMsg>() {
//
//					/**
//					 * 
//					 */
//					private static final long serialVersionUID = -4628140230759737235L;
//
//					@Override
//					public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
//						// TODO Auto-generated method stub
//						return null;
//					}
//					
//				};
//				mpa.setCmd(IMsg.GetID(), connectionMsgCmd);
				
				
				// Failure Message Command
				ARoomMsgCmd<IRoomMsg> failureMsgCmd = new ARoomMsgCmd<IRoomMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 7762576470371978595L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
						System.out.println("Message failed!");
						return null;
					}
					
				};
				mpa.setCmd(IFailureMsg.GetID(), failureMsgCmd);
				
				//Join Message Command
				ARoomMsgCmd<IRoomMsg> addMemberMsgCmd = new ARoomMsgCmd<IRoomMsg>() {


					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -4023196262933244281L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
						memberStubs.add((IRoomMember)host.getSender());
						sendMsg(new RoomDataPacket<IRoomMsg>(ISyncRoomMsg.make(memberStubs), myUserStub));
						System.out.println("Server at AddMember message.");
						
						try {
							adapter.getCmdToModelAdapter().displayText(host.getSender().getName() + " has joined the chat");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
							e.printStackTrace();
						}
						return null;
					}
				};
				mpa.setCmd(IAddMemberMsg.GetID(), addMemberMsgCmd);
				
				//String Message Command
				ARoomMsgCmd<IStringMsg> textMsgCmd = new ARoomMsgCmd<IStringMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -3498220311265032800L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IStringMsg> host, Void... params) {
						try {
							this.cmd2model.displayText(host.getSender().getName() + ": " + ((IStringMsg) host.getData()).getMessage());
							System.out.println("Received Text Message");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
							e.printStackTrace();
						}
						return null;
					}
				};
				mpa.setCmd(IStringMsg.GetID(), textMsgCmd);
				
				//Leave message command
				ARoomMsgCmd<ILeaveMsg> leaveMsgCmd = new ARoomMsgCmd<ILeaveMsg>() {


					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 1962512114493686812L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<ILeaveMsg> host, Void... params) {
						memberStubs.remove(host.getSender());
						try {
							adapter.getCmdToModelAdapter().displayText(host.getSender().getName() + " has left the chat");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							
							e.printStackTrace();
						}
						return null;
					}
				};
				mpa.setCmd(ILeaveMsg.GetID(), leaveMsgCmd);
				
				
				//Request message command
				ARoomMsgCmd<IRoomMsg> requestMsgCmd = new ARoomMsgCmd<IRoomMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -5397161403732926268L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
						// TODO Auto-generated method stub
						IDataPacketID msgID = ((IRequestCommandMsg) host.getData()).getMsgId();

						ARoomMsgCmd<? extends IRoomMsg> returnedCmd = (ARoomMsgCmd<? extends IRoomMsg>) idToUnknownCmd.get(msgID);
						System.out.println(returnedCmd.toString());

						try {
							((IRoomMember) host.getSender()).receiveMsg(new RoomDataPacket<IRoomMsg>(IAddCommandMsg.make(returnedCmd, msgID), myUserStub));
						} catch (RemoteException e) {
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
							e.printStackTrace();
						}
						return null;
					}
					
				};
				mpa.setCmd(IRequestCommandMsg.GetID(), requestMsgCmd);
//				
				//Return message cmd
				ARoomMsgCmd<IAddCommandMsg<IRoomMsg>> returnedMsgCmd = new ARoomMsgCmd<IAddCommandMsg<IRoomMsg>>() {



					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 9085380401410484428L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IAddCommandMsg<IRoomMsg>> host, Void... params) {
						ARoomMsgCmd<IRoomMsg> receivedCmd = (ARoomMsgCmd<IRoomMsg>) ((IAddCommandMsg<IRoomMsg>) host.getData()).getCommand();
						
						receivedCmd.setCmd2ModelAdpt(adapter.getCmdToModelAdapter());
						mpa.setCmd((host.getData()).getMsgId(), receivedCmd);
						
						// Check if we can execute the cached messages now
						ArrayList<RoomDataPacket<IRoomMsg>> executed = new ArrayList<RoomDataPacket<IRoomMsg>>();
						for (RoomDataPacket<IRoomMsg> unhandledMsg : unknownMsgs) {
							if (unhandledMsg.getData().getID().equals((host.getData()).getMsgId())) {
								unhandledMsg.execute(mpa);
								executed.add(unhandledMsg);
							}
						}
						unknownMsgs.removeAll(executed);
						
						return null;
					}
				};
				mpa.setCmd(IAddCommandMsg.GetID(), returnedMsgCmd);
				
				
				//request app cmd
				ARoomMsgCmd<IAddCommandMsg<IRoomMsg>> requestAppCmd = new ARoomMsgCmd<IAddCommandMsg<IRoomMsg>>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -5831832268304158263L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IAddCommandMsg<IRoomMsg>> host, Void... params) {
										
						return null;
					}
				};
				mpa.setCmd(IRequestAppMsg.GetID(), returnedMsgCmd);
				
				// SyncRoom Msg
				ARoomMsgCmd<ISyncRoomMsg> syncRoomMsg = new ARoomMsgCmd<ISyncRoomMsg>() {

					/**
					 * The serial version id
					 */
					private static final long serialVersionUID = 5570626073381656596L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<ISyncRoomMsg> host, Void... params) {
						// TODO Auto-generated method stub
						return null;
					}
					
				};
				mpa.setCmd(ISyncRoomMsg.GetID(), syncRoomMsg);
				
				
				ARoomMsgCmd<IServerCheckStatusMsg> serverCheckMsg = new ARoomMsgCmd<IServerCheckStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 3501454314251821806L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IServerCheckStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						try {
							((IRoomMember) host.getSender()).receiveMsg(new RoomDataPacket<IServerOkStatusMsg>(IServerOkStatusMsg.make(host.getData().getUUID()), myUserStub));
							System.out.println("Server sent IServerOkStatusMsg.");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
							System.out.println("Exception when dealing with IServerCheckMsg");
							e.printStackTrace();
						}
						return null;
					}
					
				};
				mpa.setCmd(IServerCheckStatusMsg.GetID(), serverCheckMsg);
				
				ARoomMsgCmd<IServerOkStatusMsg> serverOkMsg = new ARoomMsgCmd<IServerOkStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -6694220832727405741L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IServerOkStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						
						return null;
					}
					
				};
				mpa.setCmd(IServerOkStatusMsg.GetID(), serverOkMsg);
				
				
				ARoomMsgCmd<IRoomCheckStatusMsg> roomCheckMsg = new ARoomMsgCmd<IRoomCheckStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 7991727177291147946L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomCheckStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						try {
							((IRoomMember) host.getSender()).receiveMsg(new RoomDataPacket<IRoomOkStatusMsg>(IRoomOkStatusMsg.make(host.getData().getUUID()), myUserStub));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
							System.out.println("Exception when dealing with IRoomCheckStatusMsg");
							e.printStackTrace();
						}
						return null;
					}
					
				};
				mpa.setCmd(IRoomCheckStatusMsg.GetID(), roomCheckMsg);
				
				
				ARoomMsgCmd<IRoomOkStatusMsg> roomOkMsg = new ARoomMsgCmd<IRoomOkStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -1643941570235326233L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomOkStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						
						return null;
					}
					
				};
				mpa.setCmd(IRoomOkStatusMsg.GetID(), roomOkMsg);
				
				
				ARoomMsgCmd<IRemoteExceptionMsg> exceptionMsg = new ARoomMsgCmd<IRemoteExceptionMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -2955663958354758207L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRemoteExceptionMsg> host, Void... params) {
						// TODO Auto-generated method stub
						IRoomMember memberToCheck = host.getData().getBadStub();
						try {
							memberToCheck.receiveMsg(new RoomDataPacket<IRoomCheckStatusMsg>(IRoomCheckStatusMsg.make(), myUserStub));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							
							// remove this bad stub
							memberStubs.remove(memberToCheck);
							sendMsg(new RoomDataPacket<ISyncRoomMsg>(ISyncRoomMsg.make(memberStubs), myUserStub));
							
							e.printStackTrace();
						}
						
						return null;
					}
					
				};
				mpa.setCmd(IRemoteExceptionMsg.GetID(), exceptionMsg);
				
				
				ARoomMsgCmd<IRequestAppMsg> requestAppMsg = new ARoomMsgCmd<IRequestAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -296978478338126963L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRequestAppMsg> host, Void... params) {
						// TODO Auto-generated method stub
						
						try {
							host.getSender().receiveMsg(new RoomDataPacket<IProvideAppMsg>(IProvideAppMsg.make(applicationMe), myUserStub));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
							e.printStackTrace();
						}
						
						return null;
					}
					
				};
				mpa.setCmd(IRequestAppMsg.GetID(), requestAppMsg);
				
				
				ARoomMsgCmd<IProvideAppMsg> provideAppMsg = new ARoomMsgCmd<IProvideAppMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 1527781542589241392L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IProvideAppMsg> host, Void... params) {
						// TODO Auto-generated method stub
						return null;
					}
					
				};
				mpa.setCmd(IProvideAppMsg.GetID(), provideAppMsg);
				
				
				ARoomMsgCmd<IStatusMsg> statusMsg = new ARoomMsgCmd<IStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -3584283593806207710L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						System.out.println(host.getData().getDescription());
						return null;
					}
					
				};
				mpa.setCmd(IStatusMsg.GetID(), statusMsg);
				
				
				ARoomMsgCmd<IMapMessage> imgMsgCmd = new MapMsgCmd();
				mpa.setCmd(IMapMessage.GetID(), imgMsgCmd);
				idToUnknownCmd.put(IMapMessage.GetID(), imgMsgCmd);
				
				ARoomMsgCmd<IStartGameMsg> gameMsgCmd = new StartGameMsgCmd();
				mpa.setCmd(IStartGameMsg.GetID(), gameMsgCmd);
				idToUnknownCmd.put(IStartGameMsg.GetID(), gameMsgCmd);
				
				ARoomMsgCmd<ISubmitScoreMsg> submitScoreCmd = new SubmitScoreMsgCmd();
				mpa.setCmd(ISubmitScoreMsg.GetID(), submitScoreCmd);
				idToUnknownCmd.put(ISubmitScoreMsg.GetID(), submitScoreCmd);
				
				ARoomMsgCmd<IWinMsg> winCmd = new WinMsgCmd();
				mpa.setCmd(IWinMsg.GetID(), winCmd);
				idToUnknownCmd.put(IWinMsg.GetID(), winCmd);
				
				ARoomMsgCmd<ILoseMsg> loseCmd = new LoseMsgCmd();
				mpa.setCmd(IWinMsg.GetID(), loseCmd);
				idToUnknownCmd.put(ILoseMsg.GetID(), loseCmd);
				
			}
		};
		
	}
	
	/**
	 * Store unknown commands
	 */
	HashMap<IDataPacketID, ARoomMsgCmd<? extends IRoomMsg>> idToUnknownCmd = new HashMap<IDataPacketID, ARoomMsgCmd<? extends IRoomMsg>>();

	/**
	 * Send a msg to the chat room
	 * @param msg - msg to be sent
	 * @return - a list of threads sending the messages
	 */
	public ArrayList<Thread> sendChatRoomMsg(RoomDataPacket<? extends IRoomMsg> msg) {
		//TODO: handle returned result, status message?
		ArrayList<Thread> threadList = new ArrayList<Thread>();
		for (IRoomMember member : memberStubs) {
			Thread sendMsgThread = new Thread() {
				public void run() {
					try {
						member.receiveMsg(msg);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(member), myUserStub));
						e.printStackTrace();
					}
				}
			};
			threadList.add(sendMsgThread);
			sendMsgThread.start();
		}
		System.out.println("after msg sent " + memberStubs.size());
		return threadList;

	}
	
	/**
	 * 
	 * @return the message processing algo
	 */
	public DataPacketAlgo<Void, Void> getMessageProcessingAlgo() {
		return this.messageProcessingAlgo;
	}

	
//	public String getName() {
//		return teamName;
//	}

	/**
	 * Add user to the room
	 * @param user - user to be added
	 */
	public void addUser(IRoomMember user) {
		memberStubs.add(user);

	}
	
	/**
	 * Remove an user from the room
	 * @param user - user to be removed
	 */
	public void removeUser(IRoomMember user) {
		memberStubs.remove(user);
	}
	
	/**
	 * Get my stub in the room
	 * @return - my chat user stub
	 */
	public IRoomMember getMe() {
		return myUserStub;
	}

//	public UUID getID() {
//		// TODO Auto-generated method stub
//		return this.teamID;
//	}

	/**
	 * Set the adapter 
	 * @param adapter to retrieve the cmd to model adapter
	 */
	public void setCRMtoMiniModelAdapter(ITeamToMiniModelAdapter adapter) {
		// TODO Auto-generated method stub
		System.out.println("Hello in set adapter");
		this.adapter = adapter;
	}

	/**
	 * Method to send message
	 * @param msg message to send
	 */
	public void sendMsg(RoomDataPacket<? extends IRoomMsg> msg) {
		// TODO Auto-generated method stub
		this.sendChatRoomMsg(msg);
		
	}

	@Override
	public Collection<IRoomMember> getMembers() {
		// TODO Auto-generated method stub
		return this.memberStubs;
	}

	@Override
	public IRoomIdentity getInfo() {
		// TODO Auto-generated method stub
		return this.roomIdentity;
	}

}
