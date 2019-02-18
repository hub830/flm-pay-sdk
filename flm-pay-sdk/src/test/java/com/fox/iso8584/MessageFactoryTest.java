package com.fox.iso8584;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import com.fox.iso8584.codecs.CompositeField;
import com.fox.iso8584.field.FieldFactory;
import com.fox.iso8584.field.FieldType;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.impl.SimpleTraceGenerator;
import top.lemno.pay.commons.dto.UnifiedPayOrder;
import top.lemno.pay.commons.enums.SettleType;

@RunWith(JUnitPlatform.class)
@DisplayName("MessageFactory 测试")
class MessageFactoryTest {
  MessageFactory messageFactory;
  String sourceStationId;
  String destinationId;
  String charset;

  String resultMessage;

  String bodyMessage;

  @BeforeEach
  void setUp() throws Exception {
    sourceStationId = "48501111";
    destinationId = "62100000";
    charset = "GBK";

    messageFactory = ConfigParser.createFromClasspathConfig("com/fox/iso8584/config.xml");
    messageFactory.setSourceStationId(sourceStationId);
    messageFactory.setDestinationId(destinationId);
    messageFactory.setAssignDate(true);
//    messageFactory.setCharacterEncoding(charset);
    messageFactory.setTraceNumberGenerator(
        new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));

