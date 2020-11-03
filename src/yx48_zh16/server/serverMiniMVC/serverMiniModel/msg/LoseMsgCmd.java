package yx48_zh16.server.serverMiniMVC.serverMiniModel.msg;

import common.cmd.ARoomMsgCmd;
import common.packet.RoomDataPacket;
import provided.datapacket.IDataPacketID;

/**
 * Unknown command for ILoseMsg
 *
 */
public class LoseMsgCmd extends ARoomMsgCmd<ILoseMsg>{

	/**
	 * serial ID
	 */
	private static final long serialVersionUID = 911166009153566455L;

	@Override
	public Void apply(IDataPacketID index, RoomDataPacket<ILoseMsg> host, Void... params) {
		// TODO Auto-generated method stub
		this.cmd2model.displayText("Server: You Lost, try again next time!");
		return null;
	}

}
