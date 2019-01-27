package top.lemno.pay.flm.handler;

import top.lemno.pay.commons.Handler.PayHandler;
import top.lemno.pay.commons.client.SocketClient;
import top.lemno.pay.commons.dto.UnifiedPayOrder;
import top.lemno.pay.commons.dto.UnifiedPayRequest;
import top.lemno.pay.commons.dto.UnifiedResponse;
import top.lemno.pay.commons.enums.OperateType;
import top.lemno.pay.commons.enums.OrderType;
import top.lemno.pay.commons.enums.SettleType;
import top.lemno.pay.commons.enums.TransType;

public class PosPayHandler extends PayHandler {

  private final SocketClient socketClient;

  public PosPayHandler(SocketClient socketClient) {
    super();
    this.socketClient = socketClient;
  }

  @Override
  public UnifiedResponse execute(UnifiedPayRequest request) {
    socketClient.sendMsg(request.toString());
    return null;
  }

  @Override
  public boolean isSupport(OperateType operateType, SettleType settleType, TransType transType,
      OrderType orderType) {
    if (operateType != OperateType.TRADE)
      return false;
    if (transType != TransType.M_POS)
      return false;
    if (orderType != OrderType.TRADING)
      return false;
    return true;
  }

  private String to8583(UnifiedPayOrder order) {

    
    return null;
  }
}
