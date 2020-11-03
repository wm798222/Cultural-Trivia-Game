package yx48_zh16.server.serverMVC.serverModel;

import java.util.Collection;

import common.identity.IIdentity;
import common.receivers.IRoomMember;
import common.virtualNetwork.IVirtualNetwork;

/**
 * Implementation of VirtualNetworkPlayers
 *
 */
public class VirtualNetworkPlayers implements IVirtualNetwork<IRoomMember, IIdentity> {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -7606308171176781063L;

	/**
	 * chat room members
	 */
	private Collection<IRoomMember> members;
	
	/**
	 * network identity
	 */
	private IIdentity identity;
	
	/**
	 * @param members - members in the room 
	 * @param identity - IIdentity of this network
	 */
	public VirtualNetworkPlayers(Collection<IRoomMember> members, IIdentity identity) {
		this.members = members;
		this.identity = identity;
	}

	@Override
	public Collection<IRoomMember> getMembers() {
		// TODO Auto-generated method stub
		return this.members;
	}

	@Override
	public IIdentity getInfo() {
		// TODO Auto-generated method stub
		return this.identity;
	}

}
