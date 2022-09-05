package rest.service;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ConsultantAuthentication.AuthenticatedConsultant;
import model.BankAccount;
import model.Transaction;
import model.User;
import model.enumeration.BankAccountType;
import model.enumeration.CardType;
import rest.controller.ConsultantController;
import utils.ParserJson;

@Path("consultant")
public class ConsultantService {
	
	@Inject
	private ConsultantController controller;
	
	@GET
	@Path("users")
	@Produces(MediaType.APPLICATION_JSON)
	@AuthenticatedConsultant
	public List<User> getAssociatedUsers(@HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			return controller.getAssociatedUsers(identificationNumber, true);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@GET
	@Path("user/details")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@AuthenticatedConsultant
	public User getUserDetails(String userIdJson, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> idData = ParserJson.fromString(userIdJson);
			Long userId = Long.parseLong(idData.get("userId"));
			
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
	@AuthenticatedConsultant
	public List<BankAccount> getUserBankAccounts(String userIdJson, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> idData = ParserJson.fromString(userIdJson);
			Long userId = Long.parseLong(idData.get("userId"));
			
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
	@AuthenticatedConsultant
	public Response modifyUserBankAccountType(String requestIds, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> data = ParserJson.fromString(requestIds);
			Long userId = Long.parseLong(data.get("userId"));
			Long accountId = Long.parseLong(data.get("accountId"));
			
			String typeToConvert = data.get("accountType");
			BankAccountType type;
			
			if (typeToConvert.equals("ORDINARIO")) {
				type = BankAccountType.ORDINARIO;
			} else if (typeToConvert.equals("UNDER30")) {
				type = BankAccountType.UNDER30;
			} else if (typeToConvert.equals("INVESTITORE")) {
				type = BankAccountType.INVESTITORE;
			} else {
				return Response.notAcceptable(null).entity("Impossibile modificare il tipo del conto: tipo non valido").build();
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
	@AuthenticatedConsultant
	public List<Transaction> getBankAccountTransactions(String requestIds, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> data = ParserJson.fromString(requestIds);
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
	@AuthenticatedConsultant
	public Float getBankAccountBalance(String requestIds, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> data = ParserJson.fromString(requestIds);
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
	@AuthenticatedConsultant
	public Response addUserCard(String requestData, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> data = ParserJson.fromString(requestData);
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
			} else if (cardTypeToConvert.equals("RICARICABILE")){
				cardType = CardType.RICARICABILE;
			} else {
				return Response.notAcceptable(null).entity("Carta non aggiunta: la tipologia della carta non è corretta").build();
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
	
	@DELETE
	@Path("user/cards/remove-card")
	@Consumes(MediaType.APPLICATION_JSON)
	@AuthenticatedConsultant
	public Response removeUserCard(String requestIds, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> data = ParserJson.fromString(requestIds);
			Long userId = Long.parseLong(data.get("userId"));
			Long accountId = Long.parseLong(data.get("accountId"));
			Long cardId = Long.parseLong(data.get("cardId"));
			
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				if (controller.getBankAccountOwnedByUser(accountId, userId) != null) {
					controller.removeCard(cardId);
					return Response.ok().entity("Carta rimossa con successo").build();
				} else return Response.notAcceptable(null).entity("Carta non rimossa").build();
			} else return Response.notAcceptable(null).entity("Carta non rimossa").build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Carta non rimossa").build();
		}
	}
	
	@PATCH
	@Path("user/cards/update-card-massimale")
	@Consumes(MediaType.APPLICATION_JSON)
	@AuthenticatedConsultant
	public Response updateMassimaleUserCard(String requestData, @HeaderParam("Authorization") String authorization) {
		try {
			String[] split = authorization.split(" ");
			String identificationNumber = split[0];
			
			Long consultantId = controller.getConsultantIdFromIdNumber(identificationNumber);
			
			Map<String, String> data = ParserJson.fromString(requestData);
			Long userId = Long.parseLong(data.get("userId"));
			Long accountId = Long.parseLong(data.get("accountId"));
			Long cardId = Long.parseLong(data.get("cardId"));
			
			Float massimale = Float.parseFloat(data.get("massimale"));
			
			if (controller.checkUserIsAssociated(consultantId, userId)) {
				if (controller.getBankAccountOwnedByUser(accountId, userId) != null) {
					controller.updateMassimale(cardId, massimale);
					return Response.ok().entity("Il massimale della carta è stato cambiato con successo").build();
				} else {
					return Response.notAcceptable(null).entity("Non è stato possibile cambiare il massimale").build();
				}
			} else {
				return Response.notAcceptable(null).entity("Non è stato possibile cambiare il massimale").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.notAcceptable(null).entity("Non è stato possibile cambiare il massimale").build();
		}
	}

}
