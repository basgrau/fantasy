package de.basgrau.herkules.websocket;

import java.util.Date;
import java.util.HashMap;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.websocket.Session;

import de.basgrau.herkules.hadesconnect.UnterweltApiClient;
import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.hermes.status.Status;

/**
 * HerkulesWebSocketBackendService, Backend zum WebSocket, Timer der Status
 * prüft und Nachricht schickt.
 * 
 * @author basgrau
 *
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class HerkulesWebSocketBackendService {
	private final HashMap<String, Session> sMap = new HashMap<String, Session>();

	/** . */
	private Date lastAutomaticTimeout;

	/**
	 * add.
	 * 
	 * @param s Session
	 */
	@Lock(LockType.WRITE)
	public void add(Session s) {
		sMap.put(s.getId(), s);
		printStoredSessions();
	}

	/** . */
	public void init() {
		System.out.println(this.getClass().getName() + " started.");
	}

	/** . */
	@Lock(LockType.WRITE)
	public void deleteSession(String sessionId) {
		printStoredSessions();
		sMap.remove(sessionId);
		System.out.println("Session " + sessionId + " deleted.");
	}

	private void printStoredSessions() {
		int size = sMap.size();
		System.out.println("Storage contains: " + size + " active Sessions.");
		for (String key : sMap.keySet()) {
			System.out.println("Session " + key + " in Storage.");
		}
	}

	/**
	 * getVerarbeitungsStatus.
	 * 
	 * @param humandid       HumanId
	 * @param sendungsnummer Sendungsnummer
	 * @return Status
	 */
	private String getVerarbeitungsStatus(String humanid, String sendungsnummer) {
		return new UnterweltApiClient().getStatus(humanid, sendungsnummer);
	}

	/**
	 * getSendungen.
	 * 
	 * @param humanid Id
	 * @return Sendungen.
	 */
	private String[] getSendungen(String humanid) {
		return new UnterweltApiClient().getAlleSendungen(humanid);
	}

	/**
	 * automaticTimeout.
	 */
	@Schedule(minute = "*/1", hour = "*", persistent = false) // jede Minute
	@Lock(LockType.READ)
	public void automaticTimeout() {
		this.setLastAutomaticTimeout(new Date());
		System.out.println("Timeout ausgelöst.");

		printStoredSessions();

		for (String key : sMap.keySet()) {
			Session s = sMap.get(key);
			if (s.isOpen()) {
				String humanId = (String) s.getUserProperties().get(SharedValues.HUMANCLIENTID);

				System.out.println("Zum Human " + humanId + " soll der Status aller Sendungen geprüft werden.");
				try {
					String[] sendungen = getSendungen(humanId);

					if (sendungen == null) {
						System.err.println("Keine Sendungen zur " + humanId + " enthalten.");
						return;
					} 
					
					for (String sendungsNummer : sendungen) {
						System.out.println("Status wird für Sendung "+ sendungsNummer + " abgefragt.");
						String verarbeitungsStatus = getVerarbeitungsStatus(humanId, sendungsNummer);
						if ( verarbeitungsStatus.contains(""+Status.OK)) {
							s.getBasicRemote().sendText(sendungsNummer + SharedValues.SENDUNG_VERARBEITET);
							
							String deleteSendungStatus = deleteSendung(humanId, sendungsNummer);
							if(deleteSendungStatus.equals(""+Status.OK)) {
								System.out.println("Sendung " + sendungsNummer + " aus cache gelöscht.");
							}
						}
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			} else {
				sMap.remove(key);
			}
		}
	}

	/**
	 * deleteSendung.
	 * @param humanId
	 * @param sendungsNummer
	 * @return status
	 */
	private String deleteSendung(String humanId, String sendungsNummer) {
		return new UnterweltApiClient().deleteSendung(humanId, sendungsNummer);
	}

	/**
	 * getLastAutomaticTimeout.
	 * 
	 * @return Status
	 */
	public String getLastAutomaticTimeout() {
		if (lastAutomaticTimeout != null) {
			return lastAutomaticTimeout.toString();
		} else {
			return "niemals";
		}
	}

	/**
	 * setLastAutomaticTimeout.
	 * 
	 * @param lastAutomaticTimeout Letzter
	 */
	public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
		this.lastAutomaticTimeout = lastAutomaticTimeout;
	}

}
