package msgr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import msgr.broker.IMessageBroker;
import msgr.init.InitializableComponentRegistry;

//public class MessageBrokerService {
//
//	private List<IMessageBroker> registry;
//
//	private IMessageBroker activeProvider;
//	
//	@Autowired
//	public MessageBrokerService(List<IMessageBroker> registry, @Value("${msgr.message.broker:Simple}") String provider) {
//		this.registry = registry;
//		setActive(provider);
//	}
//	
//	public IMessageBroker getStore() {
//		return activeProvider;
//	}
//	
//	public void setActive(String provider) {
//		activeProvider = registry.stream().filter(s -> s.getClass().getName().contains(provider)).findFirst().get();
//		if (activeProvider != null && !activeProvider.isInitialized()) {
//			activeProvider.initialize();
//		}
//	}
//	
//	// just used for testing the StoreService
//	List<IMessageBroker> getRegisteredStores() {
//		return registry;
//	}
//}
@Service
public class MessageBrokerService extends InitializableComponentRegistry<IMessageBroker> {

	@Autowired
	public MessageBrokerService(List<IMessageBroker> registry, @Value("${msgr.message.broker:Simple}") String broker) {
		super(registry, broker);
	}
	
}
