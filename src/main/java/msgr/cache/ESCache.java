package msgr.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import msgr.broker.MessageRequestParams;
import msgr.broker.MessageRequestTopic;
import msgr.elastic.ESMessage;
import msgr.elastic.ESMessageRepository;
import msgr.msg.Message;
import msgr.msg.StoredMessage;
import msgr.svc.MessageStoreService;

@Service
public class ESCache implements IMessageCache {

	@Autowired
	private MessageStoreService messageStoreService;
	
    @Autowired
    private ESMessageRepository esMessageRepository;

    @Override
	public boolean isInitialized() {
		return esMessageRepository.count() > 0L ? true : false;
	}

	@Override
	public void initialize() {
		// naive implementation -- load all messages from message store into ES
		// if ES cache is not initialised [eg. it's found to be empty]
		List<Message> result = messageStoreService.getStore().getMessages();
		
		result.forEach(message -> {
			if (message instanceof StoredMessage) {
				esMessageRepository.save(new ESMessage((StoredMessage) message));
			}
		});
	}

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		List<Message> result = new ArrayList<Message>();

		switch (topic) {
		case GetAllMessages:
    	{
    		Iterable<ESMessage> messages = esMessageRepository.findAll();
    		for (ESMessage m : messages) {
    			result.add(m);
    		}
    		break;
    	}
		case GetAllMessagesBy:
		{
			Page<ESMessage> messages = esMessageRepository.findByAuthor(params.getFindByAuthor(), null);
			messages.forEach(m -> result.add(m));
			break;
		}
		case GetOneMessage:
		{
			Page<ESMessage> messages = esMessageRepository.findByMessageId(params.getFindById(), null);
			messages.forEach(m -> result.add(m));
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
						esMessageRepository.save(new ESMessage((StoredMessage) message));
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
				Page<ESMessage> messages = esMessageRepository.findByMessageId(params.getFindById(), null);
				messages.forEach(m -> {
    				m.setAuthor(params.getMessagePayload().getAuthor());
    				m.setMessage(params.getMessagePayload().getMessage());
    				esMessageRepository.save(m);
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
					Page<ESMessage> esMessages = esMessageRepository.findByMessageId(((StoredMessage)message).getMessageId(), null);
					for (ESMessage m : esMessages) {
	    				m.setMessage(message.getMessage());
	    				esMessageRepository.save(m);
		    		}
				}
			});
			break;
		}
		case DeleteMessage:
		{
			messageStoreService.getStore().deleteMessage(params.getFindById());
			esMessageRepository.deleteAll(esMessageRepository.findByMessageId(params.getFindById(), null));
    		break;
		}
		case DeleteMessagesBy:
		{
			messageStoreService.getStore().deleteMessagesBy(params.getFindByAuthor());
			esMessageRepository.deleteAll(esMessageRepository.findByAuthor(params.getFindByAuthor(), null));
			break;
		}
		case DeleteAllMessages:
		{
			messageStoreService.getStore().deleteAll();
			esMessageRepository.deleteAll();
			break;
		}
		default:
			break;
		}
		
		return result;

	}

}
