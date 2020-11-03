package yx48_zh16.server.serverMVC.serverModel;

import java.rmi.RemoteException;

import common.msg.IAppMsg;
import common.packet.AppDataPacket;
import common.receivers.IApplication;

/**
 * Concrete class of an IApplication object.
 *
 */
public class Application implements IApplication {

	@Override
	public void receiveMsg(AppDataPacket<? extends IAppMsg> msg) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
