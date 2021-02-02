package sophie.document.indexer.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.document.indexer.Indexer.IIndexer;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class EsIndexResolver implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(EsIndexResolver.class);
    private IIndexer indexer;
    private String indexName;
    private LinkedBlockingQueue<EsIndexTask> linkedBlockingQueue;
    private AtomicBoolean stop;

    public EsIndexResolver(IIndexer indexer, String indexName, LinkedBlockingQueue<EsIndexTask> linkedBlockingQueue, AtomicBoolean stop) {
        this.indexer = indexer;
        this.indexName = indexName;
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.stop = stop;
    }

    @Override
    public void run() {
        while (true) {
            EsIndexTask esIndexTask = linkedBlockingQueue.poll();
            if (null != esIndexTask) {
                try {
                    indexer.insertDoc(indexName, esIndexTask);
                } catch (IOException e) {
                    logger.error("Insert Document error : {}", e.toString());
                }
            }
            if (linkedBlockingQueue.isEmpty() && stop.get()) {
                logger.info("EsIndexResolver : Goodbye World.");
                break;
            }
        }
    }
}
