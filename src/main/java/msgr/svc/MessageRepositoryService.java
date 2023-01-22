package msgr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import msgr.broker.MessageRequestParams;
import msgr.broker.MessageRequestTopic;
import msgr.msg.Message;

@Service
public class MessageRepositoryService {
	// Message Repository allows for introduction of caching layer between
	// the broker and the physical message store [e.g. Mongo, SqlServer]
	
	// Each cache implementation can handle the request differently, from 
	// simple call forwarding to write-through implementation
	
	@Autowired
	private MessageCachingService cache;
	
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		
		return cache.getActiveComponent().handleRequest(topic, params);
	}
}
