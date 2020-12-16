package util;

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
import java.nio.file.Path;
import java.nio.file.Paths;

public class TransformUtil {
    private static Logger logger = LoggerFactory.getLogger(TransformUtil.class);

    public static Transformer transformerFactory() throws TransformerConfigurationException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "text");
        return transformer;
    }

    public static void poiDoc2Txt(Transformer transformer, File docFile, File dstPath) {

        try {
            logger.info("Transform docfile : {}", docFile);
            InputStream is = new FileInputStream(docFile);
            HWPFDocument wordDocument = new HWPFDocument(is);
            WordToTextConverter converter = new WordToTextConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            converter.processDocument(wordDocument);
            Path dstFile = Paths.get(dstPath.toString(), docFile.getName().substring(0, docFile.getName().lastIndexOf(".")) + ".txt");
            Writer writer = new FileWriter(dstFile.toString());
            transformer.transform(
                    new DOMSource(converter.getDocument()),
                    new StreamResult(writer));
        } catch (Exception e) {
            logger.info("Something wrong within poiDoc2Txt : {}", e.toString());
        }
    }

    public static void processTxt(File srcFile, File dstPath) {

        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(srcFile));
            bufferedWriter = new BufferedWriter(new FileWriter(Paths.get(dstPath.toString(), srcFile.getName()).toString()));
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
        } catch (IOException e) {
            logger.info("Something wrong in processing txt : {}", e.toString());
        } finally {
            try {
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
            } catch (IOException e) {
                logger.info("Fail to close : {}", e.toString());
            }
        }
    }
}
