package msgr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import msgr.db.IMessageStore;
import msgr.init.InitializableComponentRegistry;

@Service
public class MessageStoreService extends InitializableComponentRegistry<IMessageStore> {

	@Autowired
	public MessageStoreService(List<IMessageStore> registry, @Value("${msgr.db.store:Mongo}") String store) {
		super(registry, store);
	}
	
	public IMessageStore getStore() {
		return super.getActiveComponent();
	}
	
	// just used for testing the StoreService
	List<IMessageStore> getRegisteredStores() {
		return super.getRegistry();
	}
}
