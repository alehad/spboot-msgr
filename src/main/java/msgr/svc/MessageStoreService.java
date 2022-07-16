package msgr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import msgr.db.IMessageStore;

@Service
public class MessageStoreService {

//	@Autowired
//	private List<IMessageStore> registry;

//	// if nothing specified, default to mongo
//	@Value( "${msgr.db.store:Mongo}" )
//	private String activeStore;
//	
//	public IMessageStore getStore() {
//		return registry.stream().filter(s -> s.getClass().getName().contains(activeStore)).findFirst().get();
//	}
//	
//	public void setStore(String storeType) {
//		activeStore = storeType;
//	}

	private List<IMessageStore> registry;

	private IMessageStore activeStore;
	
	@Autowired
	public MessageStoreService(List<IMessageStore> registry, @Value("${msgr.db.store:Mongo}") String store) {
		this.registry = registry;
		setActiveStore(store);
	}
	
	public IMessageStore getStore() {
		return activeStore;
	}
	
	public void setActiveStore(String store) {
		activeStore = registry.stream().filter(s -> s.getClass().getName().contains(store)).findFirst().get();
		if (activeStore != null && !activeStore.isInitialized()) {
			activeStore.initialize();
		}
	}
	
	// just used for testing the StoreService
	List<IMessageStore> getRegisteredStores() {
		return registry;
	}
}
