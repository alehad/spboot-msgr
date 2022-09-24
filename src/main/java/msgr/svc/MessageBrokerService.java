package msgr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import msgr.broker.IMessageBroker;
import msgr.init.InitializableComponentRegistry;

@Service
public class MessageBrokerService extends InitializableComponentRegistry<IMessageBroker> {

	@Autowired
	public MessageBrokerService(List<IMessageBroker> registry, @Value("${msgr.message.broker:Simple}") String broker) {
		super(registry, broker);
	}
	
}
