package msgr.app;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import msgr.msg.Message;

@Service
public class MessageService {
	
	//naive implementation
	private List<Message> messages = Arrays.asList(new Message("msg1", "auth1"), new Message("msg2", "auth2"));
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public Message getMessage(String byAuthor) {
		return messages.stream().filter(t -> t.getAuthor().equals(byAuthor)).findFirst().get();
	}
}
