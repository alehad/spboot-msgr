package msgr.elastic;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.HttpHeaders;

@Configuration
@EnableElasticsearchRepositories(basePackages = "msgr.elastic")
@ComponentScan(basePackages = {"msgr.elastic", "msgr.init", "msgr.svc", "msgr.cache", "msgr.app"})
public class ESConfig extends AbstractElasticsearchConfiguration {

	@Value("${spring.data.elasticsearch.host:localhost}")
	private String _host;
	
	@Value("${spring.data.elasticsearch.port:9200}")
	private String _port;
	
	@Override
	public RestHighLevelClient elasticsearchClient() {
		/*
        ClientConfiguration clientConfiguration = ClientConfiguration.builder().connectedTo(_host + ":" + _port).build();

        return RestClients.create(clientConfiguration).rest();
        */
		//Below 3 lines are added to make elastic client version 7.17.4 compatible with elastic server version 8x
		 HttpHeaders compatibilityHeaders = new HttpHeaders();
		        compatibilityHeaders.add("Accept", "application/vnd.elasticsearch+json;compatible-with=7");
		        compatibilityHeaders.add("Content-Type", "application/vnd.elasticsearch+json;"
		                + "compatible-with=7");

		        final ClientConfiguration clientConfiguration =
		                ClientConfiguration
		                        .builder()
		                        .connectedTo(_host + ":" + _port)
		      //                  .usingSsl()
		      //                  .withBasicAuth(esConnectionDetails.getUsername(), esConnectionDetails.getPassword())
		                        .withDefaultHeaders(compatibilityHeaders)    // this variant for imperative code
		                        .build();

		return RestClients.create(clientConfiguration).rest();
    }

}
