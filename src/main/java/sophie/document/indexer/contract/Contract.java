package sophie.document.indexer.contract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.document.indexer.config.Constant;
import sophie.document.indexer.util.FileUtil;

import java.io.File;
import java.nio.file.Paths;

public class Contract extends Document {

    private static Logger logger = LoggerFactory.getLogger(Contract.class);

    private String pactSerial; // 合同ID
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

    public void setPactContent() {
        File file = new File(Paths.get(Constant.CONTRACT_TXT_CLEAR_DIR_PATH, this.pactSerial + ".txt").toString());
        this.pactContent = FileUtil.readTxt(file);
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
