package yx48_zh16.client.clientMVC.clientModel;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.Timer;

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
import yx48_zh16.client.clientMiniMVC.clientMiniModel.ITeamToMiniModelAdapter;

/**
 * An implementation of IChatRoom
 *
 */
public class Room implements IRoom {

	/**
	 * 
	 */
	private static final long serialVersionUID = -968473737258401156L;

	/**
	 * The chat user represents this person
	 */
	private IRoomMember myUserStub;
	
	/**
	 * Stubs of other users in the room
	 */
	private Collection<IRoomMember> memberStubs = new ArrayList<IRoomMember>();

	/** 
	 * Default command
	 */
	private ARoomMsgCmd<IRoomMsg> defautlCmd;
	
	/**
	 * Message processing algo
	 */
	private DataPacketAlgo<Void, Void> messageProcessingAlgo;
	
	/**
	 * room identity
	 */
	private IRoomIdentity roomIdentity;	
	
	/**
	 * The time elapse to update the information of the model to the view.
	 */
	private int _timeSlice = 5000;  // update every 50 milliseconds
	/**
	 * The timer that passes a lambda function to update information from the model periodically.
	 */
	private Timer _timer = new Timer(_timeSlice,  (e) -> this.sendCheckServerMsgWithTimer());  // use the timer to update the information of model to view periodically
	
	/**
	 * for status msg
	 */
	private int serverCheckMissed;
	
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
	 * @param applicationMe application level stub
	 */
	public Room(UUID teamID, String teamName, IRoomMember myUserStub, String playerName, IApplication applicationMe) {
		if (teamID != null) {
		}
		this.roomIdentity = new IRoomIdentity() {

			/**
			 * Serial Version UID
			 */
			private static final long serialVersionUID = -3271505560439870239L;

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
			private static final long serialVersionUID = 1554000983463056469L;

			@Override
			public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
				// TODO Auto-generated method stub
				//Cache the unknown message
				unknownMsgs.add(host);
				
				System.out.println((IRoomMember)host.getSender());
				
				//TODO: handle returned result, status message?
				try {
					((IRoomMember) host.getSender()).receiveMsg(new RoomDataPacket<IRequestCommandMsg>(IRequestCommandMsg.make(host.getData().getID()), myUserStub));
					System.out.println("This is client send request: " + new RoomDataPacket<IRequestCommandMsg>(IRequestCommandMsg.make(host.getData().getID()), myUserStub));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return null;
			}
			
		};
		messageProcessingAlgo = new DataPacketAlgo<Void, Void>(this.defautlCmd){
			
			/**
			 * Serial Version UID
			 */
			private static final long serialVersionUID = -7121458081066897712L;
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
					private static final long serialVersionUID = 1867160912727317400L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
						System.out.println("Message failed!");
						return null;
					}
					
				};
				mpa.setCmd(IFailureMsg.GetID(), failureMsgCmd);

				
				//Join Message Command
				ARoomMsgCmd<IRoomMsg> joinMsgCmd = new ARoomMsgCmd<IRoomMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 4103605203311280569L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomMsg> host, Void... params) {
//						memberStubs.add((IRoomMember)host.getSender());
//						try {
//							adapter.getCmdToModelAdapter().displayText(host.getSender().getName() + " has joined the chat");
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						return null;
					}
				};
				mpa.setCmd(IAddMemberMsg.GetID(), joinMsgCmd);
				
				//String Message Command
				ARoomMsgCmd<IStringMsg> textMsgCmd = new ARoomMsgCmd<IStringMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -1070332808689557122L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IStringMsg> host, Void... params) {
						try {
							this.cmd2model.displayText(host.getSender().getName() + ": " + ((IStringMsg) host.getData()).getMessage());
							System.out.println("Received Text Message");
						} catch (RemoteException e) {
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
							// TODO Auto-generated catch block
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
					private static final long serialVersionUID = -2028191804481707343L;

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
					private static final long serialVersionUID = -2312093179005075133L;

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
				
				//Return message cmd
				ARoomMsgCmd<IAddCommandMsg<IRoomMsg>> returnedMsgCmd = new ARoomMsgCmd<IAddCommandMsg<IRoomMsg>>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 8076739527877862925L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IAddCommandMsg<IRoomMsg>> host, Void... params) {
						ARoomMsgCmd<IRoomMsg> receivedCmd = (ARoomMsgCmd<IRoomMsg>) ((IAddCommandMsg<IRoomMsg>) host.getData()).getCommand();
						
//						try {
//							host.getSender().receiveMsg(new RoomDataPacket<IRoomMsg>(ISuccessMsg.make("Successfully got unknown command"), myUserStub));
//						} catch (RemoteException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}	
					
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
				
				
				ARoomMsgCmd<ISyncRoomMsg> syncRoomCmd = new ARoomMsgCmd<ISyncRoomMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -6499829614288433761L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<ISyncRoomMsg> host, Void... params) {
						// TODO Auto-generated method stub
						Collection<IRoomMember> updatedMembers = host.getData().getMembers();
						
						IRoomMember serverStub = host.getSender();
						memberStubs = updatedMembers;
						memberStubs.remove(myUserStub);
						memberStubs.add(serverStub);
						
						return null;
					}
					
				};
				mpa.setCmd(ISyncRoomMsg.GetID(), syncRoomCmd);
				
				
				new ARoomMsgCmd<IAddCommandMsg<IRoomMsg>>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 8076739527877862925L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IAddCommandMsg<IRoomMsg>> host, Void... params) {
						
						return null;
					}
				};
				mpa.setCmd(IRequestAppMsg.GetID(), returnedMsgCmd);
				
				
				ARoomMsgCmd<IServerCheckStatusMsg> serverCheckMsg = new ARoomMsgCmd<IServerCheckStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -6902909198215246461L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IServerCheckStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
