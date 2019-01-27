package top.lemno.pay.commons.Handler;

import top.lemno.pay.commons.dto.UnifiedPayRequest;
import top.lemno.pay.commons.dto.UnifiedResponse;
import top.lemno.pay.commons.enums.OperateType;
import top.lemno.pay.commons.enums.OrderType;
import top.lemno.pay.commons.enums.SettleType;
import top.lemno.pay.commons.enums.TransType;

public abstract class PayHandler {

  protected PayHandler next;
  
  public abstract UnifiedResponse execute(UnifiedPayRequest request);
  
  public abstract boolean isSupport(OperateType operateType,SettleType settleType,TransType transType,OrderType orderType);

  public   UnifiedResponse handleRequest(UnifiedPayRequest request) {

    OperateType operateType = request.getOperateType();
    SettleType settleType = request.getSettleType();
    TransType transType = request.getTransType();
    OrderType orderType = request.getOrderType();
    
    if(isSupport(operateType,settleType,transType,orderType))
    {
      return execute(request);
    }
    return this.next.handleRequest(request);
  }

  public final PayHandler next() {
    return this.next;
  }

  public final void setNext(PayHandler next) {
    this.next = next;
  }
}
