package de.basgrau.hades.jms.mdb;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import de.basgrau.hades.status.StatusSingleton;
import de.basgrau.hermes.shared.SharedValues;

/**
 * HadesMDB, Message Driven Bean für die Unterwelt.
 * @author basgrau
 *
 */
@MessageDriven
public class HadesMDB implements MessageListener {
	
	@Resource
	MessageDrivenContext ejbcontext;

	@EJB
	private StatusSingleton storage;
	
	@PostConstruct
	public void postConstruct() {
		System.out.println("Unterwelt gestartet.");
	}

	/**
	 * onMessage.
	 */
	public void onMessage(Message message) {
		try {
			System.out.println("Erhaltene Nachricht" + message);
			
			if (message instanceof TextMessage) {
				TextMessage msg = (TextMessage)message;
				
				String humanId = msg.getStringProperty(SharedValues.HUMANCLIENTID);
				String sendungsnummer = msg.getStringProperty(SharedValues.SENDUNGSNUMMER_PARAM);
				
				
				if(storage == null) {
					System.err.println("Storage ist null!");
				}else {
					System.out.println("Added (human: " + humanId + " | sendung: " + sendungsnummer +")" );
					if(humanId == null) {
						System.err.println("Fehler: HumanId ist null :-(");
						return;
					}
					storage.add(humanId, sendungsnummer);
					
					Thread.sleep(SharedValues.MAX_THREAD_PAUSE); //so lange darf ich höchstens warten -> sonst Timeout bei Websocket
					
					String reversedMessage = new StringBuilder(msg.getText()).reverse().toString();
					System.out.println(reversedMessage);
					storage.done(humanId, sendungsnummer);
				}
		
			}
			
			System.out.println("Nachricht in Unterwelt verarbeitet.");
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
}
