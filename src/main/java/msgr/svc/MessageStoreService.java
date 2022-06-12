package msgr.svc;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import msgr.db.IMessageStore;
import msgr.db.SimpleDbStore;

@Service
public class MessageStoreService {

	//@Autowired
	//@Qualifier("simpleDbStore")
	IMessageStore messageStore = new SimpleDbStore();
	
	public IMessageStore getStore() {
		return messageStore;
	}
}
