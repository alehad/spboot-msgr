package msgr.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import msgr.msg.Message;
import msgr.msg.StoredMessage;

@RedisHash("RedisMessage")
public class RedisMessage extends StoredMessage {

    @Id
    private String id;

    public RedisMessage() {
		//no-op default constructor
	}
	
	public RedisMessage(int msgId, Message msg) {
		super(msgId, msg);
	}

	public RedisMessage(int msgId, String message, String author) {
		super(msgId, message, author);
	}

	public RedisMessage(StoredMessage msg) {
		super(msg.getMessageId(), msg);
	}
}
