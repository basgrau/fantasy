package de.basgrau.fantasy.human.task;

import de.basgrau.fantasy.human.fx.util.HumanFxUtil;
import de.basgrau.fantasy.human.fx.websocket.HerkulesWebSocketClientConnector;
import de.basgrau.hermes.shared.UserConfig;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

/**
 * CloseWebSocketTask.
 * 
 * @author basgrau
 *
 */
public class CloseWebSocketTask extends Task<String> {

	private ObservableList<String> _wsNachrichtenListData;
	private String _status = "error";
	private HerkulesWebSocketClientConnector _hWSCC;
	private RadioButton _rbtn;
	private UserConfig _userConfig;
	private String _connectStatus = "error";
	private Label _label;

	/**
	 * CloseWebSocketTask
	 * 
	 * @param userConfig Config
	 * @param hWSCC      Connector
	 * @param rbtn       Button
	 */
	public CloseWebSocketTask(ObservableList<String> wsNachrichtenListData, UserConfig userConfig,
			HerkulesWebSocketClientConnector hWSCC, RadioButton rbtn, Label label) {
		this._wsNachrichtenListData = wsNachrichtenListData;
		this._rbtn = rbtn;
		this._hWSCC = hWSCC;
		this._userConfig = userConfig;
		this._label = label;
	}

	@Override
	protected String call() throws Exception {
		if (_hWSCC != null && _hWSCC.isConnected()) {
			_connectStatus = _hWSCC.close();
		} else {
			System.out.println("nicht verbunden");
		}

		return _status;
	}

	@Override
	protected void succeeded() {
		System.out.println("Schließen WebSocket: " + _connectStatus);
		if (!_connectStatus.equals("error")) {
			_wsNachrichtenListData
					.add("HumanClientID (" + _userConfig.getHumanClientID() + ") SessionID(-) geschlossen.");
			HumanFxUtil.updateStatusLabel(_label,
					"HumanClientID (" + _userConfig.getHumanClientID() + ") SessionID(" + _connectStatus + ")");
		} else {
			_wsNachrichtenListData.add("HumanClientID (" + _userConfig.getHumanClientID()
					+ ") SessionID(-) konnte nicht geschlossen werden.");
		}

		HumanFxUtil.updateStatusLabel(_label,
				"HumanClientID (" + _userConfig.getHumanClientID() + ") SessionID(" + _connectStatus + ")");

		HumanFxUtil.setWebSocketStatus(_rbtn, _hWSCC.isConnected());
	}
}
