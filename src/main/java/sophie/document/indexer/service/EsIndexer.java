package sophie.document.indexer.service;

import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.document.indexer.indexer.EsIndexerImpl;
import sophie.document.indexer.indexer.IIndexer;
import sophie.document.indexer.config.Constant;
import sophie.document.indexer.config.EsClient;
import sophie.document.indexer.task.EsIndexProvider;
import sophie.document.indexer.task.EsIndexResolver;
import sophie.document.indexer.task.EsIndexTask;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EsIndexer {

    private static Logger logger = LoggerFactory.getLogger(EsIndexer.class);

    private static final String ES_INDEX = Constant.ES_INEX_NAME;
    private static final int ES_SHARDS_NUM = Constant.ES_SHARDS_NUM;
    private static final int ES_REPLICAS_NUM = Constant.ES_REPLICAS_NUM;
    private static final String mappings = "{\"properties\": {" +
            "\"pactSerial\":{\"type\": \"text\"}," +
            "\"pactCall\":{\"type\": \"text\"}," +
            "\"pactName\":{\"type\": \"text\"}," +
            "\"pactTime\":{\"type\": \"text\"}," +
            "\"pactDraftName\":{\"type\": \"text\"}," +
            "\"pactDeptName\":{\"type\": \"text\"}," +
            "\"pactParty\":{\"type\": \"text\"}," +
            "\"pactHlcCall\":{\"type\": \"text\"}," +
            "\"pactContent\":{\"type\": \"text\"}" +
            "}}";
    private static RestHighLevelClient client = EsClient.getEsClient();

    private static final int CONSUMER_SIZE = 7;
    private static ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_SIZE + 1);
    private static LinkedBlockingQueue<EsIndexTask> taskQueue = new LinkedBlockingQueue<>();
    public static volatile AtomicBoolean stop = new AtomicBoolean(false);

    public static void main(String[] args) {

        IIndexer indexer = new EsIndexerImpl(client);
        long startTime = System.currentTimeMillis();

//        try {
//            indexer.deleteDocAll(ES_INDEX);
//            indexer.deleteIndex(ES_INDEX);
//            indexer.createIndex(ES_INDEX, mappings, ES_SHARDS_NUM, ES_REPLICAS_NUM);
//        } catch (IOException e) {
//            logger.error("Prepare index fail : {}", e.toString());
//        }

        EsIndexProvider producer = new EsIndexProvider(taskQueue, stop);
        executor.execute(producer);
        for (int i = 0; i < CONSUMER_SIZE; i++) {
            executor.execute(new EsIndexResolver(indexer, ES_INDEX, taskQueue, stop));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        logger.info("EsIndex task done...");

        // remember finally close
        try {
            indexer.close();
        } catch (IOException e) {
            logger.error("Close index fail : {}", e.toString());
        }

        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime) / 1000;
        logger.info("Process time used : {}", usedTime);
    }
}
