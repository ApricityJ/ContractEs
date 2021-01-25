package sophie.contract.indexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.contract.indexer.config.Constant;
import sophie.contract.indexer.entity.Contracts;
import sophie.contract.indexer.util.EsUtil;

import java.io.IOException;

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

    public static void main(String[] args) {

/*        try {
            EsUtil.createIndex(ES_INDEX, mappings,ES_SHARDS_NUM, ES_REPLICAS_NUM);
        } catch (IOException e) {
            logger.error("Create index fail : {}", e.toString());
        }*/

        try {
            EsUtil.deleteDocAll(Constant.ES_INEX_NAME);
        } catch (IOException e) {
            logger.error("Delete all docs fail : {}", e.toString());
        }

/*        Contracts.docToTxtThreadPool();
        Contracts.docToTxtParallelStream();*/

        long startTime = System.currentTimeMillis();

        Contracts.indexToEs();

        /*try {
            Contracts.bulkIndexToEs(ES_INDEX);
        } catch (IOException e) {
            logger.error("BulkIndexToEs error : {}", e.toString());
        }*/

        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime) / 1000;
        logger.info("Process Es time used : {}", usedTime);

        try {
            EsUtil.close();
        } catch (IOException e) {
            logger.warn("Close Es client fail : {}", e.toString());
        }
    }
}
