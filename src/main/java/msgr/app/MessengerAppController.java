package msgr.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import msgr.broker.IMessageBroker;
import msgr.broker.MessageRequestParams;
import msgr.broker.MessageRequestTopic;
import msgr.msg.Message;
import msgr.svc.MessageBrokerService;

@RestController
public class MessengerAppController {
	
	/*
	 * **IMPORTANT**
	 * Auto-wiring only happens AFTER MessengerAppController construction
	 * which means that you cannot:
	 * 1. access the instance of the message store service in another variable inline initiation, or 
	 * 2. in the MessengerAppController constructor itself
	 * In both of these cases, you will get null pointer exception as the msg store service would not be 
	 * initialised at that point 
	 */
	
	private IMessageBroker messageBroker;
	
	@Autowired
	public MessengerAppController(MessageBrokerService svc) {
		this.messageBroker = svc.getActiveComponent();
	}
	
	// for GET methods, you only need to provide URL path
	@RequestMapping("/welcome")
	public String welcomeResponse() {
		return "Bonjour et bienvenue!";
	}

	@RequestMapping("/messages")
	public List<Message> getMessages() {
		return messageBroker.handleRequest(MessageRequestTopic.GetAllMessages, new MessageRequestParams());
	}

	@RequestMapping(method=RequestMethod.POST, value="/messages")
	public Message postMessage(@RequestBody Message msg) {
		return messageBroker.handleRequest(MessageRequestTopic.AddOneMessage, new MessageRequestParams(msg)).get(0);
	}

	// byId
	@RequestMapping("/messages/{msgId}")
	public Message getMessage(@PathVariable int msgId) {
		return messageBroker.handleRequest(MessageRequestTopic.GetOneMessage, new MessageRequestParams(msgId)).get(0);
	}

	@RequestMapping(method=RequestMethod.PUT, value="/messages/{msgId}")
	public Message updateMessage(@RequestBody Message newMsg, @PathVariable int msgId) {
		return messageBroker.handleRequest(MessageRequestTopic.UpdateMessage, new MessageRequestParams(msgId, newMsg)).get(0);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/messages/{msgId}")
	public void deleteMessage(@PathVariable int msgId) {
		messageBroker.handleRequest(MessageRequestTopic.DeleteMessage, new MessageRequestParams(msgId));
	}

	// byAuthor
	@RequestMapping("/messages/by/{byAuthor}")
	public List<Message> getMessagesBy(@PathVariable String byAuthor) {
		return messageBroker.handleRequest(MessageRequestTopic.GetAllMessagesBy, new MessageRequestParams(byAuthor));
	}

	@RequestMapping(method=RequestMethod.PUT, value="/messages/by/{byAuthor}")
	public Message updateMessageBy(@RequestBody Message newMsg, @PathVariable String byAuthor) {
		return messageBroker.handleRequest(MessageRequestTopic.UpdateMessageBy, new MessageRequestParams(byAuthor, newMsg)).get(0);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/messages/by/{byAuthor}")
	public void deleteMessagesBy(@PathVariable String byAuthor) {
		messageBroker.handleRequest(MessageRequestTopic.DeleteMessagesBy, new MessageRequestParams(byAuthor));
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/messages/all")
	public void deleteAll() {
		messageBroker.handleRequest(MessageRequestTopic.DeleteAllMessages, new MessageRequestParams());
	}
}
