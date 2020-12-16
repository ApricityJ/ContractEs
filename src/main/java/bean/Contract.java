package bean;

public class Contract {
    private String pactSerial; // ID
    private String pactCall; // 审查编号
    private String pactName; // 名称
    private String pactTime; // 创建时间
    private String pactDraftName; // 起草人
    private String pactDeptName; // 起草部门
    private String pactParty; // 简介
    private String pactHlcCall; // 分行审查编号
    private String pactContent; // 正文

    public Contract() {
    }

    public String getPactSerial() {
        return pactSerial;
    }

    public void setPactSerial(String pactSerial) {
        this.pactSerial = pactSerial;
    }

    public String getPactCall() {
        return pactCall;
    }

    public void setPactCall(String pactCall) {
        this.pactCall = pactCall;
    }

    public String getPactName() {
        return pactName;
    }

    public void setPactName(String pactName) {
        this.pactName = pactName;
    }

    public String getPactTime() {
        return pactTime;
    }

    public void setPactTime(String pactTime) {
        this.pactTime = pactTime;
    }

    public String getPactDraftName() {
        return pactDraftName;
    }

    public void setPactDraftName(String pactDraftName) {
        this.pactDraftName = pactDraftName;
    }

    public String getPactDeptName() {
        return pactDeptName;
    }

    public void setPactDeptName(String pactDeptName) {
        this.pactDeptName = pactDeptName;
    }

    public String getPactParty() {
        return pactParty;
    }

    public void setPactParty(String pactParty) {
        this.pactParty = pactParty;
    }

    public String getPactHlcCall() {
        return pactHlcCall;
    }

    public void setPactHlcCall(String pactHlcCall) {
        this.pactHlcCall = pactHlcCall;
    }

    public String getPactContent() {
        return pactContent;
    }

    public void setPactContent(String pactContent) {
        this.pactContent = pactContent;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "pactSerial='" + pactSerial + '\'' +
                ", pactCall='" + pactCall + '\'' +
                ", pactName='" + pactName + '\'' +
                ", pactTime=" + pactTime +
                ", pactDraftName='" + pactDraftName + '\'' +
                ", pactDeptName='" + pactDeptName + '\'' +
                ", pactParty='" + pactParty + '\'' +
                ", pactHlcCall='" + pactHlcCall + '\'' +
                ", pactContent='" + pactContent + '\'' +
                '}';
    }
}
