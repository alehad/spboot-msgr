package msgr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import msgr.db.IMessageStore;

@Service
public class MessageStoreService {

	@Autowired
	private List<IMessageStore> registry;

	// if nothing specified, default to mongo
	@Value( "${msgr.db.store:Mongo}" )
	private String storeType;
	
	public IMessageStore getStore() {
		return registry.stream().filter(s -> s.getClass().getName().contains(storeType)).findFirst().get();
	}
}
