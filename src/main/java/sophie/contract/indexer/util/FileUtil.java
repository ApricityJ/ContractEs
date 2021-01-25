package sophie.contract.indexer.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static void reMkDirs(File f) throws IOException {
        rm(f);
        mkdirs(f);
    }

    public static void mkdirs(File f) {
        if (!f.exists()) {
            if (!f.mkdirs()) {
                logger.warn("Fail to mkdir in : {}", f.getAbsolutePath());
            }
        }
    }

    public static void rm(File f) throws IOException {
        if (f.exists()) {
            if (f.isDirectory()) {
                FileUtils.deleteDirectory(f);
            } else {
                FileUtils.deleteQuietly(f);
            }
        }
    }

    public static List<String> toLineList(File f) throws IOException {
        return FileUtils.readLines(f, "utf-8");
    }
}
