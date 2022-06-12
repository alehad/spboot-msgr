package msgr.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import msgr.db.IMessageStore;
//import msgr.db.SimpleDbStore;
import msgr.msg.Message;

import msgr.svc.MessageStoreService;

@RestController
public class MessengerAppController {
	
//	@Autowired
//	private MessageStoreService messageStoreService;

	private MessageStoreService messageStoreService = new MessageStoreService();

//	@Autowired
//	private IMessageStore messageStore = new SimpleDbStore();
	private IMessageStore messageStore;
	
	public MessengerAppController() {
		messageStore = messageStoreService.getStore();
	}

	// for GET methods, you only need to provide URL path
	@RequestMapping("/welcome")
	public String welcomeResponse() {
		return "Bonjour et bienvenue!";
	}

	@RequestMapping("/messages")
	public List<Message> getMessages() {
		return messageStore.getMessages();
	}

	@RequestMapping("/messages/by/{byAuthor}")
	public List<Message> getMessagesBy(@PathVariable String byAuthor) {
		return messageStore.getMessagesBy(byAuthor);
	}

	@RequestMapping(method=RequestMethod.POST, value="/messages")
	public Message postMessage(@RequestBody Message msg) {
		return messageStore.createMessage(msg);
	}

	@RequestMapping(method=RequestMethod.PUT, value="/messages/by/{byAuthor}")
	public Message updateMessageBy(@RequestBody Message newMsg, @PathVariable String byAuthor) {
		return messageStore.updateMessageBy(byAuthor, newMsg);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/messages/by/{byAuthor}")
	public boolean deleteMessagesBy(@PathVariable String byAuthor) {
		return messageStore.deleteMessagesBy(byAuthor);
	}
}
