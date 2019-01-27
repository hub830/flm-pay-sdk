package top.lemno.pay.flm.bean;

import java.util.Map;
import java.util.TreeMap;
import org.springframework.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * POS/NFC 请求Bean
 * 
 * @author 元歌
 */
@Data@EqualsAndHashCode(callSuper=false)
@ToString
public class TransPayBean extends AbstractPayBean {

  public final static String REQUEST_TYPE = "0200";

  public TransPayBean() {
    this.setRequestType(REQUEST_TYPE);
  }

  /**
   * F2.主账号
   */
  private String acctNum;
  /**
   * F3.交易处理码
   */
  private String processingCode;
  /**
   * F4.交易金额
   */
  private String amount;
  /**
   * F7.交易传输时间 MMddhhmmss
   */
  private String transTime;
  /**
   * F11.系统跟踪号
   */
  private String auditNum;
  /**
   * F12.受卡方所在地时间 hhmmss
   */
  private String localTransTime;
  /**
   * F13.受卡方所在地日期 MMdd
   */
  private String localTransDate;
  /**
   * F14.卡有效期
   */
  private String dateExpr;
  /**
   * F18.商户分类编码(MCC)
   */
  private String mcc;
  /**
   * F22.服务点输入方式码
   */
  private String posEntryModeCode;
  /**
   * F23.卡序列号
   */
  private String cardSeqId;
  /**
   * F25.服务点条件码
   */
  private String posCondCode;
  /**
   * F26.服务点PIN获取码
   */
  private String pinCaptureCode;
  /**
   * F32.受理方标识码
   */
  private String receiveIdCode;
  /**
   * F33.发送方标识码
   */
  private String sendIdCode;
  /**
   * F35.第二磁道数据
   */
  private String track2;
  /**
   * F37.检索参考号
   */
  private String referenceNum;
  /**
   * F41.终端号
   */
  private String terminalNum;
  /**
   * F42.商户号
   */
  private String customerNo;
  /**
   * F43.商户名称
   */
  private String customerName;
  /**
   * F49.币种
   */
  private String currencyNo;
  /**
   * F52.个人标识码
   */
  private String pin;
  /**
   * F53.安全控制信息
   */
  private String securityInfo;
  /**
   * F55.IC卡数据域
   */
  private String icData;
  /**
   * F60. 自定义域
   */
  private String reserved;

  /**
   * F61.用户信息 61.1 ans2 —01：MPOS T1 消费； ——02：MPOS D0 消费； ——03：传统 POS T1 消费； ——04：传统 POS D0 消费 61.2
   * ans15当消费是属于MPOS消费时，上送11位手机号（后补4空格）；当消 费是属于传统POS消费时，本域上送15位前端商户号 61.3
   * ans11传统POS消费时上送前端商户对应的法人手机号，如果是MPOS消费时本域为空 （11位空格） 61.4 ans12
   * 实时上送该笔消费的手续费金额，如果是D0消费，则实时上送该笔消费的手续费 （示例：如手续费：1.58元，则上送000000000158，与第4域上送规则相同），如果是T1交
   * 易，本域为空（12位空格）
   */
  private String userBodyInfo;
  /**
   * F128.报文鉴别码
   */
  private String mac;

  @Override
  public Map<Integer, String> createBodyMap() {
    Map<Integer, String> bodyMap = new TreeMap<>();
    bodyMap.put(2, getAcctNum());
    bodyMap.put(3, getProcessingCode());
    bodyMap.put(4, getAmount());
    bodyMap.put(7, getTransTime());
    bodyMap.put(11, getAuditNum());
    bodyMap.put(12, getLocalTransTime());
    bodyMap.put(13, getLocalTransDate());
    if (StringUtils.hasText(getDateExpr())) {
      bodyMap.put(14, getDateExpr());
    }
    bodyMap.put(18, getMcc());
    bodyMap.put(22, getPosEntryModeCode());
    if (StringUtils.hasText(getCardSeqId())) {
      bodyMap.put(23, getCardSeqId());
    }
    bodyMap.put(25, getPosCondCode());
    bodyMap.put(26, getPinCaptureCode());
    bodyMap.put(32, getReceiveIdCode());
    bodyMap.put(33, getSendIdCode());
    bodyMap.put(35, getTrack2());
    bodyMap.put(37, getReferenceNum());
    if (StringUtils.hasText(getTerminalNum())) {
      bodyMap.put(41, getTerminalNum());
    }
    bodyMap.put(42, getCustomerNo());
    bodyMap.put(43, getCustomerName());
    bodyMap.put(49, getCurrencyNo());
    if (StringUtils.hasText(getPin())) {
      bodyMap.put(52, getPin());
    }
    bodyMap.put(53, getSecurityInfo());
    if (StringUtils.hasText(getIcData())) {
      bodyMap.put(55, getIcData());
    }
    bodyMap.put(60, getReserved());
    bodyMap.put(61, getUserBodyInfo());
    bodyMap.put(128, getMac());
    return bodyMap;
  }

}
