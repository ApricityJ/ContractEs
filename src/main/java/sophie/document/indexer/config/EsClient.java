package sophie.document.indexer.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class EsClient {

    private EsClient() {

    }

    private enum Singleton {
        INSTANCE;
        private RestHighLevelClient client;

        Singleton() {
            this.client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost("localhost", 9200, "http")));
        };

        public RestHighLevelClient getClient() {
            return client;
        }
    }

    public static RestHighLevelClient getEsClient() {
        return Singleton.INSTANCE.getClient();
    }
}
