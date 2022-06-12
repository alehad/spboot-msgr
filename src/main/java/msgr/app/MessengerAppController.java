package msgr.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import msgr.msg.Message;

import msgr.svc.MessageStoreService;

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
	@Autowired
	private MessageStoreService messageStoreService;

	// for GET methods, you only need to provide URL path
	@RequestMapping("/welcome")
	public String welcomeResponse() {
		return "Bonjour et bienvenue!";
	}

	@RequestMapping("/messages")
	public List<Message> getMessages() {
		return messageStoreService.getStore().getMessages();
	}

	@RequestMapping(method=RequestMethod.POST, value="/messages")
	public Message postMessage(@RequestBody Message msg) {
		return messageStoreService.getStore().createMessage(msg);
	}

	// byId
	@RequestMapping("/messages/{msgId}")
	public Message getMessage(@PathVariable int msgId) {
		return messageStoreService.getStore().getMessage(msgId);
	}

	@RequestMapping(method=RequestMethod.PUT, value="/messages/{msgId}")
	public Message updateMessage(@RequestBody Message newMsg, @PathVariable int msgId) {
		return messageStoreService.getStore().updateMessage(msgId, newMsg);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/messages/{msgId}")
	public void deleteMessage(@PathVariable int msgId) {
		messageStoreService.getStore().deleteMessage(msgId);
	}

	// byAuthor
	@RequestMapping("/messages/by/{byAuthor}")
	public List<Message> getMessagesBy(@PathVariable String byAuthor) {
		return messageStoreService.getStore().getMessagesBy(byAuthor);
	}

	@RequestMapping(method=RequestMethod.PUT, value="/messages/by/{byAuthor}")
	public Message updateMessageBy(@RequestBody Message newMsg, @PathVariable String byAuthor) {
		return messageStoreService.getStore().updateMessageBy(byAuthor, newMsg);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/messages/by/{byAuthor}")
	public void deleteMessagesBy(@PathVariable String byAuthor) {
		messageStoreService.getStore().deleteMessagesBy(byAuthor);
	}
}
