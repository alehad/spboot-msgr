package msgr.broker;

import java.util.List;
import java.util.Properties;

import msgr.init.IInitializableComponent;
import msgr.msg.Message;

public interface IMessageBroker extends IInitializableComponent {

	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params);
}
