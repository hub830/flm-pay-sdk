package top.lemno.pay.commons.enums;

import lombok.ToString;

/**
 * 订单类型
 * @author mux
 *
 */
@ToString
public enum OrderType {
  TRADING("普通交易"), // 时用户卡
  DEPOSIT("押金"), // 进指定对公账户
  FEE("缴费")// 收益 进收益账户
  ;//

  private String name;

  private OrderType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

}
