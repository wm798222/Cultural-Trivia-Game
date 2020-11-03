package yx48_zh16.client.clientMiniMVC.clientMiniModel;

import javax.swing.JComponent;

/**
 * Adapter for the mini model to communicate to the mini view
 *
 */
public interface IMiniModel2ViewAdapter {

	/**
	 * Method for displaying message received in the model
	 * @param message the text message received
	 */
	public void displayMessage(String message);
	
	/**
	 * Method for displaying the java.awt component received in the model
	 * @param component the JComponent data received to be displayed
	 */
	public void displayComponent(JComponent component);

	/**
	 * leave method
	 */
	public void leave();

}
