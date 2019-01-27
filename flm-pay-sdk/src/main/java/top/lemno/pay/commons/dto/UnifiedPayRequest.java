package top.lemno.pay.commons.dto;

import lombok.Data;
import lombok.ToString;
import top.lemno.pay.commons.enums.OperateType;
import top.lemno.pay.commons.enums.OrderType;
import top.lemno.pay.commons.enums.SettleType;
import top.lemno.pay.commons.enums.TransType;

/**
 * 统一支付请求
 * 
 * @author mux
 *
 */
@Data
@ToString
public class UnifiedPayRequest  implements UnifiedRequest{
  /**
   * 订单类型
   */
  private OrderType orderType;
  /**
   * 结算类型
   */
  private SettleType settleType;
  /**
   * 交易类型
   */
  private TransType transType;
  /**
   * 操作类型
   */
  private OperateType operateType;  
  /**
   * 订单
   */
  private UnifiedPayOrder order;

  public UnifiedPayRequest(OrderType orderType, SettleType settleType, TransType transType,
      UnifiedPayOrder order) {
    super();
    this.orderType = orderType;
    this.settleType = settleType;
    this.transType = transType;
    this.order = order;
  }

}
