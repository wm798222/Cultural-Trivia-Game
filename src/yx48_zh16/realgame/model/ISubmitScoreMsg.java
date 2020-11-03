package yx48_zh16.realgame.model;

import java.util.UUID;
import common.msg.IRoomMsg;
import common.receivers.IRoomMember;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * ISubmitScoreMsg to submit game score to server
 *
 */
public interface ISubmitScoreMsg extends IRoomMsg{
	/**
	 * Method for getting the DataPacketID corresonding to this message.
	 * @return The IDataPacketID
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(ISubmitScoreMsg.class);
	}

	/**
	 * getter for your score
	 * @return your score
	 */
	public int getYourScore();
	
	/**
	 * get UUIS
	 * @return UUID
	 */
	public UUID getUUID();


	@Override
	public default IDataPacketID getID() {
		// TODO Auto-generated method stub
		return ISubmitScoreMsg.GetID();
	}
	
	/**
	 * Getter method for the ID of this type of message.
	 * @return The ID for this type of message
	 */
	public IDataPacketID getMessageID();
	
	/**
	 * make method for ISubmitScoreMsg
	 * @param serverStub server's stub
	 * @param yourScore your score
	 * @param uuid UUIS
	 * @return ISubmitScoreMsg
	 */
	static ISubmitScoreMsg make(IRoomMember serverStub, int yourScore, UUID uuid) {
		return new ISubmitScoreMsg() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 2186301920964189107L;

			@Override
			public IDataPacketID getMessageID() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getYourScore() {
				// TODO Auto-generated method stub
				return yourScore;
			}

			@Override
			public UUID getUUID() {
				// TODO Auto-generated method stub
				return uuid;
			}
			
		};
	}

}
