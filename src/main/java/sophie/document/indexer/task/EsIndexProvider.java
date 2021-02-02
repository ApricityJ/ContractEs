package sophie.document.indexer.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.document.indexer.contract.Contracts;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EsIndexProvider implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(EsIndexProvider.class);
    private LinkedBlockingQueue<EsIndexTask> linkedBlockingQueue;
    private AtomicBoolean stop;

    public EsIndexProvider(LinkedBlockingQueue<EsIndexTask> linkedBlockingQueue, AtomicBoolean stop) {
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.stop = stop;
    }

    @Override
    public void run() {
        AtomicInteger docID = new AtomicInteger(0);
        Contracts.toContractList().stream().forEach(contract -> {
            contract.setPactContent();
            linkedBlockingQueue.offer(new EsIndexTask(String.valueOf(docID.incrementAndGet()), contract));
        });
        stop.getAndSet(true);
    }
}
