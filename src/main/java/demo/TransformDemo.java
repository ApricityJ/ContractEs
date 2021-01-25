package demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.TransformService;

import javax.xml.transform.TransformerConfigurationException;
import java.io.File;
import java.io.IOException;

public class TransformDemo {
    private static Logger logger = LoggerFactory.getLogger(TransformDemo.class);

    public static void main(String[] args) throws IOException, TransformerConfigurationException {

        long startTime = System.currentTimeMillis();

        File srcDocPath = new File("F:\\task\\contract\\contract");
        File dstTxtPath = new File("F:\\task\\contract\\doc2txt_new");
        File dstTxtProcessedPath = new File("F:\\task\\contract\\txt_clear");

        TransformService.transformDoc2Txt(srcDocPath, dstTxtPath);
        long endTime = System.currentTimeMillis();
        long usedTime = (endTime - startTime) / 1000;
        logger.info("TransformDoc2Txt time used : {}", usedTime);

//        startTime = System.currentTimeMillis();
//        TransformService.processTxt(dstTxtPath, dstTxtProcessedPath);
//        endTime = System.currentTimeMillis();
//        usedTime = (endTime - startTime) / 1000;
//        logger.info("ProcessTxt time used : {}", usedTime);
    }
}
