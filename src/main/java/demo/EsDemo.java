package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.EsService;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class EsDemo {
    private static Logger logger = LoggerFactory.getLogger(EsDemo.class);
    private static final String ES_INDEX = "contract_test2";
    private static final String mappings = "{\"properties\": {" +
            "\"pactSerial\":{\"type\": \"text\"}," +
            "\"pactCall\":{\"type\": \"text\"}," +
            "\"pactName\":{\"type\": \"text\"}," +
            "\"pactTime\":{\"type\": \"date\", \"format\": \"yyyy-MM-dd HH:mm:ss\"}," +
            "\"pactDraftName\":{\"type\": \"text\"}," +
            "\"pactDeptName\":{\"type\": \"text\"}," +
            "\"pactParty\":{\"type\": \"text\"}," +
            "\"pactHlcCall\":{\"type\": \"text\"}," +
            "\"pactContent\":{\"type\": \"text\"}" +
            "}}";

    public static void main(String[] args) throws IOException, SQLException, ParseException, InterruptedException {
        long startTime = System.currentTimeMillis();
        EsService esService = new EsService();
//        esService.deleteIndex(ES_INDEX);
//        esService.createIndex(ES_INDEX, mappings);
//        esService.bulkInsert(ES_INDEX);
//        esService.deleteDocAll(ES_INDEX);
//        esService.bulkInsertAsync(ES_INDEX);

        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime) / 1000;
        logger.info("Process Es time used : {}", usedTime);
        esService.close();
    }
}
