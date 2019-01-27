package com.fox.iso8584;



import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import com.fox.iso8584.codecs.CompositeField;
import com.fox.iso8584.codecs.CustomField43;
import com.fox.iso8584.impl.SimpleTraceGenerator;
import com.fox.iso8584.parse.ConfigParser;
import top.lemno.pay.commons.dto.UnifiedPayOrder;
import top.lemno.pay.commons.enums.SettleType;


/**
 * This little example program creates a message factory out of a XML config file, creates a new
 * message, and parses a couple of message from a text file.
 * 
 * @author Enrique Zamudio
 */
public class Example {

  public static void print(IsoBody m) {
    for (int i = 2; i < 128; i++) {
      if (m.hasField(i)) {
        System.out.printf("F %3d(%s): %s -> '%s'\n", i, m.getField(i).getType(),
            m.getObjectValue(i), m.getField(i).toString());
      }
    }
  }

  public static void main(String[] args) throws Exception {
    CustomField43 customField43 = new CustomField43();
    customField43.setEncoding("GBK");
    MessageFactory mfact = ConfigParser.createFromClasspathConfig("com/fox/iso8584/config.xml");
    mfact.setSourceStationId("48501111");
    mfact.setDestinationId("62100000");
    mfact.setAssignDate(true);
    mfact.setCustomField(43, customField43);
    mfact.setTraceNumberGenerator(
        new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));
    mfact.setCharacterEncoding("GBK");
    // Create a new message
    IsoMessage m = mfact.newMessage(0x200);
    initBody(m.getIsoBody());

    System.err.println("NEW MESSAGE");
    byte[] buf = m.writeData();
    System.err.println(new String(buf));

