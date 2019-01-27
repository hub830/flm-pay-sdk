package top.lemno.pay.commons.enums;

import lombok.ToString;

/**
 * 结算类型
 * 
 * @author mux
 *
 */
@ToString
public enum SettleType {
  D0("秒结"), //
  T1("T1")//
  ;//

  private String name;

  private SettleType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
