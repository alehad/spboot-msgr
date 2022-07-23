package msgr.kafka;

import java.util.HashMap;
import java.util.Map;

import msgr.msg.Message;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value("${kafka.bootstrap.address}")
	private String _bootstrapAddress;
	
	@Value("${kafka.group.id}")
	private String _groupId;

    @Bean
    public ConsumerFactory<String, Message> messageConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,  _bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG,       	_groupId);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(Message.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Message> messageKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Message> factory =
        	new ConcurrentKafkaListenerContainerFactory<>();
        	factory.setConsumerFactory(messageConsumerFactory());
        return factory;
    }
}