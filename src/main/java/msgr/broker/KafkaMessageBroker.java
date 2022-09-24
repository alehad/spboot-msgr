package msgr.broker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
//import org.springframework.util.concurrent.ListenableFutureCallback;

import msgr.msg.Message;
import msgr.svc.MessageStoreService;

@Service
public class KafkaMessageBroker implements IMessageBroker {

	@Autowired
	private MessageStoreService messageStoreService;
	
	class ProcessingResult {
		Boolean requestProcessed = false;
		List<Message> result 	 = new ArrayList<Message>();
	}
	
	private HashMap<String, ProcessingResult> requestProcessedStatus = new HashMap<String, ProcessingResult>();
	
	@Value("${kafka.bootstrap.address}")
	private String bootstrapServerAddress;
	
	private boolean initialized = false;
	
	@Autowired
	private KafkaTemplate<String, MessageRequestParams> kafkaTemplate;

	public String postRequest(MessageRequestTopic requestTopic, MessageRequestParams params) {
        
	    ListenableFuture<SendResult<String, MessageRequestParams>> future = kafkaTemplate.send(requestTopic.topic, params);
		
	    String requestKey = null; 
	    		
	    try {
	    	SendResult<String, MessageRequestParams>  result = future.get();
			requestKey = generateRequestCorrelationId(result.getRecordMetadata().topic(), 
													  result.getRecordMetadata().partition(), 
													  result.getRecordMetadata().offset());
			requestProcessedStatus.put(requestKey, new ProcessingResult());
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
        return requestKey;
	}
	
	@Override
	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public void initialize() {
		/* We need to create kafka topics programmatically otherwise the first time
		 * services are running and topic is being published, the kafka listener will not get notified
		 * If you restart the services and keep the kafka cluster running, same topic will be successfully
		 * processed, but not the first time it is issued. Hence initialize will create all the topics
		 * we care about that are not already known/created on the kafka cluster
		 */
		Properties kafkaConfig = new Properties();
		
		kafkaConfig.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerAddress);
		
		try (Admin admin = Admin.create(kafkaConfig)) {
            createTopic(admin, "alehad.messenger.topic.getall");
            createTopic(admin, "alehad.messenger.topic.getallby");
            createTopic(admin, "alehad.messenger.topic.getone");
            createTopic(admin, "alehad.messenger.topic.addone");
            createTopic(admin, "alehad.messenger.topic.update");
            createTopic(admin, "alehad.messenger.topic.updateby");
            createTopic(admin, "alehad.messenger.topic.delete");
            createTopic(admin, "alehad.messenger.topic.deleteby");
            createTopic(admin, "alehad.messenger.topic.deleteall");

            initialized = true;
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public void createTopic(Admin admin, String topicName) throws Exception {
		ListTopicsResult topics = admin.listTopics();
    	
		if (topics.names().get().contains(topicName)) {
			return;
		}
		
        int partitions = 1;
        short replicationFactor = 1;
        
        NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

        CreateTopicsResult result = admin.createTopics(Collections.singleton(newTopic));

        KafkaFuture<Void> future = result.values().get(topicName);

        // block until topic creation has completed or failed
        future.get();
    }
    
	// generate simple request correlation id 
	private static String generateRequestCorrelationId(String topic, int partition, long offset) {
		return topic + "-" + partition + "-" + offset;
	}

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		String requestKey = postRequest(topic, params);
		
	    while (!requestProcessedStatus.get(requestKey).requestProcessed) {
	    	try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return requestProcessedStatus.get(requestKey).result;
	}

	@KafkaListener(
			 topicPattern = "alehad.messenger.topic.*",
		     containerFactory = "messageParamsKafkaListenerContainerFactory")
	public void processRequest(@Payload MessageRequestParams params, 
										@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, 
										@Header(KafkaHeaders.RECEIVED_PARTITION_ID) String partition,
										@Header(KafkaHeaders.OFFSET) String offset) {

		String requestKey = topic + "-" + partition + "-" + offset;
		
		ProcessingResult processingResult = requestProcessedStatus.get(requestKey);
		
		if (processingResult == null) {
			return;
		}

		switch (topic) {
		case "alehad.messenger.topic.getall":
    	{
    		processingResult.result = messageStoreService.getStore().getMessages();
    		break;
    	}
		case "alehad.messenger.topic.getallby":
		{
			processingResult.result = messageStoreService.getStore().getMessagesBy(params.getFindByAuthor());
			break;
		}
		case "alehad.messenger.topic.getone":
		{
			processingResult.result.add(messageStoreService.getStore().getMessage(params.getFindById()));
			break;
		}
		case "alehad.messenger.topic.addone":
		{
			processingResult.result.add(messageStoreService.getStore().createMessage(params.getMessagePayload()));
			break;
		}
		case "alehad.messenger.topic.update":
		{
			processingResult.result.add(messageStoreService.getStore().updateMessage(params.getFindById(), params.getMessagePayload()));
			break;
		}
		case "alehad.messenger.topic.updateby":
		{
			processingResult.result.add(messageStoreService.getStore().updateMessageBy(params.getFindByAuthor(), params.getMessagePayload()));
			break;
		}
		case "alehad.messenger.topic.delete":
		{
			messageStoreService.getStore().deleteMessage(params.getFindById());
			break;
		}
		case "alehad.messenger.topic.deleteby":
		{
			messageStoreService.getStore().deleteMessagesBy(params.getFindByAuthor());
			break;
		}
		case "alehad.messenger.topic.deleteall":
		{
			messageStoreService.getStore().deleteAll();
			break;
		}
		default:
			break;
		}
		
		processingResult.requestProcessed = true;
	}
}
