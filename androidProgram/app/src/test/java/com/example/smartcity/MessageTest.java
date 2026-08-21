package com.example.smartcity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * @author shangyishen
 * UID: u7735222
 */
@RunWith(RobolectricTestRunner.class)
public class MessageTest {
    private P2PMessage p2pMessage;

    @Before
    public void setUp() {
        p2pMessage = new P2PMessage();
    }

    @Test
    public void testCreateMessageDataWithValidInputs() {
        //prepare testing data
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        String messageContent = "Hello, this is a test message.";
        Map<String, Object> messageData = p2pMessage.createMessageData(senderEmail, receiverEmail, messageContent);

        // check if elements are correct
        assertNotNull(messageData);
        assertEquals("Hello, this is a test message.", messageData.get("message"));
        assertEquals("receiver@example.com", messageData.get("receiver"));
        assertEquals("sender@example.com", messageData.get("sender"));
        assertEquals("sent", messageData.get("statue"));
        assertNotNull(messageData.get("timestamp"));
        assertFalse(((String) Objects.requireNonNull(messageData.get("timestamp"))).isEmpty());
    }

    /**
     * marginal tests
     */
    @Test
    public void testCreateMessageDataWithEmptyMessage() {
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        String messageContent = "";

        Map<String, Object> messageData = p2pMessage.createMessageData(senderEmail, receiverEmail, messageContent);

        // Assert: 验证 messageData
        assertNotNull(messageData);
        assertEquals("", messageData.get("message"));
        assertEquals("receiver@example.com", messageData.get("receiver"));
        assertEquals("sender@example.com", messageData.get("sender"));
        assertEquals("sent", messageData.get("statue"));
        assertNotNull(messageData.get("timestamp"));
        assertFalse(((String) Objects.requireNonNull(messageData.get("timestamp"))).isEmpty());
    }

    @Test
    public void testCreateMessageDataWithNullMessage() {
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        String messageContent = null;
        Map<String, Object> messageData = p2pMessage.createMessageData(senderEmail, receiverEmail, messageContent);

        assertNotNull(messageData);
        assertNull(messageData.get("message"));
        assertEquals("receiver@example.com", messageData.get("receiver"));
        assertEquals("sender@example.com", messageData.get("sender"));
        assertEquals("sent", messageData.get("statue"));
        assertNotNull(messageData.get("timestamp"));
        assertFalse(((String) Objects.requireNonNull(messageData.get("timestamp"))).isEmpty());
    }
    // test long message content
    @Test
    public void testCreateMessageDataWithLongMessage() {
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        StringBuilder longMessageContent = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longMessageContent.append("message part ");
        }

        Map<String, Object> messageData = p2pMessage.createMessageData(senderEmail, receiverEmail, longMessageContent.toString());

        assertNotNull(messageData);
        assertEquals(longMessageContent.toString(), messageData.get("message"));
        assertEquals("receiver@example.com", messageData.get("receiver"));
        assertEquals("sender@example.com", messageData.get("sender"));
        assertEquals("sent", messageData.get("statue"));
        assertNotNull(messageData.get("timestamp"));
        assertFalse(((String) Objects.requireNonNull(messageData.get("timestamp"))).isEmpty());
    }

    //test special character
    @Test
    public void testCreateMessageDataWithSpecialCharacters() {
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        String messageContent = "This message includes special characters: !@#$%^&*()_+";

        Map<String, Object> messageData = p2pMessage.createMessageData(senderEmail, receiverEmail, messageContent);

        assertNotNull(messageData);
        assertEquals("This message includes special characters: !@#$%^&*()_+", messageData.get("message"));
        assertEquals("receiver@example.com", messageData.get("receiver"));
        assertEquals("sender@example.com", messageData.get("sender"));
        assertEquals("sent", messageData.get("statue"));
        assertNotNull(messageData.get("timestamp"));
        assertFalse(((String) Objects.requireNonNull(messageData.get("timestamp"))).isEmpty());
    }
    // test null receiver and sender
    @Test
    public void testCreateMessageDataWithEmptyEmails() {
        String senderEmail = "";
        String receiverEmail = "";
        String messageContent = "Test message with empty emails.";

        Map<String, Object> messageData = p2pMessage.createMessageData(senderEmail, receiverEmail, messageContent);

        assertNotNull(messageData);
        assertEquals("Test message with empty emails.", messageData.get("message"));
        assertEquals("", messageData.get("receiver"));
        assertEquals("", messageData.get("sender"));
        assertEquals("sent", messageData.get("statue"));
        assertNotNull(messageData.get("timestamp"));
        assertFalse(((String) Objects.requireNonNull(messageData.get("timestamp"))).isEmpty());
    }
}
