package msgr.elastic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ESMessageRepository extends ElasticsearchRepository<ESMessage, String> {
	Page<ESMessage> findByAuthor(String author, Pageable pageable);
	Page<ESMessage> findByMessageId(int messageId, Pageable pageable);
}
