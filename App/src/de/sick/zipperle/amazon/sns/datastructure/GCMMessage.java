package de.sick.zipperle.amazon.sns.datastructure;

/**
 * Created by michael on 19.12.17.
 */

public class GCMMessage {
    private long sendTime = 0;
    private String sender = "";
    private String messageId = "";
    private String message = "";

    public GCMMessage(){

    }

    public GCMMessage(long sendTime, String sender, String messageId, String message) {
        this.sendTime = sendTime;
        this.sender = sender;
        this.messageId = messageId;
        this.message = message;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString(){
        String result = "Time: " + sendTime + "\n"
                + "Sender: " + sender + "\n"
                + "Message ID: " + messageId + "\n"
                + "Message: " + message + "\n";
        return result;
    }
}
