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
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * AlleSendungenUpdateTask.
 * @author basgrau
 *
 */
public class AlleSendungenUpdateTask extends Task<String> {

	private ObservableList<String> _alleSendungenListData;
	private String _status = "";
	private String _humanId = "";

	/**
	 * AlleSendungenUpdateTask.
	 * @param alleSendungenListData List
	 * @param humanId Id
	 */
	public AlleSendungenUpdateTask(ObservableList<String> alleSendungenListData, String humanId) {
		this._alleSendungenListData = alleSendungenListData;
		this._humanId = humanId;
	}

	@Override
	protected String call() throws Exception {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(SharedValues.UNTERWELT_SENDUNGEN_ALLE_REST_API_URL + "/" + _humanId);
		Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
		Response response = builder.get();

		if (response.getStatus() == 200) {
			_status = HumanFxUtil.readInputStreamAsString((InputStream) response.getEntity());
		} else {
			System.err.println("ResponseStatus: "+response.getStatus());
		}
		
		return _status;
	}

	@Override
	protected void succeeded() {
		_alleSendungenListData.clear();

		if(!_status.contains(";")) {
			return;
		}
		
		String[] sendungen = _status.split(";");

		for (int i = 0; i < sendungen.length; i++) {
			String[] sendung = sendungen[i].split(",");
			_alleSendungenListData.add("(S-Nr: " + sendung[0] + ") " + sendung[1]);
		}
	}
}
