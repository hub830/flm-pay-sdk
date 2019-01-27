package top.lemno.pay.flm.bean;

import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 1.skb -> flm 发送重置密钥申请
 * 2.flm -> skb 接受重置密钥响应，接受重置密钥交易请求
 * 3.解析接受重置密钥交易请求，获取96域数据
 * 4.调用加密机 5.2.导入密钥<0xD102> 指令：<示例>D102081000511232E6B1E156477D0B</示例> <主密钥>32E6B1E156477D0B</主密钥>
 * 5.接受加密机结果获取LMK mak工作密钥 <示例>4108726146AC7EDBBF81C715BE195AB796AF</示例> <工作密钥>726146AC7EDBBF81</工作密钥>
 * 6.随后计算mac均使用5.20. 计算 MAC<0xD132> 来计算
 * <p>
 * 重复步骤4，5获得PINkey
 * 5.2.导入密钥<0xD102> 指令：<示例>D102081000511116B717610A96A5AE</示例> <主密钥>16B717610A96A5AE</主密钥>
 * 接受加密机结果获取LMK mak工作密钥 <示例>4108D68162385DE7004BFBC4103EB031B8F3</示例> <工作密钥>D68162385DE7004B</工作密钥>
 * 7.调用D122 对 PIN 加密
 */
@Component
public abstract class AbstractPayBean {

    public final static int REQUEST_TYPE_LENGTH = 4;

    public final static int HEAD_LENGTH = 46;

    /**
     * 交易类型
     */
    private String requestType;
    /**
     * 报文头长度
     */
    @Value("${trans.headLength}")
    private int headLength;
    /**
     * 版本号
     */
    @Value("${trans.version}")
    private String version;
    /**
     * 报文总长度
     */
    private String totalLength;
    /**
     * 目标id
     */
    @Value("${trans.destinationId}")
    private String destinationId;
    /**
     * 源id
     */
    @Value("${trans.sourceId}")
    private String sourceId;
    /**
     * 预留字段
     */
    @Value("${trans.remark}")
    private String remark;
    /**
     * 批次号
     */
    @Value("${trans.batchNum}")
    private String batchNum;
    /**
     * 交易信息
     */
    @Value("${trans.transInfo}")
    private String transInfo;
    /**
     * 用户信息
     */
    @Value("${trans.userInfo}")
    private String userInfo;
    /**
     * 拒绝码
     */
    @Value("${trans.rejectCode}")
    private String rejectCode;


    /**
     * F18.商户分类编码(MCC) M
     */
    private String mcc;
    /**
     * F32.受理方标识码
     */
    private String receiveIdCode;
    /**
     * F41.终端号
     */
    private String terminalNum;
    /**
     * F42.商户号
     */
    private String customerNo;

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getReceiveIdCode() {
        return receiveIdCode;
    }

    public void setReceiveIdCode(String receiveIdCode) {
        this.receiveIdCode = receiveIdCode;
    }

    public String getTerminalNum() {
        return terminalNum;
    }

    public void setTerminalNum(String terminalNum) {
        this.terminalNum = terminalNum;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getHeadLength() {
        return headLength;
    }

    public void setHeadLength(int headLength) {
        this.headLength = headLength;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(String totalLength) {
        this.totalLength = totalLength;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getTransInfo() {
        return transInfo;
    }

    public void setTransInfo(String transInfo) {
        this.transInfo = transInfo;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getRejectCode() {
        return rejectCode;
    }

    public void setRejectCode(String rejectCode) {
        this.rejectCode = rejectCode;
    }

    /**
     * 生成请求报文头
     *
     * @param totalLength
     * @return
     */
    public  Map<Integer, String> createHeadMap(String totalLength) {
        Map<Integer, String> headMap = new TreeMap<>();
        headMap.put(1, "46");
        headMap.put(2, "1");
        headMap.put(3, totalLength);
        headMap.put(4, "62100000");
        /**测试环境源id*/
//        headMap.put(5, "60191241");
        /**生产环境源id*/
        headMap.put(5, "48501111");
        headMap.put(6, "000");
        headMap.put(7, "0");
        headMap.put(8, "00000000");
        headMap.put(9, "0");
        headMap.put(10, "00000");
        return headMap;
    }

    /**
     * 创建报文体
     *
     * @return
     */
    public abstract Map<Integer, String> createBodyMap();

    /**
     * 转换报文内容
     *
     * @return
     */
}