//						try {
//							((IRoomMember) host.getSender()).receiveMsg(new RoomDataPacket<IServerOkStatusMsg>(IServerOkStatusMsg.make(host.getData().getUUID()), myUserStub));
//						} catch (RemoteException e) {
//							// TODO Auto-generated catch block
//							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
//							
//							System.out.println("Exception when dealing with IServerCheckMsg");
//							e.printStackTrace();
//						}
						
						return null;
					}
					
				};
				mpa.setCmd(IServerCheckStatusMsg.GetID(), serverCheckMsg);
				
				
				ARoomMsgCmd<IServerOkStatusMsg> serverOkMsg = new ARoomMsgCmd<IServerOkStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = 6344775457866387406L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IServerOkStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub				
						if (serverCheckMissed > 0) {
							serverCheckMissed--;
						}		
						return null;
					}
					
				};
				mpa.setCmd(IServerOkStatusMsg.GetID(), serverOkMsg);
				
				
				ARoomMsgCmd<IRoomCheckStatusMsg> roomCheckMsg = new ARoomMsgCmd<IRoomCheckStatusMsg>() {

					/**
					 * Serial Version UID
					 */
					private static final long serialVersionUID = -420433253911891608L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IRoomCheckStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						try {
							((IRoomMember) host.getSender()).receiveMsg(new RoomDataPacket<IRoomOkStatusMsg>(IRoomOkStatusMsg.make(host.getData().getUUID()), myUserStub));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(host.getSender()), myUserStub));
//							sendMsg(new RoomDataPacket<IRoomMsg>(I))
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
					private static final long serialVersionUID = -1212203226699929514L;

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
					private static final long serialVersionUID = 8642055109465633171L;

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
					private static final long serialVersionUID = -8571915902258322445L;

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
					private static final long serialVersionUID = 1965787465058928716L;

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
					private static final long serialVersionUID = -5961722013150634608L;

					@Override
					public Void apply(IDataPacketID index, RoomDataPacket<IStatusMsg> host, Void... params) {
						// TODO Auto-generated method stub
						System.out.println(host.getData().getDescription());
						return null;
					}
					
				};
				mpa.setCmd(IStatusMsg.GetID(), statusMsg);
				
			}
		};
		
		this._timer.start();
		
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
	 * send msg method
	 * @param msg - message to send
	 */
	public void sendMsg(RoomDataPacket<? extends IRoomMsg> msg) {
		// TODO Auto-generated method stub
		this.sendChatRoomMsg(msg);
		
	}
	
	/**
	 * check server status
	 */
	public void sendCheckServerMsgWithTimer() {
		// TODO Auto-generated method stub
		
//		sendMsg(new RoomDataPacket<IServerCheckStatusMsg>(IServerCheckStatusMsg.make(), myUserStub));
		for (IRoomMember i : memberStubs) {
			try {
				i.receiveMsg(new RoomDataPacket<IServerCheckStatusMsg>(IServerCheckStatusMsg.make(), myUserStub));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				System.out.println("Eception when sending server check msg");
				sendMsg(new RoomDataPacket<IRoomMsg>(IRemoteExceptionMsg.make(i), myUserStub));
				e.printStackTrace();
			}
		}
		
		this.serverCheckMissed++;
		if (this.serverCheckMissed > 3) {
			this.adapter.leave();
			this._timer.stop();
		}
		
	}
	
	/**
	 * start timer
	 */
	public void startTimer() {
		this._timer.start();
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
