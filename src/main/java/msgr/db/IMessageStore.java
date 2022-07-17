package msgr.db;

import java.util.List;

import msgr.init.IInitializableComponent;
import msgr.msg.Message;

public interface IMessageStore extends IInitializableComponent {
	
	// basic CRUD operations
	
	public List<Message> getMessages();

	public Message createMessage(Message msg);

	// by id [zero based -- first message id = 0]
	public Message getMessage(int id);
	
	public Message updateMessage(int id, Message msg);

	public void deleteMessage(int id);

	// by author
	public List<Message> getMessagesBy(String author);

	public Message updateMessageBy(String author, Message msg);

	public void deleteMessagesBy(String author);
	
	public void deleteAll();
}