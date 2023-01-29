package msgr.cache;

import java.util.ArrayList;
import java.util.List;

//import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import msgr.broker.MessageRequestParams;
import msgr.broker.MessageRequestTopic;
import msgr.elastic.ESMessage;
import msgr.elastic.ESMessageRepository;
import msgr.msg.Message;
import msgr.svc.MessageStoreService;

@Service
public class ESCache implements IMessageCache {

	@Autowired
	private MessageStoreService messageStoreService;
	
    @Autowired
    private ESMessageRepository esMessageRepository;

//    @Autowired
//    private RestHighLevelClient esClient;

    @Override
	public boolean isInitialized() {
		return esMessageRepository.count() > 0L ? true : false;
	}

	@Override
	public void initialize() {
		// naive implementation -- load all messages from message store into ES
		// in reality, ES should be loaded once and stored via volumes
		// this also assumes message store is persisted, which in reality is not the case yet
		// TODO: refactor this
		List<Message> result = messageStoreService.getStore().getMessages();
		
		if (result.isEmpty()) {
			// if nothing in the physical store, create a couple of messages
			ESMessage msg1 = new ESMessage("msg1", "auth1");
			ESMessage msg2 = new ESMessage("msg2", "auth2");
			
			esMessageRepository.save(msg1);
			esMessageRepository.save(msg2);
		}
		else {
			for (Message m : result) {
				esMessageRepository.save(new ESMessage(m));
			}
		}
	}

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		List<Message> result = new ArrayList<Message>();

		switch (topic) {
		case GetAllMessages:
    	{
    		Iterable<ESMessage> esMessages = esMessageRepository.findAll();
    		for (ESMessage m : esMessages) {
    			result.add(new Message(m.getMessage(), m.getAuthor()));
    		}
    		break;
    	}
/*		case GetAllMessagesBy:
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
*/		default:
			break;
		}
		
		return result;

	}

}
