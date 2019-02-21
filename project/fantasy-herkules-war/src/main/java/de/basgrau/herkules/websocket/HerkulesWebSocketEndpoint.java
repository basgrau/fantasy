package de.basgrau.herkules.websocket;

import java.io.IOException;
import java.util.Map;

import javax.ejb.EJB;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import de.basgrau.hermes.shared.SharedValues;

/**
 * HerkulesWebSocket, WebScocket Endpoint.
 * @author basgrau
 *
 */
@ServerEndpoint(SharedValues.HERKULES_WSOCKET_ENDPOINT+"/{humanid}")
public class HerkulesWebSocketEndpoint {
	
	@EJB
	private HerkulesWebSocketBackendService _service;
	
	@OnOpen
	public void onOpen(@PathParam("humanid") String humanid, Session session) {
		//SessionID, kann client setzen... nehmen dann eigene. Speicherung bei Hades aber von sendungsnummer != sessionid
		System.out.println("onOpen::" + session.getId());

		System.out.println("HumanID as Param: "+ humanid);
		
		Map<String, Object> userProperties = session.getUserProperties();

		// change Session User Props
		session.getUserProperties().put(SharedValues.HUMANCLIENTID, humanid);
	
		if(!userProperties.isEmpty()) {
			_service.add(session);
			System.out.println("Session added.");
		}
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("onClose::" + session.getId());
		_service.deleteSession(session.getId());
	}

	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		System.out.println("onMessage::From=" + session.getId() + " Message=" + message);
		
		System.out.println("Rückantwort von Hades läuft hier auch ein. Und die WS Nachrichten vom Client.");
	}

	@OnError
	public void onError(Throwable t) {
		System.out.println("onError::" + t.getMessage());
	}
}
