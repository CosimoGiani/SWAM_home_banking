package rest.service;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import otp.OTPAuthenticated;
import rest.controller.CardController;
import utils.ParserJson;

@Path("card")
public class CardService {
	
	@Inject
	private CardController cardController;
	
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("block")
	@OTPAuthenticated
	public Response blockCard(@HeaderParam("Authorization") String authorization, String json_id) {
		String[] split = authorization.split(" ");
	    final String email = split[0];
	    try {
			Map<String, String> idData = ParserJson.fromString(json_id);
			Long card_id = Long.parseLong(idData.get("card_id"));
			if (cardController.userOwnsCard(email, card_id)) {
				boolean cardBlocked = cardController.blockCard(card_id);
				if(cardBlocked)
					return Response.accepted("Carta bloccata con successo").build();
				else
					return Response.notModified("Qualcosa è andato storto").build();
			}
			return Response.status(403).build(); // in questo caso l'utente autenticato non possiede la carta
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(400).build(); 
		}
	}
}
