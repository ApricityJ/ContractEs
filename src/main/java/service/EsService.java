package service;

import bean.Contract;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.Constant;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.Cancellable;
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
import util.EsUtil;
import util.FileUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class EsService {

    private static Logger logger = LoggerFactory.getLogger(EsService.class);
    private RestHighLevelClient client = EsUtil.getClient();
    private static final int NUM_OF_SHARDS = Constant.NUM_OF_SHARDS;
    private static final int NUM_OF_REPLICAS = Constant.NUM_OF_REPLICAS;
    private static final String TXT_CLEAR_PATH = Constant.TXT_CLEAR_PATH;

    public boolean createIndex(String indexName) throws IOException {
        return this.createIndex(indexName, "", this.NUM_OF_SHARDS, this.NUM_OF_REPLICAS);
    }

    public boolean createIndex(String indexName, String mappings) throws IOException {
        return this.createIndex(indexName, mappings, this.NUM_OF_SHARDS, this.NUM_OF_REPLICAS);
    }

    public boolean createIndex(String indexName, String mappings, int numOfShards, int numOfReplicas) throws IOException {
        if (existIndex(indexName)) {
            logger.warn("{} : Index has already existed.", indexName);
            return false;
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
                return true;
            } else {
                logger.info("{} : Create index fail.", indexName);
                return false;
            }
        }
    }

    public boolean deleteIndex(String indexName) throws IOException {
        if (!existIndex(indexName)) {
            logger.warn("{} : Index not exist.", indexName);
            return false;
        } else {
            DeleteIndexRequest request = new DeleteIndexRequest(indexName);
            AcknowledgedResponse acknowledgedResponse = client.indices().delete(request, RequestOptions.DEFAULT);
            if (acknowledgedResponse.isAcknowledged()) {
                logger.info("{} : Delete index success.", indexName);
                return true;
            } else {
                logger.info("{} : Delete index fail.", indexName);
                return false;
            }
        }
    }

    public boolean existIndex(String indexName) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }

    public void bulkInsert(String indexName) throws SQLException, IOException, ParseException {
        ContractSqlService contractSqlService = new ContractSqlService();
        List<Contract> contracts = contractSqlService.queryAll();
        int docID = 0;

        ObjectMapper mapper = new ObjectMapper();
        BulkRequest bulkRequest = new BulkRequest();
        BulkResponse bulkResponse;
        for (Contract contract : contracts) {
            contract.setPactContent(FileUtil.readTxt(Paths.get(TXT_CLEAR_PATH, contract.getPactSerial() + ".txt").toString()));
            bulkRequest.add(new IndexRequest(indexName).id(String.valueOf(++docID)).source(mapper.writeValueAsString(contract), XContentType.JSON));
            if (docID%1000 == 0) {
                bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                logger.info("BulkInsert has failures : {}", bulkResponse.hasFailures());
                bulkRequest = new BulkRequest();
            }
        }
        if (docID%1000 != 0) {
            bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            logger.info("BulkInsert has failures : {}", bulkResponse.hasFailures());
        }
    }

    public void bulkInsertAsync(String indexName) throws SQLException, IOException, ParseException, InterruptedException {
        ContractSqlService contractSqlService = new ContractSqlService();
        List<Contract> contracts = contractSqlService.queryAll();
        int docID = 0;

        ObjectMapper mapper = new ObjectMapper();
        BulkRequest bulkRequest = new BulkRequest();

        ActionListener<BulkResponse> listener = new ActionListener<BulkResponse>() {
            @Override
            public void onResponse(BulkResponse bulkResponse) {
                logger.info("BulkInsert result : {}", bulkResponse.hasFailures());
            }

            @Override
            public void onFailure(Exception e) {
                logger.info("BulkInsert exception : {}", e.toString());
            }
        };

        for (Contract contract : contracts) {
            contract.setPactContent(FileUtil.readTxt(Paths.get(TXT_CLEAR_PATH, contract.getPactSerial() + ".txt").toString()));
            bulkRequest.add(new IndexRequest(indexName).id(String.valueOf(++docID)).source(mapper.writeValueAsString(contract), XContentType.JSON));
            if (docID%1000 == 0) {
                client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, listener);
                bulkRequest = new BulkRequest();
            }
        }
        if (docID%1000 != 0) {
            client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, listener);
        }
        Thread.sleep(1000);
    }

    public void deleteDoc(String indexName, String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);
        logger.info("DeleteDoc status : {}", deleteResponse.status());
    }

    public void deleteDocAll(String indexName) throws IOException {
        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest(indexName);
        deleteByQueryRequest.setQuery(new MatchAllQueryBuilder());
        BulkByScrollResponse bulkByScrollResponse = client.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
        logger.info("DeleteDocAll status : {}", bulkByScrollResponse.getStatus());
    }

    public void close() throws IOException {
        client.close();
    }

}