    resultMessage =
        "303532392E01303532393632313030303030202020343835303131313120202030303030303030303030303030303030303030323030F23C46C1A8E09A180000000000000001313636323539393630323435313438313736303030303030303030303030323730303030303132343139333833303038373539313139333833303031323432343130353331313035313030313030303630383438353031323730303834383530313131313337363235393936303234353134383137363D32343130323031303030303038383130303030303139303132343038373539313030303030303031373030303030303030303034343031B2CCC9D9C0D72020202020202020202020202020202020202020202020202020202020202020202031353643A8BA686B2881C1323630303030303030303030303030303134359F2608DD5BC16A368E26829F2701809F101307050103A00000010A010000000000E54570769F3704DB9246219F36020096950500000000009A031901249C01009F02060000002700005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34033F00009F3501229F1E0830303030303034388408A0000003330101029F0902008C9F4104000000163032303030303030353030303330303030303030303030303430303231333933323038333736332020202020202020202020202020203030303030303030313932304239413241363734";
    bodyMessage =
        "30323030F23C46C1A8E09A180000000000000001313636323539393630323435313438313736303030303030303030303030323730303030303132343139333833303038373539313139333833303031323432343130353331313035313030313030303630383438353031323730303834383530313131313337363235393936303234353134383137363D32343130323031303030303038383130303030303139303132343038373539313030303030303031373030303030303030303034343031B2CCC9D9C0D72020202020202020202020202020202020202020202020202020202020202020202031353643A8BA686B2881C1323630303030303030303030303030303134359F2608DD5BC16A368E26829F2701809F101307050103A00000010A010000000000E54570769F3704DB9246219F36020096950500000000009A031901249C01009F02060000002700005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34033F00009F3501229F1E0830303030303034388408A0000003330101029F0902008C9F4104000000163032303030303030353030303330303030303030303030303430303231333933323038333736332020202020202020202020202020203030303030303030313932304239413241363734";
  }

  @Test
  void testNewMessage() throws DecoderException, IOException {

    IsoMessage m = messageFactory.newMessage(0x200);
    initBody(m.getIsoBody());

    byte[] buf = m.writeData(charset);
    String message = new String(buf);

    assertEquals(resultMessage, message);
  }

  @Test
  void testParseMessage() throws DecoderException, ParseException, IOException {

    System.err.println("PARSE BINARY FROM FILE");
    byte[] buf = Hex.decodeHex(bodyMessage);
    IsoMessage message = messageFactory.parseMessage(buf, 0,charset);
    IsoBody isoBody = message.getIsoBody();

    assertEquals("6259960245148176", isoBody.getField(2).toString());
    assertEquals("000000", isoBody.getField(3).toString());
    assertEquals("2700.00", isoBody.getField(4).toString());

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    isoBody.getField(7).write(bout, charset);
    assertEquals("0124193830", bout.toString());

    assertEquals("087591", isoBody.getField(11).toString());
    // assertEquals("193830", isoBody.getField(12).toString());
    // assertEquals("0124", isoBody.getField(13).toString());
    // assertEquals("2410", isoBody.getField(14).toString());
    assertEquals("5311", isoBody.getField(18).toString());
    assertEquals("051", isoBody.getField(22).toString());
    assertEquals("001", isoBody.getField(23).toString());
    assertEquals("00", isoBody.getField(25).toString());
    assertEquals("06", isoBody.getField(26).toString());
    assertEquals("48501270", isoBody.getField(32).toString());
    assertEquals("48501111", isoBody.getField(33).toString());
    assertEquals("6259960245148176=24102010000088100000", isoBody.getField(35).toString());
    assertEquals("190124087591", isoBody.getField(37).toString());
    assertEquals("00000001", isoBody.getField(41).toString());
    assertEquals("700000000004401", isoBody.getField(42).toString());
    assertEquals("蔡少雷", isoBody.getField(43).toString());
    assertEquals("156", isoBody.getField(49).toString());
    assertEquals("43A8BA686B2881C1", isoBody.getField(52).toString());
    assertEquals("2600000000000000", isoBody.getField(53).toString());
    assertEquals(
        "9F2608DD5BC16A368E26829F2701809F101307050103A00000010A010000000000E54570769F3704DB9246219F36020096950500000000009A031901249C01009F02060000002700005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34033F00009F3501229F1E0830303030303034388408A0000003330101029F0902008C9F410400000016",
        isoBody.getField(55).toString());
    assertEquals("00000500030000000000", isoBody.getField(60).toString());
    assertEquals("CompositeField[02,13932083763,,000000001920]", isoBody.getField(61).toString());
    assertEquals("4239413241363734", isoBody.getField(128).toString());
  }


  private static void initBody(IsoBody body) throws DecoderException {
    String encoding = "GBK";



    SettleType settleType = SettleType.D0;
    DateTime dt = new DateTime(2019, 01, 24, 19, 38, 30, 0);
    DateTime dateExpr = new DateTime(2024, 10, 1, 0, 0, 0, 0);
    Date today = dt.toDate();
    UnifiedPayOrder order = UnifiedPayOrder.builder()//
        .acctNum("6259960245148176")//
        .amount(new BigDecimal(2700))//
        .transTime(today)//
        .dateExpr(dateExpr.toDate())//
        .mcc(5311)//
        .posEntryModeCode("051")// IC卡编码 05开头 磁条卡编码 02开头 需验证
        .cardSeqId("001")//
        .track2("6259960245148176=24102010000088100000")//
        .referenceNum("190124087591")//
        .terminalNum("00000001")//
        .customerNo("700000000004401")//
        .customerName("蔡少雷")//
        .pin("43A8BA686B2881C1")// 密码，需要调用加密机加密
        .icData(
            "9f2608dd5bc16a368e26829f2701809f101307050103a00000010a010000000000e54570769f3704db9246219f36020096950500000000009a031901249c01009f02060000002700005f2a02015682027c009f1a0201569f03060000000000009f3303e0e1c89f34033f00009f3501229f1e0830303030303034388408a0000003330101029f0902008c9f410400000016")//
        .phone("13932083763")//
        .fee(new BigDecimal(19))//
        .settleFee(new BigDecimal(0.2))//
        .build();

    FieldFactory.getField(FieldType.LLVAR, order.getAcctNum());



    // 请求类型
    // m.setValue(0, 0200, IsoType.NUMERIC, 4);
    body.setField(2, FieldFactory.getField(FieldType.LLVAR, order.getAcctNum()))// 主账号
        .setField(3, FieldFactory.getField(FieldType.NUMERIC, 0, 6))// 交易处理码
        .setField(4, FieldFactory.getField(FieldType.AMOUNT, order.getAmount()))// PadType.LEFT_ZERO
        // 交易金额(分)
        .setField(7, FieldFactory.getField(FieldType.DATE10, order.getTransTime()))// 交易传输时间
        .setField(11, FieldFactory.getField(FieldType.NUMERIC, 87591, 6))// 系统跟踪号
        .setField(12, FieldFactory.getField(FieldType.TIME, order.getTransTime()))// 受卡方所在地时间
        .setField(13, FieldFactory.getField(FieldType.DATE4, order.getTransTime()))// 受卡方所在地日期
        .setField(14, FieldFactory.getField(FieldType.DATE_EXP, dateExpr.toDate()))// 卡有效期
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
        .setField(60, FieldFactory.getField(FieldType.LLLVAR, "00000500030000000000"))// 自定义域
                                                                                      // .磁条卡60域名编码
                                                                                      // 或
        // IC卡60域编码
        .setField(61,
            getField61(order.getPhone(), settleType, order.getFee(), order.getSettleFee(),
                encoding))
        .setField(128, FieldFactory.getField(FieldType.BINARY, "4239413241363734", null, 8, true))// 自定义域
    // .磁条卡60域名编码
    // 或
    // IC卡60域编码
    ;

  }

  private static FieldValue<CompositeField> getField61(String phone, SettleType settleType,
      BigDecimal fee, BigDecimal settleFee, String encoding) {


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

