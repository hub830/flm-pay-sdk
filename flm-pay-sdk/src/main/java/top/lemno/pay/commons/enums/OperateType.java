package top.lemno.pay.commons.enums;

import lombok.ToString;

/**
 * 操作类型
 * 
 * @author mux
 *
 */
@ToString
public enum OperateType {
  TRADE("交易"), //
  QUERY("查询"), //
  CANCEL("取消")//
  ;//

  private String name;

  private OperateType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
