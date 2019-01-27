package top.lemno.pay.commons.api;

import top.lemno.pay.commons.Handler.PayHandler;
import top.lemno.pay.commons.dto.UnifiedPayRequest;
import top.lemno.pay.commons.dto.UnifiedResponse;
import top.lemno.pay.commons.enums.OperateType;

/**
 * 统一第三方支付交易接口
 * 
 * @author mux
 *
 */
public class UnifiedT3dPayTransApi implements UnifiedT3dPayApi<UnifiedPayRequest> {

  private final PayHandler handler;

  public UnifiedT3dPayTransApi(PayHandler handler) {
    super();
    this.handler = handler;
  }

  @Override
  public UnifiedResponse trade(UnifiedPayRequest request) {

    request.setOperateType(OperateType.TRADE);
    return handler.handleRequest(request);
  }

  @Override
  public UnifiedResponse cancel(UnifiedPayRequest request) {
    request.setOperateType(OperateType.CANCEL);
    return handler.handleRequest(request);
  }

  @Override
  public UnifiedResponse query(UnifiedPayRequest request) {
    request.setOperateType(OperateType.QUERY);
    return handler.handleRequest(request);
  }



}
