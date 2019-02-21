package de.basgrau.zeus.ejb;

import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.basgrau.hermes.shared.SharedValues;

/**
 * ZeusActor, Zeus agiert, reicht Nachricht an Hades weiter, vorher ändert sie
 * diese aber leicht.
 * 
 * @author basgrau
 *
 */
@Stateless
public class ZeusActor implements ActorFacade {

	/**
	 * 
	 */
	public String send(Message message) {
		// System.out.println("Nachricht im Olymp erhalten: " + message);

		QueueConnection con = null;
		try {
			QueueConnectionFactory cf1 = (QueueConnectionFactory) new InitialContext()
					.lookup("java:comp/env/jndi_JMS_BASE_QCF");
			Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jndi/MDBU");
			con = cf1.createQueueConnection();
			con.start();

			QueueSession session = con.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

			QueueSender send = session.createSender(queue);
			TextMessage msg = session.createTextMessage();

			msg.setStringProperty("COLOR", "Green"); // Means Done

			String text = "";
			if (message instanceof TextMessage) {
				text = ((TextMessage) message).getText();
			}

			msg.setText(text);
			msg.setStringProperty(SharedValues.HUMANCLIENTID,
					message.getStringProperty(SharedValues.HUMANCLIENTID));
			msg.setStringProperty(SharedValues.SENDUNGSNUMMER_PARAM,
					message.getStringProperty(SharedValues.SENDUNGSNUMMER_PARAM));
			send.send(msg);
			session.close();
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
			System.err.println("error");
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}

		return "send to unterwelt";
	}

}
