package rest.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.BankAccount;
import model.Card;
import model.Consultant;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;
import rest.controller.ConsultantController;
import utils.ParserJson;

@Path("consultant")
public class ConsultantService {
	
	// TODO aggiornare le richieste con header corretto quando faremo l'etichetta nuova
	
	@Inject
	private ConsultantController controller;
	
	@GET
	@Path("users")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAssociatedUsers(String identificationNumber) {
		try {
			Map<String, String> idData = ParserJson.fromString(identificationNumber);
			return controller.getAssociatedUsers(idData.get("identificationNumber"), true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@GET
	@Path("user/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public User getUserDetails(String ids) {
		try {
			Map<String, String> idData = ParserJson.fromString(ids);
			Long consultantId = controller.getConsultantIdFromIdNumber(idData.get("identificationNumber"));
			Long userId = Long.parseLong(idData.get("id"));
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				return controller.getUserDetails(userId, true);
			} else return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@GET
	@Path("user/accounts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<BankAccount> getUserBankAccounts(String ids) {
		try {
			Map<String, String> idData = ParserJson.fromString(ids);
			Long consultantId = controller.getConsultantIdFromIdNumber(idData.get("identificationNumber"));
			Long userId = Long.parseLong(idData.get("id"));
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				return controller.getUserBankAccounts(userId);
			} else return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@PATCH
	@Path("user/modify-account-type")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response modifyUserBankAccountType(String request) {
		try {
			Map<String, String> data = ParserJson.fromString(request);
			Long consultantId = controller.getConsultantIdFromIdNumber(data.get("identificationNumber"));
			Long userId = Long.parseLong(data.get("userId"));
			Long accountId = Long.parseLong(data.get("accountId"));
			String typeToConvert = data.get("accountType");
			BankAccountType type;
			if (typeToConvert.equals("ORDINARIO")) {
				type = BankAccountType.ORDINARIO;
			} else if (typeToConvert.equals("UNDER30")) {
				type = BankAccountType.UNDER30;
			} else {
				type = BankAccountType.INVESTITORE;
			}
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				BankAccount account = controller.getBankAccountOwnedByUser(accountId, userId);
				if (!account.getType().equals(type)) {
					controller.modifyAccountType(type, accountId);
					return Response.ok().entity("Il tipo del conto corrente è stato cambiato con successo").build();
				} else {
					return Response.notAcceptable(null).entity("Il tipo del conto è già " + typeToConvert).build();
				}
			} else {
				return Response.notAcceptable(null).entity("Consulente non è referente dell'utente in questione").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Impossibile modificare il tipo del conto").build();
		}
	}
	
	@GET
	@Path("user/account/transactions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Transaction> geBankAccountTransactions(String request) {
		try {
			Map<String, String> data = ParserJson.fromString(request);
			Long consultantId = controller.getConsultantIdFromIdNumber(data.get("identificationNumber"));
			Long userId = Long.parseLong(data.get("userId"));
			Long accountId = Long.parseLong(data.get("accountId"));
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				if (controller.getBankAccountOwnedByUser(accountId, userId) != null) {
					List<Transaction> transactions = controller.getBankAccountTransactions(accountId);
					return transactions;
				} else return null;
			} else return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
	
	@GET
	@Path("user/account/balance")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Float geBankAccountBalance(String request) {
		try {
			Map<String, String> data = ParserJson.fromString(request);
			Long consultantId = controller.getConsultantIdFromIdNumber(data.get("identificationNumber"));
			Long userId = Long.parseLong(data.get("userId"));
			Long accountId = Long.parseLong(data.get("accountId"));
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				BankAccount account = controller.getBankAccountOwnedByUser(accountId, userId);
				return account.getBalance();
			} else return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@POST
	@Path("user/cards/add-card")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addUserCard(String request) {
		try {
			Map<String, String> data = ParserJson.fromString(request);
			Long consultantId = controller.getConsultantIdFromIdNumber(data.get("identificationNumber"));
			Long userId = Long.parseLong(data.get("userId"));
			Long accountId = Long.parseLong(data.get("accountId"));
			String cardNumber = data.get("cardNumber");
			Float massimale = Float.parseFloat(data.get("massimale"));
			String cardTypeToConvert = data.get("cardType");
			CardType cardType;
			if (cardTypeToConvert.equals("CREDITO")) {
				cardType = CardType.CREDITO;
			} else if (cardTypeToConvert.equals("DEBITO")) {
				cardType = CardType.DEBITO;
			} else {
				cardType = CardType.RICARICABILE;
			}
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				BankAccount account = controller.getBankAccountLazy(accountId, userId);
				controller.addNewCard(account, cardNumber, massimale, cardType);
				return Response.ok().entity("Carta aggiunta con successo").build();
			} else return Response.notAcceptable(null).entity("Carta non aggiunta").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Carta non aggiunta").build();
		}
	}

}
