package msgr.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import msgr.msg.Message;

@Service
public class MessageService {
	
	//naive implementation
	private List<Message> messages = new ArrayList<Message>(Arrays.asList(new Message("msg1", "auth1"), new Message("msg2", "auth2")));
	
	public List<Message> getMessages() {
		return messages;
	}
	
	public Message getMessage(String byAuthor) {
		return messages.stream().filter(t -> t.getAuthor().equals(byAuthor)).findFirst().get();
	}

	public boolean addMessage(Message msg) {
		return messages.add(msg);
	}
	
	public boolean updateMessage(String byAuthor, Message newMsg) {
		boolean updated = false;
		// naive implementation as there can be many messages by the same author. this always updates first one found
		for (int i = 0; i < messages.size(); i++) {
			if (messages.get(i).getAuthor().equals(byAuthor)) {
				messages.set(i, newMsg);
				updated = true;
				break;
			}
		}
		return updated;
	}

	public boolean deleteMessage(String byAuthor) {
		return messages.removeIf(t -> t.getAuthor().equals(byAuthor));
	}
}
