package sophie.document.indexer.indexer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
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
import sophie.document.indexer.task.EsIndexTask;

import java.io.IOException;

public class EsIndexerImpl implements IIndexer {

    private static Logger logger = LoggerFactory.getLogger(EsIndexerImpl.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private RestHighLevelClient client;

    public EsIndexerImpl(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void createIndex(String indexName, String mappings, int numOfShards, int numOfReplicas) throws IOException {
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

    @Override
    public void deleteIndex(String indexName) throws IOException {
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

    @Override
    public void insertDoc(String indexName, EsIndexTask task) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName).id(task.getId()).source(mapper.writeValueAsString(task.getDocument()), XContentType.JSON);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        logger.info("InsertDoc status : {}", indexResponse.status());
    }

    @Override
    public void insertDocBulk() {

    }

    @Override
    public void deleteDoc(String indexName, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        logger.info("DeleteDoc status : {}", deleteResponse.status());
    }


    @Override
    public void deleteDocAll(String indexName) throws IOException {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName);
        deleteByQueryRequest.setQuery(new MatchAllQueryBuilder());
        BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        logger.info("DeleteDocAll status : {}", bulkByScrollResponse.getStatus());
    }

    @Override
    public boolean isExistIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}
