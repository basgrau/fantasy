package de.basgrau.herkules.zeusconnect;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.hermes.status.Status;

public class ZeusConnectionClient {

	/**
	 * sendToZeus
	 * @param sendungsnummer Sendung
	 * @param humanClientId Id
	 * @param messageText Text
	 * @return Status
	 */
	public int sendToZeus(String sendungsnummer, String humanClientId, String messageText) {
		QueueConnection con = null;
		try {
			System.out.println("sendToZeus Started");
			// Send Message to MDBQ
			QueueConnectionFactory cf1 = (QueueConnectionFactory) new InitialContext()
					.lookup("java:comp/env/jndi_JMS_BASE_QCF");
			Queue queue = (Queue) new InitialContext().lookup("java:comp/env/jndi/MDBO");
			con = cf1.createQueueConnection();
			con.start();

			QueueSession session = con.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);

			QueueSender send = session.createSender(queue);
			TextMessage msg = session.createTextMessage();

			msg.setStringProperty(SharedValues.SENDUNGSNUMMER_PARAM, ("".equals(sendungsnummer))?"ID" + (System.currentTimeMillis() / 1000):sendungsnummer);
			msg.setStringProperty(SharedValues.HUMANCLIENTID,("".equals(humanClientId))?"HUMAN_1":humanClientId);
			// TODO hier muss noch eine Sendungsnummer vom Client rein
			System.out.println("Sendungsnummer: " + msg.getStringProperty(SharedValues.SENDUNGSNUMMER_PARAM));

			msg.setStringProperty("COLOR", "RED");

			msg.setText(("".equals(messageText))?"MDB Test - Nachricht zu " + msg.getStringProperty(SharedValues.SENDUNGSNUMMER_PARAM):messageText);
			send.send(msg);
			session.close();
			System.out.println("sendToZeus successful");
			return Status.ANGENOMMEN;
		} catch (NamingException | JMSException e) {
			e.printStackTrace();
			System.err.println("sendToZeus not sucessful");
			return Status.NOK;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
