package sophie.contract.indexer.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.contract.indexer.config.Constant;
import sophie.contract.indexer.util.EsUtil;
import sophie.contract.indexer.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Contracts {

    private static Logger logger = LoggerFactory.getLogger(Contracts.class);
    private static List<Contract> contractList;
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAX_POOL_SIZE = 16;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;
    private static RestHighLevelClient client = EsUtil.getClient();
    private static ObjectMapper mapper = new ObjectMapper();

    private static ExecutorService esExecutor = Executors.newFixedThreadPool(2);
    public static BlockingDeque<IndexRequest> indexRequestsQueue = new LinkedBlockingDeque<>();
    private static final int INDEX_CONSUMER_SIZE = 7;

    static {
        try {
            contractList = FileUtil.toLineList(new File(Constant.CONTRACT_RECORD_FILE_PATH))
                    .stream().map(s -> new Contract(s.split("\\|!"))).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Wrong with contract record file : {} : {}", Constant.CONTRACT_RECORD_FILE_PATH, e.toString());
        }
    }

    public static List<Contract> toContractList() {
        return contractList;
    }

    public static void bulkIndexToEs(String indexName) throws IOException {
        int docID = 0;

        BulkRequest bulkRequest = new BulkRequest();
        BulkResponse bulkResponse;
        for (Contract contract : contractList) {
            contract.setPactContent();
            bulkRequest.add(new IndexRequest(indexName).id(String.valueOf(++docID)).source(mapper.writeValueAsString(contract), XContentType.JSON));
            if (docID % 1000 == 0) {
                bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                logger.info("BulkInsert has failures : {}", bulkResponse.hasFailures());
                bulkRequest = new BulkRequest();
            }
        }
        if (docID % 1000 != 0) {
            bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            logger.info("BulkInsert has failures : {}", bulkResponse.hasFailures());
        }
    }

    public static void indexToEs() {
//        contractList.stream().forEach(contract -> {
//            try {
//                contract.indexToEs();
//            } catch (IOException e) {
//                logger.error("IndexToEs : {}", e.toString());
//            }
//        });

        esExecutor.submit(() -> {
            contractList.stream().forEach(contract -> {
                try {
                    indexRequestsQueue.offer(contract.indexToEs());
                } catch (IOException e) {
                    logger.error("IndexToEs producer error : {}", e.toString());
                }
            });
        });

        esExecutor.submit(() -> {
            while(true) {
                IndexRequest indexRequest = indexRequestsQueue.poll();
                logger.info("indexRequest : {}", indexRequest);
                if (indexRequest != null) {
                    try {
                        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
                        logger.info("IndexResaponse : {}", indexResponse);
                    } catch (IOException e) {
                        logger.error("IndexToEs consumer error : {}", e.toString());
                    }
                }
            }
        });

//        for (int i = 0; i < INDEX_CONSUMER_SIZE; i++) {
//            esExecutor.execute(new Consumer(indexRequestsQueue));
//        }
    }

    public static void docToTxtParallelStream() {
        try {
            FileUtil.reMkDirs(new File(Constant.CONTRACT_TXT_ORIGIN_DIR_PATH));
            FileUtil.reMkDirs(new File(Constant.CONTRACT_TXT_CLEAR_DIR_PATH));
        } catch (IOException e) {
            logger.error("DocToTxt make dir error : {}", e.toString());
        }
        contractList.stream().parallel().forEach(contract -> {
            contract.docToTxt();
        });
    }

    public static void docToTxtThreadPool() {
        try {
            FileUtil.reMkDirs(new File(Constant.CONTRACT_TXT_ORIGIN_DIR_PATH));
            FileUtil.reMkDirs(new File(Constant.CONTRACT_TXT_CLEAR_DIR_PATH));
        } catch (IOException e) {
            logger.error("DocToTxt make dir error : {}", e.toString());
        }

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());

        for (Contract contract : contractList) {
            executor.execute(() -> {
                contract.docToTxt();
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        logger.info("Finished indexToEsThreadPool...");
    }
}
