package msgr.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import msgr.msg.Message;

@RestController
public class MessengerAppController {
	
	@Autowired
	private MessageService messageService;

	// for GET methods, you only need to provide URL path
	@RequestMapping("/welcome")
	public String welcomeResponse() {
		return "Bonjour et bienvenue!";
	}

	@RequestMapping("/messages")
	public List<Message> getMessages() {
		return messageService.getMessages();
	}

	@RequestMapping("/messages/{byAuthor}")
	public Message getMessage(@PathVariable String byAuthor) {
		return messageService.getMessage(byAuthor);
	}

	@RequestMapping(method=RequestMethod.POST, value="/messages")
	public boolean postMessage(@RequestBody Message msg) {
		return messageService.addMessage(msg);
	}

	@RequestMapping(method=RequestMethod.PUT, value="/messages/{byAuthor}")
	public boolean updateMessage(@RequestBody Message newMsg, @PathVariable String byAuthor) {
		return messageService.updateMessage(byAuthor, newMsg);
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/messages/{byAuthor}")
	public boolean deleteMessage(@PathVariable String byAuthor) {
		return messageService.deleteMessage(byAuthor);
	}
}
