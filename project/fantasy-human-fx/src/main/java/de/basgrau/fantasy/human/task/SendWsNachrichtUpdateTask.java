package de.basgrau.fantasy.human.task;

import de.basgrau.fantasy.human.fx.values.MessageConstants;
import de.basgrau.fantasy.human.fx.websocket.HerkulesWebSocketClientConnector;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * SendWsNachrichtUpdateTask.
 * 
 * @author basgrau
 *
 */
public class SendWsNachrichtUpdateTask extends Task<String> {

	private ObservableList<String> _wsNachrichtenListData;
	private String _status = "error";
	private HerkulesWebSocketClientConnector _hWSCC;
	private String _text;

	/**
	 * SendWsNachrichtUpdateTask.
	 * 
	 * @param wsNachrichtenListData List
	 */
	public SendWsNachrichtUpdateTask(ObservableList<String> wsNachrichtenListData, String text, HerkulesWebSocketClientConnector hWSCC) {
		this._wsNachrichtenListData = wsNachrichtenListData;
		this._hWSCC = hWSCC;
		this._text = text;
	}

	@Override
	protected String call() throws Exception {
		if (_hWSCC != null && _hWSCC.isConnected()) {
			_status = _hWSCC.sendMessage(MessageConstants.TEST_WS_MESSAGE);
		}

		return _status;
	}

	@Override
	protected void succeeded() {
		if ("error".equals(_status)) {
			return;
		}
		
		if("".equals(_text)) {
			_text  = MessageConstants.TEST_WS_MESSAGE ;
		}

		_wsNachrichtenListData.add(_text + "(Status:" + _status + ")");
	}
}
