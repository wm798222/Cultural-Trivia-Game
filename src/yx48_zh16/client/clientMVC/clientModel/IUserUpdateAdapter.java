package yx48_zh16.client.clientMVC.clientModel;

/**
 * The definition of adapter for updating information when an user accidentally left 
 * without leave message.
 *
 */
public interface IUserUpdateAdapter {
	
	/**
	 * Delete the accidentally left user from the collection of all connected/available users.
	 * @param user - the user to be deleted
	 */
	public void deleteConnectedUsers(UserWrapper user);

}
