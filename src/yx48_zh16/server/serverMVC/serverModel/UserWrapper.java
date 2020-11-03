package yx48_zh16.server.serverMVC.serverModel;

import java.rmi.RemoteException;

import common.receivers.IApplication;

/**
 * Wrapper class around a client for setting the correct toString.
 *
 */
public class UserWrapper {
	
	/**
	 * The user at the user level.
	 */
	private IApplication user;
	
	/**
	 * Adapter for updating user information.
	 */
	private IUserUpdateAdapter _userUpdateAdapter;
	
	/**
	 * Constructor of the user wrapper class.
	 * @param iConnection - the user
	 * @param userUpdateAdapter - the user update adapter
	 */
	public UserWrapper(IApplication iConnection, IUserUpdateAdapter userUpdateAdapter) {
		this.user = iConnection;
		this._userUpdateAdapter = userUpdateAdapter;
	}
	
	/**
	 * Getter method of the user being wrapped.
	 * @return the user being wrapped.
	 */
	public IApplication getUser() {
		return user;
	}

	@Override
	public String toString() {
		try {
			return this.user.getName();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this._userUpdateAdapter.deleteConnectedUsers(this);
			return "Get name exception!";
			
		}
	}
	
}
