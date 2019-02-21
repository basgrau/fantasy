package de.basgrau.hades.api;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.basgrau.hades.status.StatusSingleton;
import de.basgrau.hermes.shared.SharedValues;
import de.basgrau.hermes.status.Status;

/**
 * HadesUnterweltAPI, ReST API zum Abfragen des Status.
 * @author basgrau
 *
 */
@Path(SharedValues.UNTERWELT_API_PATH)
@Stateless
public class HadesUnterweltAPI {

	@EJB
	private StatusSingleton _storage;
	
	@GET
	@Path("hello")
	@Produces("text/html")
	public String pojo() {
		return "pojo ok @ " + new Date().toString();
	}

	/**
	 * getVerarbeitungsStatus.
	 * @param hid HumanId
	 * @param sid Sendungsnummer
	 * @return Status
	 */
	@GET
	@Path("status/{hid}/{sid}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getVerarbeitungsStatus(@PathParam("hid")String hid, @PathParam("sid")String sid) {
		try {
			int verarbeitungsStatus = _storage.getVerarbeitungsStatus(hid, sid);
			return Response.ok(""+verarbeitungsStatus).build();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return Response.noContent().build();
		}
	}
	
	@GET
	@Path("delete/{hid}/{sid}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response removeSendung(@PathParam("hid")String hid, @PathParam("sid")String sid) {
		try {
			_storage.delete(hid, sid);
			return Response.ok(Status.OK).build();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return Response.noContent().build();
		}
	}
	
	/**
	 * 
	 * @return sendungen
	 */
	@GET
	@Path("sendungen/{hid}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getSendungen(@PathParam("hid")String hid) {
		if( _storage == null) {
			System.err.println("Storage ist null.");
			return Response.noContent().build();
		}else {
			ConcurrentHashMap<String, Integer> sendungen = _storage.getSendungen(hid);
			
			if(sendungen == null) {
				System.err.println("Keine Sendungen zu " + hid);
				return Response.noContent().build();
			}
			
			StringBuilder sb = new StringBuilder();
			for (String key : sendungen.keySet()) {
				Integer status = sendungen.get(key);
				sb.append(key+","+status+";");
			}
			
			String status = sb.toString();
			
			if("".equals(status)) {
				System.err.println("Keinen Status zu Sendungen zu " + hid);
				return Response.noContent().build(); 
			}
			
			status = status.substring(0,status.lastIndexOf(";"));
			
			return Response.ok(status).build();
		}
	}
}