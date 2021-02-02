package sophie.document.indexer.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.document.indexer.util.WordToTxtUtil;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConvertResolver implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ConvertResolver.class);
    private File dstDir;
    private File dstClearDir;
    private LinkedBlockingQueue<File> linkedBlockingQueue;
    private AtomicBoolean stop;

    public ConvertResolver(File dstDir, File dstClearDir, LinkedBlockingQueue<File> linkedBlockingQueue, AtomicBoolean stop) {
        this.dstDir = dstDir;
        this.dstClearDir = dstClearDir;
        this.linkedBlockingQueue = linkedBlockingQueue;
        this.stop = stop;
    }

    @Override
    public void run() {
        while (true) {
            File srcFile = linkedBlockingQueue.poll();
            if (null != srcFile) {
                String fileName = srcFile.getName().substring(0, srcFile.getName().lastIndexOf(".")) + ".txt";
                File dstFile = new File(Paths.get(dstDir.toString(), fileName).toString());
                File dstClearFile = new File(Paths.get(dstClearDir.toString(), fileName).toString());
                WordToTxtUtil.wordToTxt(srcFile, dstFile);
                WordToTxtUtil.processTxt(dstFile, dstClearFile);
                }
            if (linkedBlockingQueue.isEmpty() && stop.get()) {
                logger.info("ConvertResolver : Goodbye World.");
                break;
            }
        }
    }
}
