/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.chatapppoe;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author tsits
 */
public class Message {
    // Enums for message status
    public enum MessageStatus {
        SENT, STORED, DISREGARDED
    }
    
    private static int messageCounter = 0;
    private String messageID;
    private String recipient;
    private String messageText;
    private String messageHash;
    private MessageStatus status;
    private String sender; // For Part 3 - sender name

    // Default constructor
    public Message() {}
    
    // Constructor for creating messages from JSON
    public Message(String messageID, String recipient, String messageText, 
                   String messageHash, MessageStatus status, String sender) {
        this.messageID = messageID;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = messageHash;
        this.status = status;
        this.sender = sender;
    }

    // Generate random 10-digit message ID
    private static String generateMessageID() {
        Random rand = new Random();
        long id = 1_000_000_000L + (long)(rand.nextDouble() * 9_000_000_000L);
        return String.valueOf(id);
    }

    // Validate message ID length (≤10 characters)
    public boolean checkMessageID(String id) {
        return id != null && id.length() <= 10;
    }
    
    // Message should be less that 250 characters
    public String validateMessageLength(String message) {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int excess = message.length() - 250;
            
            return "Message exceeds 250 characters by " + excess + ", please reduce the size.";
        }
    }

    // Validate recipient cell: +27 followed by 9 or 10 digits
    public String checkRecipientCell(String cell) {
        if (cell == null || !cell.matches("^\\+27[0-9]{9,10}$")) {
            return "Recipient cell number incorrectly formatted. Please use +27 followed by 9 or 10 digits.";
        }
        return "Cell phone number successfully captured.";
    }
    
    // Create message hash: first two digits of ID : counter : firstWordLastWord (uppercase)
    public String createMessageHash(String msgID, int count, String message) {
        String firstTwo = msgID.length() >= 2 ? msgID.substring(0, 2) : msgID;
        // Extract first and last words
        String[] words = message.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 0 ? words[words.length - 1] : "";
        String combined = (firstWord + lastWord).replaceAll("[^a-zA-Z]", "").toUpperCase();
        return firstTwo + ":" + count + ":" + combined;
    }

    // User interaction: send, store, or disregard
    public String sentMessage(Scanner scanner, String msgID, String recipient, 
                             String msgText, int count, String senderName) {
        System.out.println("\nChoose an option:");
        System.out.println("1 - Send Message");
        System.out.println("2 - Store Message (to send later, saved in JSON)");
        System.out.println("0 - Disregard Message (delete)");
        System.out.print("Your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                this.status = MessageStatus.SENT;
                this.sender = senderName;
                messageCounter++;
                storeMessage(msgID, recipient, msgText, count, "SENT", senderName);
                return "Message successfully sent";
            case 2:
                this.status = MessageStatus.STORED;
                this.sender = senderName;
                storeMessage(msgID, recipient, msgText, count, "STORED", senderName);
                return "Message successfully stored";
            case 0:
                this.status = MessageStatus.DISREGARDED;
                this.sender = senderName;
                storeMessage(msgID, recipient, msgText, count, "DISREGARDED", senderName);
                return "Press 0 to delete the message - Message disregarded.";
            default:
                return "Invalid option. Message not processed.";
        }
    }

    // Store message in JSON file with status flag
    public void storeMessage(String msgID, String recipient, String msgText, 
                            int count, String flag, String senderName) {
        try {
            Path path = Paths.get("messages.json");
            JSONArray messagesArray = new JSONArray();
            if (Files.exists(path)) {
                String content = new String(Files.readAllBytes(path));
                if (!content.trim().isEmpty()) {
                    messagesArray = new JSONArray(content);
                }
            }
            JSONObject msgObj = new JSONObject();
            msgObj.put("messageID", msgID);
            msgObj.put("recipient", recipient);
            msgObj.put("message", msgText);
            msgObj.put("messageCounter", count);
            msgObj.put("flag", flag);
            msgObj.put("sender", senderName);
            msgObj.put("timestamp", System.currentTimeMillis());
            
            // Generate hash if not already set
            String hash = createMessageHash(msgID, count, msgText);
            msgObj.put("messageHash", hash);
            
            messagesArray.put(msgObj);
            Files.write(path, messagesArray.toString(2).getBytes());
        } catch (Exception e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }

    // Read messages from JSON file into an array
    public static List<Message> readMessagesFromJSON() {
        List<Message> messages = new ArrayList<>();
        try {
            Path path = Paths.get("messages.json");
            if (!Files.exists(path)) {
                return messages;
            }
            String content = new String(Files.readAllBytes(path));
            if (content.trim().isEmpty()) {
                return messages;
            }
            JSONArray jsonArray = new JSONArray(content);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String msgID = obj.getString("messageID");
                String recipient = obj.getString("recipient");
                String message = obj.getString("message");
                String hash = obj.getString("messageHash");
                String flag = obj.getString("flag");
                String sender = obj.optString("sender", "Unknown");
                
                MessageStatus status;
                switch (flag) {
                    case "SENT": status = MessageStatus.SENT; break;
                    case "STORED": status = MessageStatus.STORED; break;
                    case "DISREGARDED": status = MessageStatus.DISREGARDED; break;
                    default: status = MessageStatus.STORED;
                }
                
                Message msg = new Message(msgID, recipient, message, hash, status, sender);
                messages.add(msg);
            }
        } catch (Exception e) {
            System.out.println("Error reading messages from JSON: " + e.getMessage());
        }
        return messages;
    }

    // Print all messages sent during this session
    public String printMessages(List<Message> messageList) {
        if (messageList.isEmpty()) return "Coming soon!";
        StringBuilder sb = new StringBuilder("\n--- Sent Messages ---\n");
        for (Message m : messageList) {
            if (m.status == MessageStatus.SENT) {
                sb.append("Message ID: ").append(m.messageID).append("\n");
                sb.append("Message Hash: ").append(m.messageHash).append("\n");
                sb.append("Recipient: ").append(m.recipient).append("\n");
                sb.append("Message: ").append(m.messageText).append("\n");
                sb.append("------------------------\n");
            }
        }
        return sb.toString();
    }
    
    // Return total number of messages sent
    public int returnTotalMessages() {
        return messageCounter;
    }

    // Create a new message (used by main app)
    public Message createNewMessage(Scanner scanner, int messageNumber, String senderName) {
        System.out.println("\n--- New Message #" + (messageNumber + 1) + " ---");
        
        String recipient;
        while (true) {
            System.out.print("Recipient cell number (+27 followed by 9 digits, e.g., +27831234567): ");
            recipient = scanner.nextLine();
            String validation = checkRecipientCell(recipient);
            if (validation.equals("Cell phone number successfully captured.")) {
                break;
            }
            System.out.println(validation);
        }
        
        String text;
        while (true) {
            System.out.print("Message (max 250 characters): ");
            text = scanner.nextLine();
            if (text.length() <= 250) break;
            System.out.println("Please enter a message of less than 250 characters.");
        }
        
        String msgID = generateMessageID();
        while (!checkMessageID(msgID)) {
            msgID = generateMessageID();
        }
        int currentCount = messageCounter;
        String hash = createMessageHash(msgID, currentCount, text);
        
        String actionResult = sentMessage(scanner, msgID, recipient, text, currentCount, senderName);
        
        if (actionResult.equals("Message successfully sent") || 
            actionResult.equals("Message successfully stored")) {
            this.messageID = msgID;
            this.recipient = recipient;
            this.messageText = text;
            this.messageHash = hash;
            System.out.println(actionResult);
            return this;
        } else {
            System.out.println(actionResult);
            return null;
        }
    }

    // Getters and Setters
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }
    public MessageStatus getStatus() { return status; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

}
