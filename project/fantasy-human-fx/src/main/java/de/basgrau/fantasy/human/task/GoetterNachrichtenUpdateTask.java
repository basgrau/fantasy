package de.basgrau.fantasy.human.task;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import de.basgrau.fantasy.human.fx.util.HumanFxUtil;
import de.basgrau.hermes.shared.GoetterNachricht;
import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.hermes.shared.UserConfig;
import de.basgrau.hermes.status.Status;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * GoetterNachrichtenUpdateTask.
 * @author basgrau
 *
 */
public class GoetterNachrichtenUpdateTask extends Task<String> {

	/** . */
	private ObservableList<String> _goetterNachrichtenList;
	/** . */
	private UserConfig _userConfig;
	/** . */
	private String _nachrichtenText;
	/** . */
	private String _status = "error";
	/** . */
	private GoetterNachricht _goetterNachricht;
	/** . */
	private String _statusText = "kein Info";
	
	/**
	 * GoetterNachrichtenUpdateTask.
	 * @param goetterNachrichtenList List
	 */
	public GoetterNachrichtenUpdateTask(UserConfig userConfig, String nachrichtenText, ObservableList<String> goetterNachrichtenList) {
		this._goetterNachrichtenList = goetterNachrichtenList;
		this._userConfig = userConfig;
		this._nachrichtenText = nachrichtenText;
		System.out.println(_nachrichtenText);
	}

	@Override
	protected String call() throws Exception {
		System.out.println(_nachrichtenText);
		if ("".equals(_nachrichtenText)) {
			return "error";
		}
		_goetterNachricht = new GoetterNachricht();
		_goetterNachricht.setHumanClientId(_userConfig.getHumanClientID());
		_goetterNachricht.setSendungsnummer("SDNR" + System.currentTimeMillis() / 1000);
		_goetterNachricht.setText(_nachrichtenText);

		HumanFxUtil.testObjectToJson(_goetterNachricht);

		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(SharedValues.HERKULES_REST_API_URL);

			_status = target.request(MediaType.APPLICATION_JSON).accept(MediaType.TEXT_PLAIN)
					.post(Entity.entity(_goetterNachricht, MediaType.APPLICATION_JSON), String.class);
		} catch (Exception e) {
			_statusText = e.getMessage();
		}
		return _status;
	}

	@Override
	protected void succeeded() {
		switch (_status) {
		case Status.OK + "":
			_statusText = "Sendung wurde erledigt";
			break;
		case Status.NOK + "":
			_statusText = "Sendung wurde noch nicht erledigt";
			break;
		case Status.NOTFOUND + "":
			_statusText = "Sendung wurde nicht gefunden";
			break;
		case Status.ANGENOMMEN + "":
			_userConfig.setSendungsNummern(
					_userConfig.getSendungsNummern() + ";" + _goetterNachricht.getSendungsnummer());
			_statusText = "Sendung wurde angenommen";
			break;
		default:
			break;
		}

		_goetterNachrichtenList
				.add("(S-Nr: " + _goetterNachricht.getSendungsnummer() + ") " + _statusText);
		
	}
}
