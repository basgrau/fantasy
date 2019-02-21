package de.basgrau.herkules.api;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.basgrau.herkules.zeusconnect.ZeusConnectionClient;
import de.basgrau.hermes.shared.GoetterNachricht;
import de.basgrau.hermes.shared.SharedValues;

@Path(SharedValues.HERKULES_API_PATH)
public class HerkulesApi {
	
	@GET
	@Path("hello")
	@Produces("text/html")
	public String pojo() {
		return "pojo ok @ " + new Date().toString();
	}
	
	@POST
	@Path("sendenNachricht")
	@Consumes({MediaType.APPLICATION_JSON})
	@Produces(MediaType.TEXT_PLAIN)
	public Response sendenNachricht(GoetterNachricht nachricht) {
		if(nachricht == null)
			return Response.noContent().build();
		
		int status = new ZeusConnectionClient().sendToZeus(nachricht.getSendungsnummer(), nachricht.getHumanClientId(), nachricht.getText());  
		
		System.out.println("Sendung: "+nachricht.getSendungsnummer() + " mit Status: "+status+" erhalten!");
		
		return Response.ok(""+status).build();
	}
}
