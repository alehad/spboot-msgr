package msgr.db;

import java.util.List;

import msgr.msg.Message;

public interface IMessageStore {
	
	// basic CRUD operations
	
	public List<Message> getMessages();

	public Message createMessage(Message msg);

	// by id
	public Message getMessage(int id);
	
	public Message updateMessage(int id, Message msg);

	public void deleteMessage(int id);

	// by author
	public List<Message> getMessagesBy(String author);

	public Message updateMessageBy(String author, Message msg);

	public void deleteMessagesBy(String author);
}