package msgr.broker;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import msgr.msg.Message;
import msgr.svc.MessageStoreService;

@Service
public class SimpleMessageBroker implements IMessageBroker {
	
	@Autowired
	private MessageStoreService messageStoreService;

	//SimpleMessageBroker just calls the store service directly to handle request
	//it does not use any transport/broker mechanism for request handling

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, Properties params, Message msg) {

		List<Message> result = new ArrayList<Message>();

		switch (topic) {
		case GetAllMessages:
    	{
    		result = messageStoreService.getStore().getMessages();
    		break;
    	}
		case GetAllMessagesBy:
		{
			result = messageStoreService.getStore().getMessagesBy(params.getProperty("byAuthor"));
			break;
		}
		case GetOneMessage:
		{
			result.add(messageStoreService.getStore().getMessage(Integer.parseInt(params.getProperty("msgId"))));
			break;
		}
		case AddOneMessage:
		{
			result.add(messageStoreService.getStore().createMessage(msg));
			break;
		}
		case UpdateMessage:
		{
			result.add(messageStoreService.getStore().updateMessage(Integer.parseInt(params.getProperty("msgId")), msg));
			break;
		}
		case UpdateMessageBy:
		{
			result.add(messageStoreService.getStore().updateMessageBy(params.getProperty("byAuthor"), msg));
			break;
		}
		case DeleteMessage:
		{
			messageStoreService.getStore().deleteMessage(Integer.parseInt(params.getProperty("msgId")));
			break;
		}
		case DeleteMessagesBy:
		{
			messageStoreService.getStore().deleteMessagesBy(params.getProperty("byAuthor"));
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

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public void initialize() {
	}
}
