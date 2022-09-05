package rest.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Card;
import model.Transaction;
// import otp.OTPAuthenticated;
import otpStateful.OTPAuthenticatedStateful;
import rest.controller.BankAccountController;
import utils.ParserJson;

@Path("account")
public class BankAccountService {
	
	@Inject
	private BankAccountController controller;
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("transactions")
	//@OTPAuthenticated
	@OTPAuthenticatedStateful
	public List<Transaction> getBankAccountTransactions(@HeaderParam("Authorization") String authorization, String id) {
		String[] split = authorization.split(" ");
	    final String email = split[0];
		try {
			Map<String, String> idData = ParserJson.fromString(id);
			Long idValue = Long.parseLong(idData.get("id"));
			if (controller.userOwnsBankAccount(email, idValue)) {
				List<Transaction> transactions = controller.getBankAccountTransactions(idValue);
				return transactions;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("cards")
	//@OTPAuthenticated
	@OTPAuthenticatedStateful
	public List<Card> getBankAccountCards(@HeaderParam("Authorization") String authorization, String id) {
		String[] split = authorization.split(" ");
	    final String email = split[0];
		try {
			Map<String, String> idData = ParserJson.fromString(id);
			Long idValue = Long.parseLong(idData.get("id"));
			if (controller.userOwnsBankAccount(email, idValue)) {
				List<Card> transactions = controller.getBankAccountCards(idValue);
				return transactions;
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@DELETE
	@Path("delete-account")
	@Consumes(MediaType.APPLICATION_JSON)
	//@OTPAuthenticated
	@OTPAuthenticatedStateful
	public Response deleteBankAccount(String id) {
		
		try {
			Map<String, String> idData = ParserJson.fromString(id);
			Long idValue = Long.parseLong(idData.get("id"));
			controller.deleteBankAccount(idValue);
			return Response.ok().entity("Conto corrente chiuso con successo").build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Impossibile chiudere il conto").build();
		}
		
	}

}
