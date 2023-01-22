package msgr.cache;

import java.util.List;

import msgr.broker.MessageRequestParams;
import msgr.broker.MessageRequestTopic;
import msgr.init.IInitializableComponent;
import msgr.msg.Message;

public interface IMessageCache extends IInitializableComponent {

	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params);

}
