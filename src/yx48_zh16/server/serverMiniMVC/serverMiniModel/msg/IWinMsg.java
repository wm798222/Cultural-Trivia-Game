package yx48_zh16.server.serverMiniMVC.serverMiniModel.msg;

import common.msg.IRoomMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * IWinMsg interface: server send to players who won
 *
 */
public interface IWinMsg extends IRoomMsg {
	/**
	 * Method for getting the DataPacketID corresponding to this message.
	 * @return The IDataPacketID
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(IWinMsg.class);
	}
	
	
	@Override
	public default IDataPacketID getID() {
		// TODO Auto-generated method stub
		return IWinMsg.GetID();
	}
	
	/**
	 * Getter method for the ID of this type of message.
	 * @return The ID for this type of message
	 */
	public IDataPacketID getMessageID();
	
	
	/**
	 * Method to make IWinMsg
	 * @return IWinMsg
	 */
	static IWinMsg make() {
		return new IWinMsg() {


			/**
			 * serial ID
			 */
			private static final long serialVersionUID = -8093750232625613634L;

			@Override
			public IDataPacketID getMessageID() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
	
	
	
}
