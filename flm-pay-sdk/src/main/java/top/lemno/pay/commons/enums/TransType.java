package top.lemno.pay.commons.enums;

import lombok.ToString;

/**
 * 交易类型
 * 
 * @author mux
 *
 */
@ToString
public enum TransType {
  NFC("NFC"), //
  M_POS("MPOS"), //
  QR_CODE_UNIONPAY("银联二维码"), //
  QR_CODE_WEXINPAY("微信二维码"), //
  QR_CODE_ALIPAY("支付宝二维码")//
  ;//

  private String name;

  private TransType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
