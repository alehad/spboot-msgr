package msgr.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import msgr.broker.MessageRequestParams;
import msgr.broker.MessageRequestTopic;
import msgr.msg.Message;
import msgr.svc.MessageStoreService;

@Service
public class NoCache implements IMessageCache {

	@Autowired
	private MessageStoreService messageStoreService;
	
	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public void initialize() {
		// Nothing to initialize
		// NoCache implementation is simple call forward to active message store
	}

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		
		List<Message> result = new ArrayList<Message>();

		switch (topic) {
		case GetAllMessages:
    	{
    		result = messageStoreService.getStore().getMessages();
    		break;
    	}
		case GetAllMessagesBy:
		{
			result = messageStoreService.getStore().getMessagesBy(params.getFindByAuthor());
			break;
		}
		case GetOneMessage:
		{
			result.add(messageStoreService.getStore().getMessage(params.getFindById()));
			break;
		}
		case AddOneMessage:
		{
			result.add(messageStoreService.getStore().createMessage(params.getMessagePayload()));
			break;
		}
		case UpdateMessage:
		{
			result.add(messageStoreService.getStore().updateMessage(params.getFindById(), params.getMessagePayload()));
			break;
		}
		case UpdateMessageBy:
		{
			result.add(messageStoreService.getStore().updateMessageBy(params.getFindByAuthor(), params.getMessagePayload()));
			break;
		}
		case DeleteMessage:
		{
			messageStoreService.getStore().deleteMessage(params.getFindById());
			break;
		}
		case DeleteMessagesBy:
		{
			messageStoreService.getStore().deleteMessagesBy(params.getFindByAuthor());
			break;
		}
		case DeleteAllMessages:
		{
			messageStoreService.getStore().deleteAll();
			break;
		}
		default:
			break;
		}
		
		return result;
	}

}