    System.err.println("PARSE BINARY FROM FILE");
    byte[] aaa = new byte[966];
    System.arraycopy(buf, 100, aaa, 0, 966); 
    System.err.println(new String(aaa));
    IsoMessage message = mfact.parseMessage(Hex.decodeHex(new String(aaa)), 0);
    print(message.getIsoBody());
  }

  private static void initBody(IsoBody body) throws DecoderException {

    SettleType settleType = SettleType.D0;
    DateTime dt = new DateTime(2019, 01, 24, 19, 38, 30, 0);
    DateTime dateExpr = new DateTime(2024, 10, 1, 0, 0, 0, 0);
    Date today = dt.toDate();
    UnifiedPayOrder order = UnifiedPayOrder.builder()//
        .acctNum("6259960245148176")//
        .amount(new BigDecimal(2700))//
        .transTime(today)//
        .dateExpr(dateExpr.toDate())//
        .mcc("5311")//
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
 

    // 请求类型
    // m.setValue(0, 0200, IsoType.NUMERIC, 4);
    // 主账号
    body.setValue(2, order.getAcctNum(), IsoType.LLVAR, 0);
    // 交易处理码
    body.setValue(3, 0, IsoType.NUMERIC, 6);
    // PadType.LEFT_ZERO 交易金额(分)
    body.setValue(4, order.getAmount(), IsoType.AMOUNT, 0);
    // 交易传输时间
    body.setValue(7, order.getTransTime(), IsoType.DATE10, 10);
    // 系统跟踪号
    body.setValue(11, 87591, IsoType.NUMERIC, 6);
    // 受卡方所在地时间
    body.setValue(12, order.getTransTime(), IsoType.TIME, 0);
    // 受卡方所在地日期
    body.setValue(13, order.getTransTime(), IsoType.DATE4, 0);
    // 卡有效期
    body.setValue(14, order.getDateExpr(), IsoType.DATE_EXP, 0);
    // 清算日期
    // m.setValue(15, null, IsoType.DATE4, 0);
    // 商户分类编码(MCC)
    body.setValue(18, order.getMcc(), IsoType.NUMERIC, 4);
    // 服务点输入方式码
    body.setValue(22, order.getPosEntryModeCode(), IsoType.NUMERIC, 3);
    // 卡序列号
    body.setValue(23, order.getCardSeqId(), IsoType.NUMERIC, 3);
    // 服务点条件码
    body.setValue(25, 0, IsoType.NUMERIC, 2);
    // 服务点PIN获取码
    body.setValue(26, 6, IsoType.NUMERIC, 2);
    // 受理方标识码
    body.setValue(32, "48501270", IsoType.LLVAR, 0);
    // 发送方标识码
    body.setValue(33, "48501111", IsoType.LLVAR, 0);
    // 第二磁道数据
    body.setValue(35, order.getTrack2(), IsoType.LLVAR, 0);
    // 检索参考号
    body.setValue(37, order.getReferenceNum(), IsoType.ALPHA, 12);
    // 授权标识应答码
    // m.setValue(38, null, IsoType.ALPHA, 6);
    // 应答码
    // m.setValue(39, null, IsoType.ALPHA, 2);
    // 终端号
    body.setValue(41, order.getTerminalNum(), IsoType.ALPHA, 8);
    // 商户号
    body.setValue(42, order.getCustomerNo(), IsoType.ALPHA, 15);
    // PadType.RIGHT_SPACE 商户名称
    body.setValue(43, order.getCustomerName(), IsoType.ALPHA, 40);
    // 附加数据－私有
    // m.setValue(48, null, IsoType.LLLVAR, 0);
    // 交易货币代码
    body.setValue(49, "156", IsoType.ALPHA, 3);
    // 个人标识码
    IsoValue<String> field52 = new IsoValue<String>(IsoType.BINARY, order.getPin(), 8);
    field52.setBinaryField(true);
    body.setField(52, field52);
    // 安全控制信息
    body.setValue(53, 2600000000000000l, IsoType.NUMERIC, 16);
    // IC卡数据域
   
    IsoValue<byte[]> field55 = new IsoValue<byte[]>(IsoType.LLLBIN_2,  Hex.decodeHex(order.getIcData().toCharArray()));
    field55.setBinaryField(true);
    body.setField(55, field55);


    /**
     * F60.IC卡60域编码
     */
    String IC_60_FIELD = "00000500030000000000";
    /**
     * F60.磁条卡60域名编码
     */
    String UN_IC_60_FIELLD = "00000200030000000000";
    // 自定义域
    body.setValue(60, IC_60_FIELD, IsoType.LLLVAR, 0);

    IsoValue<String> field61_1 = new IsoValue<String>(IsoType.ALPHA, "02", 2);
    field61_1.setCharacterEncoding("GBK");
    IsoValue<String> field61_2 = new IsoValue<String>(IsoType.ALPHA, order.getPhone(), 15);
    field61_2.setCharacterEncoding("GBK");
    IsoValue<String> field61_3 = new IsoValue<String>(IsoType.ALPHA, " ", 11);
    field61_3.setCharacterEncoding("GBK");
    // 用户信息域
    CompositeField field61 = new CompositeField()//
        .addValue(field61_1)//
        .addValue(field61_2)//
        .addValue(field61_3);//

    if (settleType == SettleType.D0) {
      BigDecimal totalFee = order.getFee().add(order.getSettleFee());
      IsoValue<BigDecimal> field61_4 = new IsoValue<BigDecimal>(IsoType.AMOUNT, totalFee, 12);
      field61_4.setCharacterEncoding("GBK");
      field61.addValue(field61_4);
    } else {

      IsoValue<String> field61_4 = new IsoValue<String>(IsoType.ALPHA, "", 12);
      field61_4.setCharacterEncoding("GBK");
      field61.addValue(field61_4);
    }
    body.setValue(61, field61, field61, IsoType.LLLVAR, 0);



    // 网络管理信息码
    // m.setValue(70, null, IsoType.NUMERIC, 3);
    // 网络管理信息码
    // m.setValue(90, null, IsoType.NUMERIC, 42);
    // 报文安全码
    // m.setValue(96, null, IsoType.BINARY, 8);
    // 接收机构标识码
    // m.setValue(100, null, IsoType.LLVAR, 0);
    // 受理方保留
    // m.setValue(122, null, IsoType.LLLVAR, 0);
    // 报文鉴别码
    IsoValue<String> field128 = new IsoValue<String>(IsoType.BINARY, "4239413241363734", 8);
    field128.setBinaryField(true);
    body.setField(128, field128);
  }
}
