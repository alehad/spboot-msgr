package msgr.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import msgr.msg.StoredMessage;

/*
 * None: Unlike ElasticSearch which provides its' own implementation of crud repository
 * 		 Redis does not do this, and because Redis is true key value store, you can only
 * 		 by default search by key [in this case string id @Id]
 * 		 In order to be able to search by other fields, those need to be annotated with @Indexed
 * 		 which cannot be done on fields from super classes in the hierarchy, hence Redis needs
 * 		 class created for all fields that need to be searchable/indexed.
 */

@RedisHash("RedisMessage")
public class RedisMessage {

    @Id
    private String id;
    
    @Indexed
    private int	messageId;

    @Indexed
	private String  author;

	private String 	message;
	
    public RedisMessage() {
		//no-op default constructor
	}

	public RedisMessage(int msgId, String message, String author) {
		this.messageId = msgId;
		this.message   = message;
		this.author    = author;
	}

	public RedisMessage(StoredMessage msg) {
		this.messageId = msg.getMessageId();
		this.message   = msg.getMessage();
		this.author    = msg.getAuthor();
	}
	
	public String getId() {
		return id;
	}

	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int msgId) {
		this.messageId = msgId;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public StoredMessage asStoredMessage() {
		return new StoredMessage(this.messageId, this.message, this.author);  
	}
}
