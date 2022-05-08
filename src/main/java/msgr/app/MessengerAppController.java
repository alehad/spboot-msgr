package msgr.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
