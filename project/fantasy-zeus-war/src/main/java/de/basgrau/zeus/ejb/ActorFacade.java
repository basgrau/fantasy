package de.basgrau.zeus.ejb;

import javax.jms.Message;

/**
 * ActorFacade
 * @author basgrau
 *
 */
public interface ActorFacade {

	/**
	 * send.
	 * @param message Nachricht (JMS)
	 * @return Text Status
	 */
	public String send(Message message);
	
}
