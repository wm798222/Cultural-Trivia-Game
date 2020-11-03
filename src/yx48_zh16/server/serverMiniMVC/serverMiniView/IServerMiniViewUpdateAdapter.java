package yx48_zh16.server.serverMiniMVC.serverMiniView;

/**
 * Adapter for the mini view to update its information.
 *
 * @param <TMessage> The generic type of message sent from the view.
 */
public interface IServerMiniViewUpdateAdapter<TMessage> {
	
	/**
	 * The method for the mini to quit.
	 */
	public void leave();
	
	/**
	 * Intermediate method for sending abstract messages from the view.
	 * @param message the message to be send.
	 */
	public void sendMessageClicked(TMessage message);
	
	/**
	 * Method for sending strings typed in the view.
	 * @param text the string message.
	 */
	public void sendTextMsg(String text);


	/**
	 * Method for sending an map from the view.
	 */
	public void sendMapMsg();

	/**
	 * Method of sending game to others
	 */
	public void sendGameMsg();

//	public void endGame();

}
