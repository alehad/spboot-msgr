package msgr.svc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import msgr.cache.IMessageCache;
import msgr.init.InitializableComponentRegistry;

@Service
public class MessageCachingService extends InitializableComponentRegistry<IMessageCache> {

	@Autowired
	public MessageCachingService(List<IMessageCache> registry, @Value("${msgr.cache:NoCache}") String active) {
		super(registry, active);
	}

}
