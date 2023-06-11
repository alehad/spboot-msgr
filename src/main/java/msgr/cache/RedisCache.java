package msgr.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import msgr.broker.MessageRequestParams;
import msgr.broker.MessageRequestTopic;
import msgr.msg.Message;
import msgr.msg.StoredMessage;
import msgr.redis.RedisMessage;
import msgr.redis.RedisMessageRepository;
import msgr.svc.MessageStoreService;

@Service
public class RedisCache implements IMessageCache {

	@Autowired
	private MessageStoreService messageStoreService;
	
    @Autowired
    private RedisMessageRepository redisMessageRepository;
    
	@Override
	public boolean isInitialized() {
		return redisMessageRepository.count() > 0L ? true : false;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		RedisMessage msg0 = new RedisMessage(0, "bonjour!", "alex");
		RedisMessage msg1 = new RedisMessage(1, "bonjour encore!", "alex2");
		
		redisMessageRepository.save(msg0);
		redisMessageRepository.save(msg1);
	}

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		List<Message> result = new ArrayList<Message>();

		switch (topic) {
		case GetAllMessages:
    	{
    		Iterable<RedisMessage> messages = redisMessageRepository.findAll();
    		for (RedisMessage m : messages) {
    			result.add(m.asStoredMessage());
    		}
    		break;
    	}
		case GetAllMessagesBy:
		{
			List<RedisMessage> messages = redisMessageRepository.findByAuthor(params.getFindByAuthor());
			if (!messages.isEmpty()) {
				messages.forEach(m -> result.add(m.asStoredMessage()));
			}
			break;
		}
		case GetOneMessage:
		{
			List<RedisMessage> messages = redisMessageRepository.findByMessageId(params.getFindById());
			if (!messages.isEmpty()) {
				messages.forEach(m -> result.add(m.asStoredMessage()));
			}
			break;
		}
		case AddOneMessage:
		{
			//TODO: refactor add message to physical store to use kafka, once the ESCache does not use messageStore directly
			result.add(messageStoreService.getStore().createMessage(params.getMessagePayload()));

			//naive implementation. if saving to message store was successful, also update the cache
			if (!result.isEmpty()) {
				result.forEach(message -> {
					if (message instanceof StoredMessage) {
						redisMessageRepository.save(new RedisMessage((StoredMessage) message));
					}
				});
			}
			break;
		}
		case UpdateMessage:
		{
			//TODO: refactor to use kafka, once the ESCache does not use messageStore directly
			result.add(messageStoreService.getStore().updateMessage(params.getFindById(), params.getMessagePayload()));

			//if saving to message store was successful, also update the cache -- refactor to use kafka
			if (!result.isEmpty()) {
				List<RedisMessage> messages = redisMessageRepository.findByMessageId(params.getFindById());
				messages.forEach(m -> {
    				m.setAuthor(params.getMessagePayload().getAuthor());
    				m.setMessage(params.getMessagePayload().getMessage());
    				redisMessageRepository.save(m);
				});
			}
			break;
		}
		case UpdateMessageBy:
		{
			//naive implementation -- just update first message found by this author
			result.add(messageStoreService.getStore().updateMessageBy(params.getFindByAuthor(), params.getMessagePayload()));

			result.forEach(message -> {
				if (message instanceof StoredMessage) {
					//this ensures we will update the same message (with same id) in the cache
					List<RedisMessage> esMessages = redisMessageRepository.findByMessageId(((StoredMessage)message).getMessageId());
					for (RedisMessage m : esMessages) {
	    				m.setMessage(message.getMessage());
	    				redisMessageRepository.save(m);
		    		}
				}
			});
			break;
		}
		case DeleteMessage:
		{
			messageStoreService.getStore().deleteMessage(params.getFindById());
			redisMessageRepository.deleteAll(redisMessageRepository.findByMessageId(params.getFindById()));
    		break;
		}
		case DeleteMessagesBy:
		{
			messageStoreService.getStore().deleteMessagesBy(params.getFindByAuthor());
			redisMessageRepository.deleteAll(redisMessageRepository.findByAuthor(params.getFindByAuthor()));
			break;
		}
		case DeleteAllMessages:
		{
			messageStoreService.getStore().deleteAll();
			redisMessageRepository.deleteAll();
			break;
		}
		default:
			break;
		}
		
		return result;

	}

}
