package yx48_zh16.server.serverMiniMVC.serverMiniModel;

import javax.swing.JComponent;

import provided.mixedData.MixedDataKey;

/**
 * Adapter for the mini model to communicate to the mini view
 * @param <T> T Type
 *
 */
public interface IMiniModel2ViewAdapter<T> {

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
	 * @param <T> T type
	 * @param key key for DB
	 * @param value value to store 
	 */
	@SuppressWarnings("hiding")
	public <T> void putInServerDB(MixedDataKey<T> key, T value);

	/**
	 * @param <T> T type
	 * @param key key for DB
	 * @return T type
	 */
	@SuppressWarnings("hiding")
	public <T> T getFromServerDB(MixedDataKey<T> key);

}
