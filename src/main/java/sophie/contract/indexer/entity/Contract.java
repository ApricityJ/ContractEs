package sophie.contract.indexer.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.contract.indexer.config.Constant;
import sophie.contract.indexer.util.EsUtil;
import sophie.contract.indexer.util.TransformUtil;

import java.io.IOException;

public class Contract {

    private static Logger logger = LoggerFactory.getLogger(Contract.class);
    private static ObjectMapper mapper = new ObjectMapper();
    private static int docID = 0;
    private static RestHighLevelClient client = EsUtil.getClient();
//    private static final Lock lock = new ReentrantLock();

    private String pactSerial; // ID
    private String pactCall; // 审查编号
    private String pactName; // 名称
    private String pactTime; // 创建时间
    private String pactDraftName; // 起草人
    private String pactDeptName; // 起草部门
    private String pactParty; // 简介
    private String pactHlcCall; // 分行审查编号
    private String pactContent; // 正文

    public Contract(String[] initialInfoList) {
        this.pactSerial = initialInfoList[0];
        this.pactCall = initialInfoList[1];
        this.pactName = initialInfoList[2];
        this.pactTime = initialInfoList[3];
        this.pactDraftName = initialInfoList[4];
        this.pactDeptName = initialInfoList[5];
        this.pactParty = initialInfoList[6];
        this.pactHlcCall = initialInfoList[7];
    }

    public IndexRequest indexToEs() throws IOException {
        this.setPactContent();
        IndexRequest indexRequest = new IndexRequest(Constant.ES_INEX_NAME).id(String.valueOf(++docID)).source(mapper.writeValueAsString(this), XContentType.JSON);
//        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
//        logger.info("ID {}, Index {} : {}", docID, this.pactSerial, indexResponse.status());
        logger.info("ID {}, Index {}.", docID, this.pactSerial);
        return indexRequest;
    }

    public void setPactContent() {
        this.pactContent = TransformUtil.readTxt(this.pactSerial);
    }

    public void docToTxt() {
        TransformUtil.docToTxt(this.pactSerial);
        TransformUtil.processTxt(this.pactSerial);
    }

    public String getPactSerial() {
        return pactSerial;
    }

    public String getPactCall() {
        return pactCall;
    }

    public String getPactName() {
        return pactName;
    }

    public String getPactTime() {
        return pactTime;
    }

    public String getPactDraftName() {
        return pactDraftName;
    }

    public String getPactDeptName() {
        return pactDeptName;
    }

    public String getPactParty() {
        return pactParty;
    }

    public String getPactHlcCall() {
        return pactHlcCall;
    }

    public String getPactContent() {
        return pactContent;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "pactSerial='" + pactSerial + '\'' +
                ", pactCall='" + pactCall + '\'' +
                ", pactName='" + pactName + '\'' +
                ", pactTime='" + pactTime + '\'' +
                ", pactDraftName='" + pactDraftName + '\'' +
                ", pactDeptName='" + pactDeptName + '\'' +
                ", pactParty='" + pactParty + '\'' +
                ", pactHlcCall='" + pactHlcCall + '\'' +
                ", pactContent='" + pactContent + '\'' +
                '}';
    }
}
