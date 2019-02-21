package de.basgrau.zeus.mdb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.zeus.ejb.ActorFacade;

/**
 * ZeusMDB, Zeus Nachrichtannahme.
 * @author basgrau
 *
 */
@MessageDriven
public class ZeusMDB implements MessageListener {

	@EJB
	private ActorFacade actor;
	
	@Resource
	MessageDrivenContext ejbcontext;

	
	@PostConstruct
	public void postConstruct() {
		System.out.println(this.getClass().getName() + " started.");
	}

	public void onMessage(Message message) {
		try {
			System.out.println("Erhaltene Nachricht:");
			System.out.println("HumanId: " + message.getStringProperty(SharedValues.HUMANCLIENTID));
			System.out.println("Sendung: " + message.getStringProperty(SharedValues.SENDUNGSNUMMER_PARAM));
			System.out.println("Nachricht wird gesendet.");
			
			if (message instanceof TextMessage) {
				TextMessage msg = (TextMessage)message;
				if(actor != null) {
					actor.send(msg);
				} else {
					System.out.println("Actor null");
				}
				
			}
			
			System.out.println("Nachricht zur Unterwelt gesendet.");
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
}
