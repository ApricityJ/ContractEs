package sophie.document.indexer.util;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    public static String readTxt(File file) {
        BufferedReader bufferedReader = null;
        StringBuffer res = new StringBuffer();
        String str;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((str = bufferedReader.readLine()) != null) {
                res.append(str + '\n');
            }
        } catch (FileNotFoundException e) {
            logger.info("File not exist : {}", file);
        } catch (IOException e) {
            logger.info("Something wrong when readTxt {} : {}", file.getName(), e.toString());
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.info("Fail to close : {}", e.toString());
                }
            }
        }
        return res.toString();
    }
}
