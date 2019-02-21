package de.basgrau.fantasy.human.fx.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.basgrau.fantasy.human.fx.websocket.HerkulesWebSocketClientConnector;
import de.basgrau.hermes.shared.GoetterNachricht;
import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.hermes.shared.UserConfig;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

/**
 * HumanFxUtil.
 * 
 * @author basgrau
 *
 */
public class HumanFxUtil {

	/**
	 * readInputStreamAsString.
	 * 
	 * @param in InputStream, die Entity aus der Response.
	 * @return Satz
	 */
	public static String readInputStreamAsString(InputStream in) {
		try {
			BufferedInputStream bis = new BufferedInputStream(in);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result = bis.read();
			while (result != -1) {
				byte b = (byte) result;
				buf.write(b);
				result = bis.read();
			}
			return buf.toString();
		} catch (IOException ex) {
			return null;
		}
	}

	/**
	 * testObjectToJson.
	 * 
	 * @param nachricht Nachricht
	 */
	public static void testObjectToJson(GoetterNachricht nachricht) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String json = mapper.writeValueAsString(nachricht);
			System.out.println(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * setWebSocketStatus.
	 * 
	 * @param rbtnStatus Button
	 * @param connected  Statu
	 */
	public static void setWebSocketStatus(RadioButton rbtnStatus, boolean connected) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (connected) {
					rbtnStatus.setText("on");
					rbtnStatus.setSelected(true);
				} else {
					rbtnStatus.setText("off");
					rbtnStatus.setSelected(false);
				}
			}
		});
	}

	/**
	 * updateStatusLabel
	 * 
	 * @param label   Label
	 * @param message Nachricht
	 */
	public static void updateStatusLabel(Label label, String message) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				label.setText("Status:" + message);
			}
		});
	}

	/**
	 * keepSocketConnected.
	 * 
	 * @param userConfig                         Config
	 * @param hWSCC                              Connector
	 * @param rbtnStatus                         Status
	 * @param alleVerarbeitetenSendungenListData
	 * @return id
	 */
	public static String keepSocketConnected(UserConfig userConfig, HerkulesWebSocketClientConnector hWSCC,
			RadioButton rbtnStatus, Label label, ObservableList<String> alleVerarbeitetenSendungenListData) {
		String id = "-";
		if (hWSCC != null && hWSCC.isConnected()) {
			HumanFxUtil.updateStatusLabel(label,
					"HumanClientID (" + userConfig.getHumanClientID() + ") SessionID("
							+ (hWSCC.getSessionId().equals(SharedValues.ERRORTEXT) ? "-" : hWSCC.getSessionId()) + ")");
		} else {
			id = hWSCC.connect(userConfig, label, alleVerarbeitetenSendungenListData);
		}

		setWebSocketStatus(rbtnStatus, hWSCC.isConnected());

		return id;
	}
}
