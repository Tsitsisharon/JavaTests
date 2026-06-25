/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.chatapppoe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author tsits
 */
public class ChatAppPOE {
    
        // Arrays for Part 3
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> disregardedMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    private static List<String> messageHashes = new ArrayList<>();
    private static List<String> messageIDs = new ArrayList<>();


    public static void main(String[] args) {
        System.out.println("Hello World!");
        
        Scanner scanner = new Scanner(System.in);
        Login login = new Login();
        
        // ----- Registration and Login -----
        System.out.println("=== QuickChat Registration ===");
        System.out.print("First name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last name: ");
        String lastName = scanner.nextLine();
        System.out.print("Username (underscore, max 5 chars): ");
        String username = scanner.nextLine();
        System.out.print("Password (min 8 chars, 1 capital, 1 number, 1 special): ");
        String password = scanner.nextLine();
        System.out.print("Cell phone (+27...): ");
        String cell = scanner.nextLine();
        
        String regMsg = login.registerUser(username, password, cell, firstName, lastName);
        System.out.println(regMsg);
        if (!regMsg.equals("User successfully registered.")) {
            System.out.println("Registration failed. Exiting.");
            return;
        }
        
        System.out.println("\n=== Please Login ===");
        System.out.print("Username: ");
        String loginUser = scanner.nextLine();
        System.out.print("Password: ");
        String loginPass = scanner.nextLine();
        String loginStatus = login.returnLoginStatus(loginUser, loginPass);
        System.out.println(loginStatus);
        if (!loginStatus.startsWith("Welcome")) {
            System.out.println("Login failed. Exiting.");
            return;
        }
        
        // Get sender name (same as logged-in user)
        String senderName = login.getFirstName() + " " + login.getLastName();
        
        // ----- Load existing messages from JSON -----
        loadMessagesFromJSON();
        
        // ----- Messaging Part -----
        System.out.println("\nWelcome to QuickChat");
        System.out.print("How many messages do you wish to send today? ");
        int totalMessagesToSend = scanner.nextInt();
        scanner.nextLine();
        
        Message msgHandler = new Message();
        int messagesCreated = 0;
        boolean running = true;
        
        
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Send a message");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Quit");
            System.out.println("4. Stored Messages Management");
            System.out.print("Choose option: ");
            int option = scanner.nextInt();
            scanner.nextLine();
            
            switch (option) {
                case 1:
                    Message newMsg = msgHandler.createNewMessage(scanner, messagesCreated, senderName);
                    if (newMsg != null) {
                        // Add to appropriate array based on status
                        addMessageToArray(newMsg);
                        
                        System.out.println("\n--- Message Details ---");
                        System.out.println("Message ID: " + newMsg.getMessageID());
                        System.out.println("Message Hash: " + newMsg.getMessageHash());
                        System.out.println("Recipient: " + newMsg.getRecipient());
                        System.out.println("Message: " + newMsg.getMessageText());
                        messagesCreated++;
                    }
                    break;
                case 2:
                    // Show sent messages
                    System.out.println(msgHandler.printMessages(sentMessages));
                    break; 
                case 3:
                    System.out.println("Exiting QuickChat.");
                    System.out.println("Total messages sent this session: " + sentMessages.size());
                    scanner.close();
                    return;
                case 4:
                    storedMessagesMenu(scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        
        System.out.println("\nAll " + totalMessagesToSend + " messages have been processed.");
        System.out.println("Total messages actually sent: " + sentMessages.size());
        
        scanner.close();
    }
    
    // Load messages from JSON file
    private static void loadMessagesFromJSON() {
        List<Message> allMessages = Message.readMessagesFromJSON();
        for (Message msg : allMessages) {
            addMessageToArray(msg);
        }
        System.out.println("Loaded " + allMessages.size() + " messages from storage.");
    }
    
    // Add message to appropriate array
    private static void addMessageToArray(Message msg) {
        switch (msg.getStatus()) {
            case SENT:
                sentMessages.add(msg);
                break;
            case STORED:
                storedMessages.add(msg);
                break;
            case DISREGARDED:
                disregardedMessages.add(msg);
                break;
        }
        messageHashes.add(msg.getMessageHash());
        messageIDs.add(msg.getMessageID());
    }
    
    // Stored Messages Menu (Part 3 features)
    private static void storedMessagesMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Stored Messages Management ---");
            System.out.println("a. Display all stored messages (sender & recipient)");
            System.out.println("b. Display the longest stored message");
            System.out.println("c. Search for a message by ID");
            System.out.println("d. Search for all messages by recipient");
            System.out.println("e. Delete a message using message hash");
            System.out.println("f. Display full report of all stored messages");
            System.out.println("q. Return to main menu");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().toLowerCase();
            
            switch (choice) {
                case "a":
                    displaySenderAndRecipient();
                    break;
                case "b":
                    displayLongestMessage();
                    break;
                case "c":
                    searchByMessageID(scanner);
                    break;
                case "d":
                    searchByRecipient(scanner);
                    break;
                case "e":
                    deleteByMessageHash(scanner);
                    break;
                case "f":
                    displayFullReport();
                    break;
                case "q":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    // a. Display sender and recipient of all stored messages
    private static void displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        System.out.println("\n--- Stored Messages: Sender & Recipient ---");
        for (Message msg : storedMessages) {
            System.out.println("Sender: " + msg.getSender() + " | Recipient: " + msg.getRecipient());
        }
    }
    
    // b. Display the longest stored message
    private static void displayLongestMessage() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        Message longest = storedMessages.stream()
            .max(Comparator.comparingInt(m -> m.getMessageText().length()))
            .orElse(null);
        if (longest != null) {
            System.out.println("\n--- Longest Stored Message ---");
            System.out.println("Message: " + longest.getMessageText());
            System.out.println("Length: " + longest.getMessageText().length() + " characters");
            System.out.println("Recipient: " + longest.getRecipient());
        }
    }
    
