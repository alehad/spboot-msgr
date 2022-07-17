package msgr.broker;

import java.util.List;
import java.util.Properties;

import msgr.init.IInitializableComponent;
import msgr.msg.Message;

public interface IMessageBroker extends IInitializableComponent {

	public List<Message> handleRequest(MessageRequestTopic topic, Properties params, Message msg);

//	public List<Message> handleGetRequest(MessageRequestTopic topic, Properties params);
//
//	public Message 		 handlePostRequest(MessageRequestTopic topic, Properties params);
//
//	public Message 		 handlePutRequest(MessageRequestTopic topic, Properties params);
//
//	public void     	 handleDeleteRequest(MessageRequestTopic topic, Properties params);
}
