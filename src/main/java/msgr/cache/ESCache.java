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
		case GetAllMessagesBy:
		{
			Page<ESMessage> esMessages = esMessageRepository.findByAuthor(params.getFindByAuthor(), null);
			for (ESMessage m : esMessages) {
    			result.add(new Message(m.getMessage(), m.getAuthor()));
    		}
			break;
		}
		case GetOneMessage:
		{
			// Note: trivial implementation, not to be used on large data set
			// right way to implement is via FindById, but requires setting the @Id field in ES done properly
			// Note: ES will override Id passed in the constructor of the ESMessage, if field is tagged with @Id
    		Iterable<ESMessage> esMessages = esMessageRepository.findAll();
    		int counter = 0;
    		for (ESMessage m : esMessages) {
    			if (++counter == params.getFindById()) {
    				result.add(new Message(m.getMessage(), m.getAuthor()));
    				break;
    			}
    		}
			break;
		}
		case AddOneMessage:
		{
			//TODO: refactor add message to physical store to use kafka, once the ESCache does not use messageStore directly
			result.add(messageStoreService.getStore().createMessage(params.getMessagePayload()));

			//if saving to message store was successful, also update the cache
			//this is a naive implementation, as there is no offline sync between physical store being updated and cache being refreshed
			//the cache refresh should also be handled via kafka update once implemented for saving in step 1 above
			if (!result.isEmpty()) {
				esMessageRepository.save(new ESMessage(params.getMessagePayload()));
			}
			break;
		}
		case UpdateMessage:
		{
			//TODO: refactor to use kafka, once the ESCache does not use messageStore directly
			result.add(messageStoreService.getStore().updateMessage(params.getFindById(), params.getMessagePayload()));

			//if saving to message store was successful, also update the cache -- refactor to use kafka
			if (!result.isEmpty()) {
	    		Iterable<ESMessage> esMessages = esMessageRepository.findAll();
	    		int counter = 0;
	    		for (ESMessage m : esMessages) {
	    			if (++counter == params.getFindById()) {
	    				m.setAuthor(params.getMessagePayload().getAuthor());
	    				m.setMessage(params.getMessagePayload().getMessage());
	    				esMessageRepository.save(m);
	    				break;
	    			}
	    		}
			}
			break;
		}
		case UpdateMessageBy:
		{
			//naive implementation -- just update first message found by this author
			result.add(messageStoreService.getStore().updateMessageBy(params.getFindByAuthor(), params.getMessagePayload()));

			//if saving to message store was successful, also update the cache -- refactor to use kafka
			//this is a bad implementation because there is no guarantee that order of messages in message store and cache is the same
			//TODO: refactor to ensure same message updated in cache and in store. might be involved as need a way to identify what's been updated in store
			if (!result.isEmpty()) {
				Page<ESMessage> esMessages = esMessageRepository.findByAuthor(params.getFindByAuthor(), null);
				for (ESMessage m : esMessages) {
					//for now just update the first one found
    				m.setMessage(params.getMessagePayload().getMessage());
    				esMessageRepository.save(m);
    				break;
	    		}
			}
			break;
		}
		case DeleteMessage:
		{
			//this relies on order of messages in message store and cache being the same, which is not necessarily the case
			//TODO: in order to address this problem we need to fix setting of the message Id, which will ensure same messages are deleted 
			messageStoreService.getStore().deleteMessage(params.getFindById());

    		Iterable<ESMessage> esMessages = esMessageRepository.findAll();
    		int counter = 0;
    		for (ESMessage m : esMessages) {
    			if (++counter == params.getFindById()) {
    				esMessageRepository.delete(m);
    				break;
    			}
    		}
    		break;
		}
		case DeleteMessagesBy:
		{
			messageStoreService.getStore().deleteMessagesBy(params.getFindByAuthor());

			Page<ESMessage> esMessages = esMessageRepository.findByAuthor(params.getFindByAuthor(), null);
			esMessageRepository.deleteAll(esMessages);
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
