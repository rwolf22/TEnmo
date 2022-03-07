package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.text.NumberFormat;
import java.util.Objects;

public class App {

	private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

	private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
	
    private AuthenticatedUser currentUser;
	private Account currentUserAccount;
    private ConsoleService console;
    private AuthenticationService authenticationService;
	private AccountService accountService;
	private TransferService transferService;
	private UserService userService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out),
				new AuthenticationService(API_BASE_URL),
				new AccountService(API_BASE_URL),
				new TransferService(API_BASE_URL),
				new UserService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console,
			   AuthenticationService authenticationService,
			   AccountService accountService,
			   TransferService transferService,
			   UserService userService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.transferService = transferService;
		this.userService = userService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
		double balance = accountService.getBalance(currentUser.getUser().getId(), currentUserAccount.getAccountId());
		System.out.println("Your current account balance is: " + CURRENCY_FORMAT.format(balance));
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub
		Transfer[] transfers = transferService.list(currentUser.getUser().getId(), currentUserAccount.getAccountId());
		System.out.println("-------------------------------------------");
		System.out.println("Transfers");
		System.out.println("ID          From/To                 Amount");
		System.out.println("-------------------------------------------");
		for (Transfer transfer: transfers) {
			String fromTo;
			String username;
			if (transfer.getAccountFrom() == currentUserAccount.getAccountId()) {
				fromTo = "To:   ";
				username = userService.getUsername(accountService.getUserIdByAccountId(transfer.getAccountTo()));
			}
			else {
				fromTo = "From: ";
				username = userService.getUsername(accountService.getUserIdByAccountId(transfer.getAccountFrom()));
			}
			System.out.println(transfer.getTransferId() + "        " + fromTo + username + "          $ " + CURRENCY_FORMAT.format(transfer.getAmount()));
		}
		System.out.println("---------");
		int input = console.getUserInputInteger("Please enter transfer ID to view details (0 to cancel): ");
		if (input != 0) {
			viewTransferDetails(input);
		}
	}

	private void viewTransferDetails(int transferId) {
		Transfer transfer = transferService.getTransfer(currentUser.getUser().getId(), currentUserAccount.getAccountId(), transferId);
		System.out.println("--------------------------------------------");
		System.out.println("Transfer Details");
		System.out.println("--------------------------------------------");
		System.out.println(transfer.toString(currentUser.getToken()));

	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		Transfer[] transfers = new Transfer[0];
		if (transferService.getPending(currentUser.getUser().getId(), currentUserAccount.getAccountId()).length > 0) {
			transfers = transferService.getPending(currentUser.getUser().getId(), currentUserAccount.getAccountId());
		}
		System.out.println("-------------------------------------------");
		System.out.println("Pending Transfers");
		System.out.println("ID          To                     Amount");
		System.out.println("-------------------------------------------");
		for (Transfer transfer: transfers) {
			String username = userService.getUsername(accountService.getUserIdByAccountId(transfer.getAccountTo()));
			System.out.println(transfer.getTransferId() + "        " + username + "                $ " + CURRENCY_FORMAT.format(transfer.getAmount()));
		}
		int input = -1;
		boolean valid = false;
		while (!valid) {
			System.out.println("---------");
			input = console.getUserInputInteger("Please enter transfer ID to approve/reject (0 to cancel): ");
			if (input == 0) {
				valid = true;
			}
			else {
				for (Transfer transfer: transfers) {
					if (input == transfer.getTransferId()) {
						valid = true;
						break;
					}
				}
			}
		}
		if (input != 0) {
			approveReject(input);
		}
	}

	private void approveReject(int transferId) {
		int choice = -1;
		System.out.println("1: Approve\n2: Reject\n0: Don't approve or reject");
		while (choice > 2 || choice < 0) {
			System.out.println("---------");
			choice = console.getUserInputInteger("Please choose an option: ");
		}
		if (choice == 1) {
			if (transferService.getTransfer(currentUser.getUser().getId(), currentUserAccount.getAccountId(), transferId).getAmount() > currentUserAccount.getBalance()) {
				System.out.println("Insufficient account balance");
				transferService.rejectTransfer(currentUser.getUser().getId(), currentUserAccount.getAccountId(), transferId);
				return;
			}
			if (transferService.approveTransfer(currentUser.getUser().getId(), currentUserAccount.getAccountId(), transferId)) {
				System.out.println("Transfer completed");
				currentUserAccount = accountService.getAccountByUser(currentUser.getUser().getId());
			}
			else {
				System.out.println("Unable to complete transfer");
			}
		}
		else if (choice == 2) {
			if (transferService.rejectTransfer(currentUser.getUser().getId(), currentUserAccount.getAccountId(), transferId)) {
				System.out.println("Transfer rejected");
			}
			else {
				System.out.println("Unable to reject transfer");
			}
		}
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
		User[] users = userService.list();
		System.out.println("-------------------------------------------\n" +
				"Users\n" +
				"ID          Name\n" +
				"-------------------------------------------");
		for (User user: users) {
			if (!Objects.equals(user.getId(), currentUser.getUser().getId())) {
				System.out.println(user.getId() + "        " + user.getUsername());
			}
		}
		boolean validId = false;
		int userTo = -1;
		double amount = 0;
		while(!validId) {
			System.out.println("---------\n");
			userTo = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel): ");
			if (userTo == 0) {
				return;
			}
			else {
				for (User user : users) {
					if (userTo == user.getId()) {
						validId = true;
						break;
					}
				}
			}
		}
		while(amount <= 0) {
			amount = console.getUserInputInteger("Enter amount: ");
		}
		if (amount > currentUserAccount.getBalance()) {
			System.out.println("Insufficient account balance.");
			return;
		}
		Transfer newTransfer = new Transfer(2, 1, currentUserAccount.getAccountId(), accountService.getIdByUser(userTo), amount);
		Transfer returnedTransfer = transferService.createTransfer(newTransfer, currentUser.getUser().getId(), currentUserAccount.getAccountId());
		if (returnedTransfer != null) {
			transferService.approveTransfer(currentUser.getUser().getId(), currentUserAccount.getAccountId(), returnedTransfer.getTransferId());
			System.out.println("Transfer sent");
		}
		else {
			System.out.println("Unable to send transfer");
		}
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		User[] users = userService.list();
		System.out.println("-------------------------------------------\n" +
				"Users\n" +
				"ID          Name\n" +
				"-------------------------------------------");
		for (User user: users) {
			if (!Objects.equals(user.getId(), currentUser.getUser().getId())) {
				System.out.println(user.getId() + "        " + user.getUsername());
			}
		}
		boolean validId = false;
		int userFrom = -1;
		double amount = 0;
		while(!validId) {
			System.out.println("---------\n");
			userFrom = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel): ");
			if (userFrom == 0) {
				return;
			}
			else {
				for (User user : users) {
					if (userFrom == user.getId()) {
						validId = true;
						break;
					}
				}
			}
		}
		while(amount <= 0) {
			amount = console.getUserInputInteger("Enter amount: ");
		}
		Transfer newTransfer = new Transfer(1, 1, accountService.getIdByUser(userFrom), currentUserAccount.getAccountId(), amount);
		if (transferService.createTransfer(newTransfer, currentUser.getUser().getId(), currentUserAccount.getAccountId()) != null) {
			System.out.println("Transfer created");
		}
		else {
			System.out.println("Unable to create transfer");
		}
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
				accountService.setAuthToken(currentUser.getToken());
				transferService.setAuthToken(currentUser.getToken());
				userService.setAuthToken(currentUser.getToken());
				currentUserAccount = accountService.getAccountByUser(currentUser.getUser().getId());
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
}
