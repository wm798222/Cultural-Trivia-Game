package yx48_zh16.realgame.model;

import java.util.UUID;

import common.cmd.ARoomMsgCmd;
import common.packet.RoomDataPacket;
import provided.datapacket.IDataPacketID;
import provided.mixedData.MixedDataKey;

/**
 * SubmitScoreMsgCmd, command for submit score msg
 *
 */
public class SubmitScoreMsgCmd extends ARoomMsgCmd<ISubmitScoreMsg>{
	
	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 5479498986051888935L;
	
	/**
	 * Constructor.
	 */
	public SubmitScoreMsgCmd() {
		
	}

//	@Override
//	public Void apply(IDataPacketID index, AppDataPacket<ISubmitScoreMsg> host, Void... params) {
//		int score = host.getData().getYourScore();
//		UUID uuid = host.getData().getUUID();
//
////		MixedDataKey key = host.getData().getMDDKey();
//		MixedDataKey key = new MixedDataKey(uuid, "SubmitScoreCmd", Integer.class);
//		this.cmd2ModelAdpt.putItemInDB(key, score);
//		System.out.println("-----Submit CMD");
//		System.out.println(this.cm)
//		return null;
//	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<ISubmitScoreMsg> host, Void... params) {
		// TODO Auto-generated method stub
		int score = host.getData().getYourScore();
		UUID uuid = host.getData().getUUID();

		MixedDataKey key = new MixedDataKey(uuid, "SubmitScoreCmd", Integer.class);
		this.cmd2model.putItemInDB(key, score);
		return null;
	}

}
