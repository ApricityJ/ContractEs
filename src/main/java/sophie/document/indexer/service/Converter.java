package sophie.document.indexer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.document.indexer.config.Constant;
import sophie.document.indexer.task.ConvertProvider;
import sophie.document.indexer.task.ConvertResolver;
import sophie.document.indexer.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Converter {

    private static Logger logger = LoggerFactory.getLogger(Converter.class);
    private static final String wordDir = Constant.CONTRACT_WORD_DIR_PATH;
    private static final String txtDir = Constant.CONTRACT_TXT_ORIGIN_DIR_PATH;
    private static final String txtClearDir = Constant.CONTRACT_TXT_CLEAR_DIR_PATH;

    private static final int CONSUMER_SIZE = 7;
    private static ExecutorService executor = Executors.newFixedThreadPool(CONSUMER_SIZE + 1);
    private static LinkedBlockingQueue<File> taskQueue = new LinkedBlockingQueue<>();
    public static volatile AtomicBoolean stop = new AtomicBoolean(false);

    public static void main(String[] args) {

        File srcDir = new File(wordDir);
        File dstDir = new File(txtDir);
        File dstClearDir = new File(txtClearDir);
        try {
            FileUtil.reMkDirs(dstDir);
            FileUtil.reMkDirs(dstClearDir);
        } catch (IOException e) {
            logger.error("WordToTxt mkdir error : {}", e.toString());
        }

        long startTime = System.currentTimeMillis();

        ConvertProvider producer = new ConvertProvider(srcDir, taskQueue, stop);
        executor.execute(producer);
        for (int i = 0; i < CONSUMER_SIZE; i++) {
            executor.execute(new ConvertResolver(dstDir, dstClearDir, taskQueue, stop));
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        logger.info("Convert task done...");

        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime) / 1000;
        logger.info("Process time used : {}", usedTime);
    }
}