    // c. Search for a message by ID
    private static void searchByMessageID(Scanner scanner) {
        System.out.print("Enter Message ID to search: ");
        String id = scanner.nextLine();
        List<Message> results = new ArrayList<>();
        for (Message msg : storedMessages) {
            if (msg.getMessageID().equals(id)) {
                results.add(msg);
            }
        }
        if (results.isEmpty()) {
            System.out.println("No message found with ID: " + id);
        } else {
            System.out.println("\n--- Message Found ---");
            for (Message msg : results) {
                System.out.println("Recipient: " + msg.getRecipient());
                System.out.println("Message: " + msg.getMessageText());
                System.out.println("------------------------");
            }
        }
    }
    
    // d. Search for all messages by recipient
    private static void searchByRecipient(Scanner scanner) {
        System.out.print("Enter recipient cell number: ");
        String recipient = scanner.nextLine();
        List<Message> results = new ArrayList<>();
        for (Message msg : storedMessages) {
            if (msg.getRecipient().equals(recipient)) {
                results.add(msg);
            }
        }
        if (results.isEmpty()) {
            System.out.println("No messages found for recipient: " + recipient);
        } else {
            System.out.println("\n--- Messages for " + recipient + " ---");
            for (Message msg : results) {
                System.out.println("Message: " + msg.getMessageText());
                System.out.println("------------------------");
            }
        }
    }
    
    // e. Delete a message using message hash
    private static void deleteByMessageHash(Scanner scanner) {
        System.out.print("Enter Message Hash to delete: ");
        String hash = scanner.nextLine();
        Message toRemove = null;
        for (Message msg : storedMessages) {
            if (msg.getMessageHash().equals(hash)) {
                toRemove = msg;
                break;
            }
        }
        if (toRemove != null) {
            storedMessages.remove(toRemove);
            messageHashes.remove(hash);
            messageIDs.remove(toRemove.getMessageID());
            System.out.println("Message: \"" + toRemove.getMessageText() + "\" successfully deleted.");
        } else {
            System.out.println("No message found with hash: " + hash);
        }
    }
    
    // f. Display full report of all stored messages
    private static void displayFullReport() {
        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages found.");
            return;
        }
        System.out.println("\n=== FULL STORED MESSAGES REPORT ===");
        for (Message msg : storedMessages) {
            System.out.println("----------------------------------------");
            System.out.println("Message Hash: " + msg.getMessageHash());
            System.out.println("Message ID: " + msg.getMessageID());
            System.out.println("Recipient: " + msg.getRecipient());
            System.out.println("Sender: " + msg.getSender());
            System.out.println("Message: " + msg.getMessageText());
            System.out.println("----------------------------------------");
        }
    }
        
    
}
