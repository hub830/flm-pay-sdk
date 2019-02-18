package top.lemno.pay.commons.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class UnifiedPayOrder implements UnifiedOrder {
  /**
   * F2.主账号
   */
  private String acctNum;
  /**
   * F4.交易金额
   */
  private BigDecimal amount;
  /**
   * F7.交易传输时间 MMddhhmmss
   */
  private Date transTime;
  /**
   * F14.卡有效期
   */
  private Date dateExpr;
  /**
   * F18.商户分类编码(MCC)
   */
  private int mcc;
  /**
   * F22.服务点输入方式码
   */
  private String posEntryModeCode;
  /**
   * F23.卡序列号
   */
  private String cardSeqId;
  /**
   * F32.受理方标识码
   */
  private String receiveIdCode;
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
   * F52.个人标识码
   */
  private String pin;
  /**
   * F55.IC卡数据域
   */
  private String icData;
  /**
   * F61.2 手机号
   */
  private String phone;
  /**
   * F61.4 手续费
   */
  private BigDecimal fee;
  /**
   * F61.4 结算手续费
   */
  private BigDecimal settleFee;

}
