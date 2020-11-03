package yx48_zh16.client.clientMiniMVC.clientMiniView;

/**
 * Adapter for the mini view to update its information.
 *
 * @param <TMessage> The generic type of message sent from the view.
 */
public interface IClientMiniViewUpdateAdapter<TMessage> {
	
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
	 * Method for sending an image from the view.
	 * @param text the image file name.
	 */
	public void sendImageMsg(String text);
	

}
