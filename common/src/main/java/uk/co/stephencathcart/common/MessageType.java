package uk.co.stephencathcart.common;

/**
 * Message type enum which specifies the type of message being sent / received
 * to / from the Service Bus.
 *
 * @author Stephen Cathcart
 */
public enum MessageType {
    SNAPSHOT,
    REGISTRATION
}
