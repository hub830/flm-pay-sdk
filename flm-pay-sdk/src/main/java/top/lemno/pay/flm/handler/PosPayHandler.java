package top.lemno.pay.flm.handler;

import java.math.BigDecimal;
import com.fox.iso8584.IsoBody;
import com.fox.iso8584.IsoMessage;
import com.fox.iso8584.MessageFactory;
import com.fox.iso8584.codecs.CompositeField;
import com.fox.iso8584.enums.MessageType;
import com.fox.iso8584.field.FieldFactory;
import com.fox.iso8584.field.FieldType;
import com.fox.iso8584.field.FieldValue;
import top.lemno.pay.commons.Handler.PayHandler;
import top.lemno.pay.commons.client.SocketClient;
import top.lemno.pay.commons.dto.UnifiedPayOrder;
import top.lemno.pay.commons.dto.UnifiedPayRequest;
import top.lemno.pay.commons.dto.UnifiedResponse;
import top.lemno.pay.commons.enums.OperateType;
import top.lemno.pay.commons.enums.OrderType;
import top.lemno.pay.commons.enums.SettleType;
import top.lemno.pay.commons.enums.TransType;

/**
 * 小pos交易处理类，结算类型默认为D0
 * 
 * @author mux
 *
 */
public class PosPayHandler extends PayHandler {

  private final SocketClient socketClient;

  private final MessageFactory messageFactory;

  public PosPayHandler(SocketClient socketClient, MessageFactory messageFactory) {
    super();
    this.socketClient = socketClient;
    this.messageFactory = messageFactory;
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

  private IsoMessage to8583(UnifiedPayOrder order) {

    IsoMessage isoMessage = messageFactory.newMessage(MessageType.PAY.getType());
    IsoBody body = isoMessage.getIsoBody();

    body.setField(2, FieldFactory.getField(FieldType.LLVAR, order.getAcctNum()))// 主账号
        .setField(3, FieldFactory.getField(FieldType.NUMERIC, 0, 6))// 交易处理码
        .setField(4, FieldFactory.getField(FieldType.AMOUNT, order.getAmount()))// PadType.LEFT_ZERO
        // 交易金额(分)
        .setField(7, FieldFactory.getField(FieldType.DATE10, order.getTransTime()))// 交易传输时间
        .setField(11, FieldFactory.getField(FieldType.NUMERIC, 87591, 6))// 系统跟踪号
        .setField(12, FieldFactory.getField(FieldType.TIME, order.getTransTime()))// 受卡方所在地时间
        .setField(13, FieldFactory.getField(FieldType.DATE4, order.getTransTime()))// 受卡方所在地日期
        .setField(14, FieldFactory.getField(FieldType.DATE_EXP, order.getDateExpr()))// 卡有效期
        // .setField(15, ff.GetField(FieldType.DATE4, null))// 卡有效期
        .setField(18, FieldFactory.getField(FieldType.NUMERIC, order.getMcc(), 4))// 商户分类编码(MCC)
        .setField(22, FieldFactory.getField(FieldType.NUMERIC, order.getPosEntryModeCode(), 3))// 服务点输入方式码
        .setField(23, FieldFactory.getField(FieldType.NUMERIC, order.getCardSeqId(), 3))// 卡序列号
        .setField(25, FieldFactory.getField(FieldType.NUMERIC, 0, 2))// 服务点条件码
        .setField(26, FieldFactory.getField(FieldType.NUMERIC, 6, 2))// 服务点PIN获取码
        .setField(32, FieldFactory.getField(FieldType.LLVAR, "48501270"))// 受理方标识码
        .setField(33, FieldFactory.getField(FieldType.LLVAR, "48501111"))// 发送方标识码
        .setField(35, FieldFactory.getField(FieldType.LLVAR, order.getTrack2()))// 第二磁道数据
        .setField(37, FieldFactory.getField(FieldType.ALPHA, order.getReferenceNum(), 12))// 检索参考号
        // .setField(38, ff.GetField(FieldType.ALPHA, ""))// 授权标识应答码
        // .setField(39, ff.GetField(FieldType.ALPHA, null, null, 2, false, false))// 应答码
        .setField(41, FieldFactory.getField(FieldType.ALPHA, order.getTerminalNum(), 8))// 终端号
        .setField(42, FieldFactory.getField(FieldType.ALPHA, order.getCustomerNo(), 15))// 商户号
        .setField(43, FieldFactory.getField(FieldType.ALPHA, order.getCustomerName(), 40))// PadType.RIGHT_SPACE
        // 商户名称
        // .setField(48, ff.GetField(FieldType.LLLVAR, null))// 附加数据－私有
        .setField(49, FieldFactory.getField(FieldType.ALPHA, "156", 3))// 交易货币代码
        .setField(52, FieldFactory.getField(FieldType.BINARY, order.getPin(), null, 8, true))// 个人标识码
        .setField(53, FieldFactory.getField(FieldType.NUMERIC, 2600000000000000l, 16))// 安全控制信息
        .setField(55, FieldFactory.getField(FieldType.LLLBIN, order.getIcData(), null, 0, true))// IC卡数据域
        /*
         * 自定义域 磁条卡60域名编码或IC卡60域编码
         */
        .setField(60, FieldFactory.getField(FieldType.LLLVAR, "00000500030000000000"))
        .setField(61,
            getField61(order.getPhone(), SettleType.D0, order.getFee(), order.getSettleFee()))
        .setField(128, FieldFactory.getField(FieldType.BINARY, "4239413241363734", null, 8, true))// 自定义域
    // .磁条卡60域名编码
    // 或
    // IC卡60域编码
    ;
    return isoMessage;
  }

  private static FieldValue<CompositeField> getField61(String phone, SettleType settleType,
      BigDecimal fee, BigDecimal settleFee) {


    FieldValue<String> field61_1 = FieldFactory.getField(FieldType.ALPHA, "02", 2);
    FieldValue<String> field61_2 = FieldFactory.getField(FieldType.ALPHA, phone, 15);
    FieldValue<String> field61_3 = FieldFactory.getField(FieldType.ALPHA, " ", 11);
    // 用户信息域
    CompositeField field61 = new CompositeField()//
        .addValue(field61_1)//
        .addValue(field61_2)//
        .addValue(field61_3);//

    if (settleType == SettleType.D0) {
      BigDecimal totalFee = fee.add(settleFee);
      FieldValue<BigDecimal> field61_4 = FieldFactory.getField(FieldType.AMOUNT, totalFee, 12);
      field61.addValue(field61_4);
    } else {

      FieldValue<String> field61_4 = FieldFactory.getField(FieldType.ALPHA, "", 12);
      field61.addValue(field61_4);
    }
    return FieldFactory.getField(FieldType.LLLVAR, field61, field61, 0, false);
  }
}
