package msgr.redis;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisMessageRepository extends CrudRepository<RedisMessage, String> {
	List<RedisMessage> findByMessageId(int messageId);
	List<RedisMessage> findByAuthor(String author);
}
