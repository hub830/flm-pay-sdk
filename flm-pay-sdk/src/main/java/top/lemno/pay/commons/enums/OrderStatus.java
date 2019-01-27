package top.lemno.pay.commons.enums;

import lombok.ToString;

@ToString
public enum OrderStatus {
  SUCCESSFUL("成功"), //
  FAILURE("失败"), //
  PROCESSING("处理中")//
  ;//

  private String name;

  private OrderStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
