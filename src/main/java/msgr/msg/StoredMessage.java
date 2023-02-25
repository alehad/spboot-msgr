package msgr.msg;

public class StoredMessage extends Message implements Comparable <StoredMessage> {
    
    private int	messageId;
	
	public StoredMessage() {
		//no-op default constructor
	}

	public StoredMessage(int msgId, Message msg) {
        super(msg.getMessage(), msg.getAuthor());
        this.messageId = msgId;
    }

	public StoredMessage(int msgId, String message, String author) {
        super(message, author);
        this.messageId = msgId;
    }
    
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int msgId) {
		this.messageId = msgId;
	}

    @Override
    public int compareTo(StoredMessage o) {
        return this.messageId == o.messageId ? 0 : 1; // trivial implementation
    }
}