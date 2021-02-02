package sophie.document.indexer.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConvertProvider implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ConvertProvider.class);
    private File srcDir;
    private LinkedBlockingQueue<File> linkedBlockingQueue;
    private AtomicBoolean stop;

    public ConvertProvider(File srcDir, LinkedBlockingQueue<File> linkedBlockingQueue, AtomicBoolean stop) {
        this.srcDir = srcDir;
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.stop = stop;
    }

    @Override
    public void run() {
        Arrays.stream(srcDir.listFiles()).forEach(file -> {
            linkedBlockingQueue.offer(file);
        });
        stop.getAndSet(true);
    }
}
