package de.basgrau.fantasy.human.fx.websocket;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.hermes.shared.UserConfig;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

/**
 * 
 * @author basgrau
 *
 */
public class HerkulesWebSocketClientConnector {

	private Session session;
	private HerkulesWebsocketClientEndpoint _herkulesWebsocketClientEndpoint;
	
	
	public HerkulesWebSocketClientConnector() {
		initEndpoint();
	}

	private void initEndpoint() {
		_herkulesWebsocketClientEndpoint = new HerkulesWebsocketClientEndpoint();
	}
	
	public String getSessionId() {
		if(session != null)
			return session.getId();
		
		return "error";
	}
	
	/**
	 * isConnected.
	 * @return Status Session
	 */
	public boolean isConnected() {
		if(session != null) {
			return session.isOpen();
		}
		
		return false;
	}
	
	/**
	 * connect.
	 * 
	 * @param userConfig Config
	 * @param label Label
	 * @param alleVerarbeitetenSendungenListData Liste
	 * @return Status
	 */
	public String connect(UserConfig userConfig, Label label, ObservableList<String> alleVerarbeitetenSendungenListData) {
		WebSocketContainer container = null;

		try {
			container = ContainerProvider.getWebSocketContainer();
			session = container.connectToServer(_herkulesWebsocketClientEndpoint,
					URI.create(SharedValues.HERKULES_WSOCKET_ENDPOINT_URL + "/" + userConfig.getHumanClientID()));
			
			if(session != null) {
				_herkulesWebsocketClientEndpoint.setLabel(label);
				_herkulesWebsocketClientEndpoint.setSendungenListe(alleVerarbeitetenSendungenListData);
			}
			
			return session.getId();
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		} finally {
			
		}
	}

	/**
	 * sendMessage.
	 * 
	 * @param messageText Text
	 * @return Status
	 */
	public String sendMessage(String messageText) {
		String status = "error";
		try {
			session.getBasicRemote().sendText(messageText);
			return "done";
		} catch (IOException e) {
			e.printStackTrace();
		}

		return status;
	}

	/**
	 * close.
	 * 
	 * @return Status
	 */
	public String close() {
		String status = "error";
		if (session != null) {
			try {
				session.close();
				return "done";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return status;

	}
}
