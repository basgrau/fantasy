package de.basgrau.herkules.hadesconnect;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.basgrau.hermes.shared.SharedValues;

/**
 * UnterweltApiClient, nimmer Kontakt auf mit der Hades API.
 * 
 * @author basgrau
 *
 */
public class UnterweltApiClient {
	/** . */
	private Client client = ClientBuilder.newClient();

	/**
	 * getStatus.
	 * 
	 * @param hid HumanId
	 * @param sid Sendungsnummer
	 * @return Status
	 */
	public String getStatus(String hid, String sid) {
		String status = "";
		WebTarget target = client.target(SharedValues.UNTERWELT_STATUS_CHECK_REST_API_URL + "/" + hid + "/" + sid);
		Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
		Response response = builder.get();

		if (response.getStatus() == 200) {
			status = readInputStreamAsString((InputStream) response.getEntity());
		}

		return status;
	}

	/**
	 * deleteSendung.
	 * 
	 * @param hid HumanId
	 * @param sid Sendungsnummer
	 * @return Status
	 */
	public String deleteSendung(String hid, String sid) {
		String status = "";
		WebTarget target = client.target(SharedValues.UNTERWELT_DELETE_SENDUNG_REST_API_URL + "/" + hid + "/" + sid);
		Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
		Response response = builder.get();

		if (response.getStatus() == 200) {
			status = readInputStreamAsString((InputStream) response.getEntity());
		}

		return status;
	}
	
	/**
	 * getAlleSendungen.
	 * @param hid humanId
	 * @return Sendungen Array
	 */
	public String[] getAlleSendungen(String hid) {
		WebTarget target = client.target(SharedValues.UNTERWELT_SENDUNGEN_ALLE_REST_API_URL + "/" + hid);
		Invocation.Builder builder = target.request(MediaType.TEXT_PLAIN);
		Response response = builder.get();

		if (response.getStatus() == 200) {
			String result = readInputStreamAsString((InputStream) response.getEntity());
			if (!result.contains(";")) {
				return null;
			}

			String[] sendungen = result.split(";");

			String[] nummern = new String[sendungen.length];
			int pos = 0;
			for (String s : sendungen) {
				nummern[pos] = s.split(",")[0];
				pos++;
			}
			return nummern;
		}

		return null;
	}

	/**
	 * readInputStreamAsString.
	 * 
	 * @param in InputStream, die Entity aus der Response.
	 * @return Satz
	 */
	private String readInputStreamAsString(InputStream in) {
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

}
