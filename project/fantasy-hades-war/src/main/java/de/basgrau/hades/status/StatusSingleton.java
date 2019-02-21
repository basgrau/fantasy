package de.basgrau.hades.status;

import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import de.basgrau.hermes.status.Status;

@Singleton
@Startup
public class StatusSingleton {

	class ClientStatus {
		private String humanId;
		private ConcurrentHashMap<String, Integer> sendungen;
		
		public ClientStatus(String humanId) {
			this.humanId = humanId;
			sendungen = new ConcurrentHashMap<String, Integer>();
		}
		
		public String getHumanID() {
			return humanId;
		}
		
		public void setHumanId(String id) {
			humanId = id;
		}
		
		public void add(String sendungNr) {
			if (!sendungen.containsKey(sendungNr)) {
				sendungen.put(sendungNr, Status.NOK);
			}
		}
		
		public void changeStatus(String sendungNr, int status) {
			for (String nr : sendungen.keySet()) {
				if (nr.equals(sendungNr)) {
					sendungen.remove(sendungNr);
					sendungen.put(sendungNr, status);
				}
			}
		}
		
		public void deleteSendung(String sendungNr) {
			for (String nr : sendungen.keySet()) {
				if (nr.equals(sendungNr)) {
					sendungen.remove(sendungNr);
				}
			}
		}
		
		public ConcurrentHashMap<String, Integer> getSendungen(){
			return sendungen;
		}
	}
	
	//wäre später eine DB
	private ConcurrentHashMap<String, ClientStatus> currentHashMap = new ConcurrentHashMap<String, ClientStatus>();
	
	synchronized
	public void add(String humanid, String sendung) {
		ClientStatus clientStatus = null;
		if (currentHashMap != null) {
			for (String id : currentHashMap.keySet()) {
				if(id.equals(humanid)) {
					 clientStatus = currentHashMap.get(humanid);
					 currentHashMap.remove(humanid);
				}
			}
			
			if(clientStatus == null) {
				clientStatus = new ClientStatus(humanid);
			}
			
			clientStatus.add(sendung);
			
			currentHashMap.put(humanid, clientStatus);
		} else {
			System.err.println("is null?!");
		}
	}
	
	public void printSession() {
		for (String key : currentHashMap.keySet()) {
			System.out.println(key); //Prints Humandids
		}
	}
	
	synchronized
	public void done(String humanid, String sendungsnummer) {
		for (String id : currentHashMap.keySet()) {
			if(id.equals(humanid)) {
				ClientStatus clientStatus = currentHashMap.get(id);
				clientStatus.changeStatus(sendungsnummer, Status.OK);
				currentHashMap.remove(id);
				currentHashMap.put(id, clientStatus);
			}
		}
	}
	
	synchronized
	public void delete(String humanid, String sendungsnummer) {
		for (String id : currentHashMap.keySet()) {
			if(id.equals(humanid)) {
				ClientStatus clientStatus = currentHashMap.get(id);
				clientStatus.deleteSendung(sendungsnummer);
				currentHashMap.remove(id);
				currentHashMap.put(id, clientStatus);
			}
		}
	}

	public ConcurrentHashMap<String, Integer> getSendungen(String humanid) {
		if (currentHashMap == null) {
			return null;
		}
		
		for (String id : currentHashMap.keySet()) {
			if(id.equals(humanid)) {
				ClientStatus clientStatus = currentHashMap.get(humanid);
				return clientStatus.getSendungen();
			}
		}
		
		return null;
	}
	
	public int getVerarbeitungsStatus(String humanId, String sendungsnummer) {
		if (currentHashMap == null) {
			return 0;
		}
		
		for (String id : currentHashMap.keySet()) {
			if(id.equals(humanId)) {
				ClientStatus clientStatus = currentHashMap.get(humanId);
				ConcurrentHashMap<String,Integer> sendungen = clientStatus.getSendungen();
				if(sendungen != null) {
					for (String nr : sendungen.keySet()) {
						if (nr.equals(sendungsnummer)) {
							return sendungen.get(sendungsnummer);
						}
					}
				}
			}
		}
		
		return 0;
	}
}
