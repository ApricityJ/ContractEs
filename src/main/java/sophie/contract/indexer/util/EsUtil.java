package sophie.contract.indexer.util;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EsUtil {
    private static Logger logger = LoggerFactory.getLogger(EsUtil.class);
    private static RestHighLevelClient client;

    static {
        client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));
    }

    public static RestHighLevelClient getClient() {
        return client;
    }

    public static void createIndex(String indexName, String mappings, int numOfShards, int numOfReplicas) throws IOException {
        if (isExistIndex(indexName)) {
            logger.warn("{} : Index has already existed.", indexName);
        } else {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.settings(Settings.builder().put("index.number_of_shards", numOfShards)
                    .put("index.number_of_replicas", numOfReplicas));
            if (!"".equals(mappings)) {
                request.mapping(mappings, XContentType.JSON);
            }
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            if (createIndexResponse.isAcknowledged() && createIndexResponse.isShardsAcknowledged()) {
                logger.info("{} : Create index success.", indexName);
            } else {
                logger.info("{} : Create index fail.", indexName);
            }
        }
    }

    public static void deleteIndex(String indexName) throws IOException {
        if (!isExistIndex(indexName)) {
            logger.warn("{} : Index not exist.", indexName);
        } else {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse acknowledgedResponse = client.indices().delete(request, RequestOptions.DEFAULT);
            if (acknowledgedResponse.isAcknowledged()) {
                logger.info("{} : Delete index success.", indexName);
            } else {
                logger.info("{} : Delete index fail.", indexName);
            }
        }
    }

    public static boolean isExistIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public static void deleteDoc(String indexName, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        logger.info("DeleteDoc status : {}", deleteResponse.status());
    }

    public static void deleteDocAll(String indexName) throws IOException {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName);
        deleteByQueryRequest.setQuery(new MatchAllQueryBuilder());
        BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        logger.info("DeleteDocAll status : {}", bulkByScrollResponse.getStatus());
    }

    public static void close() throws IOException {
        client.close();
    }
}

