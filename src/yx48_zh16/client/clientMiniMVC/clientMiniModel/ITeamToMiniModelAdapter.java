package yx48_zh16.client.clientMiniMVC.clientMiniModel;

import common.ICmd2ModelAdapter;

/**
 * Definition of the adapter for the chatroom to communicate to the model
 *
 */
public interface ITeamToMiniModelAdapter {
	
	/**
	 * Abstract method for getting the command to model adapter
	 * @return the command to model adapter
	 */
	public ICmd2ModelAdapter getCmdToModelAdapter();

	/**
	 * leave method
	 */
	public void leave();
}
