package yx48_zh16.server.serverMVC.serverModel;

import java.util.Collection;

import common.identity.IIdentity;
import common.receivers.IApplication;
import common.virtualNetwork.IVirtualNetwork;

/**
 *
 */
public class VirtualNetworkApplications implements IVirtualNetwork<IApplication, IIdentity> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2877823666693530731L;

	/**
	 * constructor
	 * @param members network members
	 * @param identity network identity
	 */
	public VirtualNetworkApplications(Collection<IApplication> members, IIdentity identity) {
	}

	@Override
	public Collection<IApplication> getMembers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Identity getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
