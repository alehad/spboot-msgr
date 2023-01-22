package msgr.broker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import msgr.msg.Message;
import msgr.svc.MessageRepositoryService;

@Service
public class SimpleMessageBroker implements IMessageBroker {
	
	@Autowired
	private MessageRepositoryService messageRepositoryService;

	//SimpleMessageBroker just calls the store service directly to handle request
	//it does not use any transport/broker mechanism for request handling

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		return messageRepositoryService.handleRequest(topic, params);
	}

	@Override
	public boolean isInitialized() {
		return true;
	}

	@Override
	public void initialize() {
	}
}
