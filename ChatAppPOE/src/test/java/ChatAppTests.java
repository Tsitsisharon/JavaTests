/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import com.mycompany.chatapppoe.Login;
import com.mycompany.chatapppoe.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 *
 * @author tsits
 */
public class ChatAppTests {
    
    private List<Message> testMessages;
    Message message = new Message();
    Login login = new Login();
    
    @BeforeEach
    void setUp() {
        // Setup test data from assignment
        testMessages = new ArrayList<>();
        testMessages.add(new Message("1", "+27834557896", "Did you get the cake?",
                "12:0:DIDCAKE", Message.MessageStatus.SENT, "Kyle Smith"));
        testMessages.add(new Message("2", "+27838884567", "Where are you? You are late! I have asked you to be on time.",
                "23:1:WHERE TIME", Message.MessageStatus.STORED, "Kyle Smith"));
        testMessages.add(new Message("3", "+27834484567", "Yohoooo, I am at your gate.",
                "34:2:YOHOOGATE", Message.MessageStatus.DISREGARDED, "Kyle Smith"));
        testMessages.add(new Message("4", "0838884567", "It is dinner time!",
                "45:3:ITTIME", Message.MessageStatus.SENT, "Kyle Smith"));
        testMessages.add(new Message("5", "+27838884567", "Ok, I am leaving without you.",
                "56:4:OKYOU", Message.MessageStatus.STORED, "Kyle Smith"));
    }
           
    @Test
    void testSentMessagesArrayCorrectlyPopulated() {
        // Test Data: message 1-4
        List<String> sentMessages = new ArrayList<>();
        for (Message msg : testMessages) {
            if (msg.getStatus() == Message.MessageStatus.SENT) {
                sentMessages.add(msg.getMessageText());
            }
        }
        // Expected: "Did you get the cake?", "It is dinner time!" 
        assertEquals(2, sentMessages.size()); 
        assertTrue(sentMessages.contains("Did you get the cake?")); 
        assertTrue(sentMessages.contains("It is dinner time!"));
    }
        
    @Test
    void testDisplayLongestMessage() {
        // Test Data: message 1-4
        Message longest = testMessages.stream()
                
                .max(Comparator.comparingInt(m -> m.getMessageText().length()))
                .orElse(null);
        assertNotNull(longest);
        assertEquals("Where are you? You are late! I have asked you to be on time.", 
                longest.getMessageText());
    }
    
    @Test
    void testSearchByMessageID() {
        // Test Data: message 4
        String searchID = "4";
        Optional<Message> found = testMessages.stream()
                .filter(m -> m.getMessageID().equals(searchID))
                .findFirst();
        assertTrue(found.isPresent());
        assertEquals("It is dinner time!", found.get().getMessageText());
    }
    
    @Test
    void testSearchMessagesByRecipient() {
        // Test Data: +27838884567
        String recipient = "+27838884567";
        List<Message> results = testMessages.stream()
                .filter(m -> m.getRecipient().equals(recipient))
                .collect(Collectors.toList());
        assertEquals(2, results.size());
        List<String> messages = results.stream()
                .map(Message::getMessageText)
                .collect(Collectors.toList());
        assertTrue(messages.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(messages.contains("Ok, I am leaving without you."));
    }
    
    @Test
    void testDeleteMessageByHash() {
        // Test Data: Test Message 2
        String hashToDelete = "23:1:WHERE TIME";
        Optional<Message> toDelete = testMessages.stream()
                .filter(m -> m.getMessageHash().equals(hashToDelete))
                .findFirst();
        assertTrue(toDelete.isPresent());
        assertEquals("Where are you? You are late! I have asked you to be on time.", 
                toDelete.get().getMessageText());
        
        // Remove it
        testMessages.removeIf(m -> m.getMessageHash().equals(hashToDelete));
        
        // Verify removed
        Optional<Message> shouldBeEmpty = testMessages.stream()
            .filter(m -> m.getMessageHash().equals(hashToDelete))
            .findFirst();
        assertFalse(shouldBeEmpty.isPresent());
        assertEquals(4, testMessages.size());
    }
    
    @Test
    void testDisplayReport() {
        // Test Data: all messages should be in report with hash, recipient, message
        List<Message> storedMessages = testMessages.stream()
                .filter(m -> m.getStatus() == Message.MessageStatus.STORED)
                .collect(Collectors.toList());
        
        assertEquals(2, storedMessages.size());
        
        for (Message msg : storedMessages) {
            assertNotNull(msg.getMessageHash());
            assertNotNull(msg.getRecipient());
            assertNotNull(msg.getMessageText());
        }
    }
    
    @Test
    void testCheckMessageID() {      
        assertTrue(message.checkMessageID("1234567890"));
        assertFalse(message.checkMessageID("12345678901"));
    }
        
    @Test
    void testCheckRecipientCellValid() {
        assertEquals("Cell phone number successfully captured.", 
                message.checkRecipientCell("+27712345678"));
    }
        
    @Test
    void testCheckRecipientCellInvalid() {
        assertEquals("Recipient cell number incorrectly formatted. Please use +27 followed by 9 or 10 digits.",
                message.checkRecipientCell("0838884567"));
    }
        
    @Test
    void testCreateMessageHash() {
        String hash = message.createMessageHash("1234567890",1, "Did you get the cake?");
        assertEquals("12:1:DIDCAKE",hash);
        }
        
    @Test
    void testCheckUserName() {
        assertTrue(login.checkUserName("ab_cd"));
        assertFalse(login.checkUserName("abcdef"));
    }
        
    @Test
    void testCheckPasswordComplexity() {
        assertTrue(login.checkPasswordComplexity("Password1!"));
        assertFalse(login.checkPasswordComplexity("password"));
    }
        
}

