package msgr.db;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import msgr.msg.Message;

@Service
public class SimpleDbStore implements IMessageStore {

	//naive implementation
	private List<Message> messages = new ArrayList<Message>();
	
	@Override
	public List<Message> getMessages() {
		return messages;
	}

	@Override
	public Message getMessage(int id) {
		return messages.get(id);
	}

	@Override
	public Message createMessage(Message msg) {
		return messages.add(msg) ? msg : null;
	}

	@Override
	public Message updateMessage(int id, Message msg) {
		messages.set(id, msg); 	// should check for IndexOutOfBounds exception
		return msg;
	}

	@Override
	public void deleteMessage(int id) {
		messages.remove(id);
	}

	@Override
	public List<Message> getMessagesBy(String author) {
		return messages.stream().filter(t -> t.getAuthor().equals(author)).collect(Collectors.toList());
	}

	@Override
	public Message updateMessageBy(String author, Message msg) {
		// naive implementation as there can be many messages by the same author. this always updates first one found
		for (int i = 0; i < messages.size(); i++) {
			if (messages.get(i).getAuthor().equals(author)) {
				messages.set(i, msg);
				break;
			}
		}
		return msg;
	}

	@Override
	public void deleteMessagesBy(String author) {
		messages.removeIf(t -> t.getAuthor().equals(author));
	}

	@Override
	public void deleteAll() {
		messages.clear();
	}

	@Override
	public boolean isInitialized() {
		// initialized inline at construction
		return true;
	}

	@Override
	public void initialize() {
		// initialized inline at construction
	}
}
