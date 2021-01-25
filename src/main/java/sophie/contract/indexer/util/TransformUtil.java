package sophie.contract.indexer.util;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.contract.indexer.config.Constant;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TransformUtil {

    private static Logger logger = LoggerFactory.getLogger(TransformUtil.class);
    private static Transformer transformer;

    static {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            logger.error("TransformerFactory exception : {}", e.toString());
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "text");
    }

    public static void docToTxt(String docFileName) {
        try {
            logger.info("DocToTxt : {}", docFileName);
            File docFile = new File(Paths.get(Constant.CONTRACT_DOC_DIR_PATH,docFileName + ".doc").toString());
            InputStream is = new FileInputStream(docFile);
            HWPFDocument wordDocument = new HWPFDocument(is);
            WordToTextConverter converter = new WordToTextConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            converter.processDocument(wordDocument);
            Path dstFile = Paths.get(Constant.CONTRACT_TXT_ORIGIN_DIR_PATH, docFileName + ".txt");
            Writer writer = new FileWriter(dstFile.toString());
            transformer.transform(
                    new DOMSource(converter.getDocument()),
                    new StreamResult(writer));
        } catch (Exception e) {
            logger.info("Something wrong within docToTxt : {}", e.toString());
        }
    }

    public static void processTxt(String txtFileName) {

        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File srcFile = new File(Paths.get(Constant.CONTRACT_TXT_ORIGIN_DIR_PATH,txtFileName + ".txt").toString());
            File dstFile = new File(Paths.get(Constant.CONTRACT_TXT_CLEAR_DIR_PATH,txtFileName + ".txt").toString());
            bufferedReader = new BufferedReader(new FileReader(srcFile));
            bufferedWriter = new BufferedWriter(new FileWriter(dstFile));
            logger.info("ProcessTxt : {}", txtFileName);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.equals("")) {
                    continue;
                }
                str = str.replaceAll("\\s", "");
                bufferedWriter.write(str);
                bufferedWriter.write("\n");
            }
            bufferedWriter.flush();
        } catch (IOException e) {
            logger.info("Something wrong in processing txt : {}", e.toString());
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    logger.info("Fail to close : {}", e.toString());
                }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.info("Fail to close : {}", e.toString());
                }
            }
        }
    }

    public static String readTxt(String txtFileName) {
        File file = new File(Paths.get(Constant.CONTRACT_TXT_CLEAR_DIR_PATH,txtFileName + ".txt").toString());
        BufferedReader bufferedReader = null;
        StringBuffer res = new StringBuffer();
        String str;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((str = bufferedReader.readLine()) != null) {
                res.append(str + '\n');
            }
        } catch (FileNotFoundException e) {
            logger.info("File not exist : {}", txtFileName);
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

