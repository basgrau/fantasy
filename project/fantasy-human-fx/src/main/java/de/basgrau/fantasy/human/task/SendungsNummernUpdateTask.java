package de.basgrau.fantasy.human.task;

import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.basgrau.fantasy.human.fx.util.HumanFxUtil;
import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.hermes.status.Status;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * SendungsNummernUpdateTask.
 * 
 * @author basgrau
 *
 */
public class SendungsNummernUpdateTask extends Task<String> {

	private ObservableList<String> _sendungsNummerList;
	private String _sendungsnummer;
	private String _statusText;
	private String _humanId = "";
	
	/**
	 * SendungsNummernUpdateTask.
	 * @param sendungsNummerList List
	 * @param sendungsnummer Nr
	 * @param humanId Id
	 */
	public SendungsNummernUpdateTask(ObservableList<String> sendungsNummerList, String sendungsnummer, String humanId) {
		this._sendungsNummerList = sendungsNummerList;
		this._sendungsnummer = sendungsnummer;
		this._humanId = humanId;
	}

	@Override
	protected String call() throws Exception {
		if("".equals(_sendungsnummer)) {
			return "error";
		}
		
		Client client = ClientBuilder.newClient();
		WebTarget target = client
				.target(SharedValues.UNTERWELT_STATUS_CHECK_REST_API_URL + "/" + _humanId + "/" + _sendungsnummer);
		Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
		Response response = builder.get();

		String status = "";
		if (response.getStatus() == 200) {
			status = HumanFxUtil.readInputStreamAsString((InputStream) response.getEntity());
		}
		_statusText = "kein Info";
		switch (status) {
		case Status.OK + "":
			_statusText = "Sendung wurde erledigt";
			break;
		case Status.NOK + "":
			_statusText = "Sendung wurde noch nicht erledigt";
			break;
		case Status.NOTFOUND + "":
			_statusText = "Sendung wurde nicht gefunden";
			break;
		default:
			break;
		}

		return _statusText;
	}

	@Override
	protected void succeeded() {
		if(_statusText.equals("error")) {
			return;
		}
		_sendungsNummerList.add("(S-Nr: " + _sendungsnummer + ") " + _statusText);
	}
}
