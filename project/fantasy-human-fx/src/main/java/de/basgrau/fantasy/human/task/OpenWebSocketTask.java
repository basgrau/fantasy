package de.basgrau.fantasy.human.task;

import de.basgrau.fantasy.human.fx.util.HumanFxUtil;
import de.basgrau.fantasy.human.fx.websocket.HerkulesWebSocketClientConnector;
import de.basgrau.hermes.shared.UserConfig;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

/**
 * OpenWebSocketTask.
 * 
 * @author basgrau
 *
 */
public class OpenWebSocketTask extends Task<String> {
	
	private ObservableList<String> _wsNachrichtenListData;
	private String _status = "error";
	private HerkulesWebSocketClientConnector _hWSCC;
	private RadioButton _rbtn;
	private UserConfig _userConfig;
	private String _connectStatus = "error";
	private Label _labelStatus;
	private Label _labelVerarbeitung;
	private ObservableList<String> _alleVerarbeitetenSendungenListData;
	
	/**
	 * OpenWebSocketTask
	 * @param wsNachrichtenListData List
	 * @param userConfig Config
	 * @param hWSCC Connector
	 * @param rbtn Button
	 * @param labelStatus Label
	 * @param labelVerarbeitung Label
	 * @param alleVerarbeitetenSendungenListData Liste
	 */
	public OpenWebSocketTask(ObservableList<String> wsNachrichtenListData, UserConfig userConfig, HerkulesWebSocketClientConnector hWSCC, RadioButton rbtn, Label labelStatus, Label labelVerarbeitung, ObservableList<String> alleVerarbeitetenSendungenListData) {
		this._wsNachrichtenListData = wsNachrichtenListData;
		this._rbtn = rbtn;
		this._hWSCC = hWSCC;
		this._userConfig = userConfig;
		this._labelStatus = labelStatus;
		this._labelVerarbeitung = labelVerarbeitung;
		this._alleVerarbeitetenSendungenListData = alleVerarbeitetenSendungenListData;
	}

	@Override
	protected String call() throws Exception {
		if (_hWSCC != null && _hWSCC.isConnected()) {
			System.out.println("bereits verbunden");
			return _status;
		} else {
			System.out.println("verbinden mit WebSocket");
			_connectStatus = _hWSCC.connect(_userConfig,_labelVerarbeitung, _alleVerarbeitetenSendungenListData);
			return _connectStatus;
		}

	}

	@Override
	protected void succeeded() {
		_wsNachrichtenListData.add("HumanClient-ID: " + _userConfig.getHumanClientID());
		_wsNachrichtenListData.add("Session-ID: " + _connectStatus);
		System.out.println(_connectStatus);
		if (!_connectStatus.equals("error")) {
			HumanFxUtil.updateStatusLabel(_labelStatus,
					"HumanClientID (" + _userConfig.getHumanClientID() + ") SessionID(" + _connectStatus + ")");
		}

		HumanFxUtil.setWebSocketStatus(_rbtn, _hWSCC.isConnected());
	}
}
