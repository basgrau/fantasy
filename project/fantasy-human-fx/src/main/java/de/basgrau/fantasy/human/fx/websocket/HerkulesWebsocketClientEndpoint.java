package de.basgrau.fantasy.human.fx.websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;

import de.basgrau.hermes.shared.SharedValues;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

/**
 * HerkulesWebsocketClientEndpoint.
 * 
 * @author basgrau
 */
@ClientEndpoint
public class HerkulesWebsocketClientEndpoint {

	private Label _label;
	ObservableList<String> _alleVerarbeitetenSendungenListData;
	
	@OnMessage
	public String onMessage(String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				_label.setText("Verarbeitungsstatus: " + message);
				
				if(message.contains(SharedValues.SENDUNG_VERARBEITET)) {
					if(_alleVerarbeitetenSendungenListData != null) {
						_alleVerarbeitetenSendungenListData.add(message.replace(SharedValues.SENDUNG_VERARBEITET, ""));
					}
				}
			}
		});

		return message;
	}

	public void setLabel(Label label) {
		_label = label;
	}

	public void setSendungenListe(ObservableList<String> alleVerarbeitetenSendungenListData) {
		this._alleVerarbeitetenSendungenListData = alleVerarbeitetenSendungenListData;
	}
	
}
