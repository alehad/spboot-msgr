package msgr.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisMessageRepository extends CrudRepository<RedisMessage, String> {}
