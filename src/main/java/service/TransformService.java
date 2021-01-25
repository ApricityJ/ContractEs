package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import util.TransformUtil;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TransformService {

    private static Logger logger = LoggerFactory.getLogger(TransformService.class);

    private static final int CORE_POOL_SIZE = 8;
    private static final int MAX_POOL_SIZE = 16;
    private static final int QUEUE_CAPACITY = 100;
    private static final Long KEEP_ALIVE_TIME = 1L;

    public static void transformDoc2Txt(File srcDocPath, File dstTxtPath) throws TransformerConfigurationException, IOException {

        FileUtil.reMkDirs(dstTxtPath);
        File[] docFiles = srcDocPath.listFiles();

        Transformer transformer = TransformUtil.transformerFactory();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.AbortPolicy());

        for (File docFile : docFiles) {
            executor.execute(() -> {
                TransformUtil.poiDoc2Txt(transformer, docFile.getAbsoluteFile(), dstTxtPath);
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        logger.info("Finished executor transformDoc2Txt...");
    }

    public static void processTxt(File srcTxtPath, File dstTxtPath) throws IOException {

        FileUtil.reMkDirs(dstTxtPath);
        File[] txtFiles = srcTxtPath.listFiles();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadPoolExecutor.CallerRunsPolicy());

        for (File txtFile : txtFiles) {
            executor.execute(() -> {
                TransformUtil.processTxt(txtFile.getAbsoluteFile(), dstTxtPath);
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {

        }
        logger.info("Finished executor txtProcess...");
    }
}
