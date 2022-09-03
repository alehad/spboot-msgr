package msgr.broker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import msgr.msg.Message;

@Service
public class KafkaMessageBroker implements IMessageBroker {

	//@Autowired
	//private SimpleMessageBroker simpleMessageBroker;
	
	@Autowired
	private KafkaTemplate<String, MessageRequestParams> kafkaTemplate;

	public void sendMessage(MessageRequestTopic requestTopic, MessageRequestParams params) {
        
	    ListenableFuture<SendResult<String, MessageRequestParams>> future = kafkaTemplate.send(requestTopic.topic, params);
		
	    future.addCallback(new ListenableFutureCallback<SendResult<String, MessageRequestParams>>() {
	        @Override
	        public void onSuccess(SendResult<String, MessageRequestParams> result) {
	            System.out.println("Sent message=[" + 
	            				  (params.getMessagePayload() == null ? requestTopic.topic : params.getMessagePayload().getMessage()) + 
	            				  "] with offset=[" + result.getRecordMetadata().offset() + "]");
	        }
	        @Override
	        public void onFailure(Throwable ex) {
	            System.out.println("Unable to send message=[" + 
	            				  (params.getMessagePayload() == null ? requestTopic.topic : params.getMessagePayload().getMessage()) + 
	            				  "] due to : " + ex.getMessage());
	        }
	    });
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

	@Override
	public List<Message> handleRequest(MessageRequestTopic topic, MessageRequestParams params) {
		// TODO Auto-generated method stub
		sendMessage(topic, params);
		return null;
	}

	@KafkaListener(
     topics = "alehad.messenger.topic.getall", 
     containerFactory = "messageParamsKafkaListenerContainerFactory")
	public void getAllMessagesListener(MessageRequestParams params) {
		 System.out.println("got alehad.messenger.topic.getall topic to process");
	}
	
	@KafkaListener(
		     topics = "alehad.messenger.topic.addone", 
		     containerFactory = "messageParamsKafkaListenerContainerFactory")
	public void addOneMessageListener(MessageRequestParams params) {
		 System.out.println("got alehad.messenger.topic.addone topic to process, message = " + params.getMessagePayload().getMessage());
	}
}
