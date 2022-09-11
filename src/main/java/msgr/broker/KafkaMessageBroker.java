package msgr.broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
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

//	@Autowired
//	private SimpleMessageBroker simpleMessageBroker;
	
	@Autowired
	private MessageStoreService messageStoreService;
	
//	List<Message> result = new ArrayList<Message>();
	
//	private HashMap<String, Boolean> requestProcessedStatus = new HashMap<String, Boolean>();
	
	class ProcessingResult {
		Boolean requestProcessed = false;
		List<Message> result 	 = new ArrayList<Message>();
	}
	
	private HashMap<String, ProcessingResult> requestProcessedStatus = new HashMap<String, ProcessingResult>();
	
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
//        
//	    future.addCallback(new ListenableFutureCallback<SendResult<String, MessageRequestParams>>() {
//	        @Override
//	        public void onSuccess(SendResult<String, MessageRequestParams> result) {
//	            System.out.println("Sent message=[" + 
//	            				  (params.getMessagePayload() == null ? requestTopic.topic : params.getMessagePayload().getMessage()) + 
//	            				  "] with offset=[" + result.getRecordMetadata().offset() + "]");
//
//	        }
//	        @Override
//	        public void onFailure(Throwable ex) {
//	            System.out.println("Unable to send message=[" + 
//	            				  (params.getMessagePayload() == null ? requestTopic.topic : params.getMessagePayload().getMessage()) + 
//	            				  "] due to : " + ex.getMessage());
//	        }
//	    });
	}
	
	@Override
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	// generate simple request correlation id 
	private static String generateRequestCorrelationId(String topic, int partition, long offset) {
		return topic + "-" + partition + "-" + offset;
	}

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		String requestKey = postRequest(topic, params);
		
//	    while (!requestProcessedStatus.containsKey(requestKey) && !requestProcessedStatus.get(requestKey).requestProcessed) {
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
		
//		if (requestProcessedStatus.containsKey(requestKey)) {
//			ProcessingResult processingResult = new ProcessingResult();
//			processingResult.requestProcessed = true;
//			processingResult.result = result;
//			requestProcessedStatus.put(requestKey, processingResult);
//		}
		
//		return result;
	}


//	@KafkaListener(
//     topics = "alehad.messenger.topic.getall", 
//     containerFactory = "messageParamsKafkaListenerContainerFactory")
//	public void getAllMessagesListener(MessageRequestParams params) {
//		 System.out.println("got alehad.messenger.topic.getall topic to process");
//	}
//	
//	@KafkaListener(
//		     topics = "alehad.messenger.topic.addone", 
//		     containerFactory = "messageParamsKafkaListenerContainerFactory")
//	public void addOneMessageListener(MessageRequestParams params) {
//		 System.out.println("got alehad.messenger.topic.addone topic to process, message = " + params.getMessagePayload().getMessage());
//	}
}
