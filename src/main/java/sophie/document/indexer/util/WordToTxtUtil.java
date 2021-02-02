package sophie.document.indexer.util;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.WordToTextConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class WordToTxtUtil {

    private static Logger logger = LoggerFactory.getLogger(WordToTxtUtil.class);
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

    public static void wordToTxt(File srcFile, File dstFile) {
        try {
            logger.info("WordToTxt : {}", srcFile);
            InputStream is = new FileInputStream(srcFile);
            HWPFDocument wordDocument = new HWPFDocument(is);
            WordToTextConverter converter = new WordToTextConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            converter.processDocument(wordDocument);
            Writer writer = new FileWriter(dstFile.toString());
            transformer.transform(
                    new DOMSource(converter.getDocument()),
                    new StreamResult(writer));
        } catch (Exception e) {
            logger.warn("Something wrong within poiWordToTxt : {}", e.toString());
        }
    }

    public static void processTxt(File srcFile, File dstFile) {

        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(srcFile));
            bufferedWriter = new BufferedWriter(new FileWriter(dstFile));
            logger.info("ProcessTxt : {}", srcFile);
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
            logger.warn("Something wrong in processing txt : {}", e.toString());
        } finally {
            if (null != bufferedWriter) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    logger.warn("Fail to close : {}", e.toString());
                }
            }
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    logger.info("Fail to close : {}", e.toString());
                }
            }
        }
    }
}

