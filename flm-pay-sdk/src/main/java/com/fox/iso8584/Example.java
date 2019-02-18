package com.fox.iso8584;



import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import com.fox.iso8584.codecs.CompositeField;
import com.fox.iso8584.field.FieldFactory;
import com.fox.iso8584.field.FieldType;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.impl.SimpleTraceGenerator;
import top.lemno.pay.commons.dto.UnifiedPayOrder;
import top.lemno.pay.commons.enums.SettleType;


/**
 * This little example program creates a message factory out of a XML config file, creates a new
 * message, and parses a couple of message from a text file.
 * 
 * @author Enrique Zamudio
 */
public class Example {

  public static void print(IsoBody m) throws UnsupportedEncodingException, IOException {
    for (int i = 2; i < 128; i++) {
      if (m.hasField(i)) {
        FieldValue<Object> field = m.getField(i);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();    
        field.write(bout);         
        byte[] format = bout.toByteArray();
        String encodeHex = Hex.encodeHexString(format).toUpperCase();
        System.out.printf("F %3d(%s): %s -> '%s'\n", i, m.getField(i).getType(),
            field.toString(), encodeHex);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    MessageFactory mfact = ConfigParser.createFromClasspathConfig("com/fox/iso8584/config.xml");
    mfact.setSourceStationId("48501111");
    mfact.setDestinationId("62100000");
    mfact.setAssignDate(true);
    mfact.setCharacterEncoding("GBK");
    mfact.setTraceNumberGenerator(
        new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));
    // mfact.setCharacterEncoding("GBK");
    // Create a new message
    IsoMessage m = mfact.newMessage(0x200);
    initBody(m.getIsoBody());

    System.err.println("NEW MESSAGE");
    byte[] buf = m.writeData();
    System.err.println(new String(buf));



    System.err.println("----------------"); 

    
     System.err.println("PARSE BINARY FROM FILE");
     byte[] aaa = new byte[966];
     System.arraycopy(buf, 100, aaa, 0, 966);
     System.err.println(new String(aaa));
     IsoMessage message = mfact.parseMessage(Hex.decodeHex(new String(aaa)), 0);
     print(message.getIsoBody());
  }

  private static void initBody(IsoBody body) throws DecoderException {

    FieldFactory ff = new FieldFactory("GBK");



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

    ff.getField(FieldType.LLVAR, order.getAcctNum());



    // 请求类型
    // m.setValue(0, 0200, IsoType.NUMERIC, 4);
    body.setField(2, ff.getField(FieldType.LLVAR, order.getAcctNum()))// 主账号
        .setField(3, ff.getField(FieldType.NUMERIC, 0, 6))// 交易处理码
        .setField(4, ff.getField(FieldType.AMOUNT, order.getAmount()))// PadType.LEFT_ZERO
                                                                      // 交易金额(分)
        .setField(7, ff.getField(FieldType.DATE10, order.getTransTime()))// 交易传输时间
        .setField(11, ff.getField(FieldType.NUMERIC, 87591, 6))// 系统跟踪号
        .setField(12, ff.getField(FieldType.TIME, order.getTransTime()))// 受卡方所在地时间
        .setField(13, ff.getField(FieldType.DATE4, order.getTransTime()))// 受卡方所在地日期
        .setField(14, ff.getField(FieldType.DATE_EXP, dateExpr.toDate()))// 卡有效期
        // .setField(15, ff.GetField(FieldType.DATE4, null))// 卡有效期
        .setField(18, ff.getField(FieldType.NUMERIC, order.getMcc(), 4))// 商户分类编码(MCC)
        .setField(22, ff.getField(FieldType.NUMERIC, order.getPosEntryModeCode(), 3))// 服务点输入方式码
        .setField(23, ff.getField(FieldType.NUMERIC, order.getCardSeqId(), 3))// 卡序列号
        .setField(25, ff.getField(FieldType.NUMERIC, 0, 2))// 服务点条件码
        .setField(26, ff.getField(FieldType.NUMERIC, 6, 2))// 服务点PIN获取码
        .setField(32, ff.getField(FieldType.LLVAR, "48501270"))// 受理方标识码
        .setField(33, ff.getField(FieldType.LLVAR, "48501111"))// 发送方标识码
        .setField(35, ff.getField(FieldType.LLVAR, order.getTrack2()))// 第二磁道数据
        .setField(37, ff.getField(FieldType.ALPHA, order.getReferenceNum(), 12))// 检索参考号
        // .setField(38, ff.GetField(FieldType.ALPHA, ""))// 授权标识应答码
        // .setField(39, ff.GetField(FieldType.ALPHA, null, null, 2, false, false))// 应答码
        .setField(41, ff.getField(FieldType.ALPHA, order.getTerminalNum(), 8))// 终端号
        .setField(42, ff.getField(FieldType.ALPHA, order.getCustomerNo(), 15))// 商户号
        .setField(43, ff.getField(FieldType.ALPHA, order.getCustomerName(), 40))// PadType.RIGHT_SPACE
                                                                                // 商户名称
        // .setField(48, ff.GetField(FieldType.LLLVAR, null))// 附加数据－私有
        .setField(49, ff.getField(FieldType.ALPHA, "156", 3))// 交易货币代码
        .setField(52, ff.getField(FieldType.BINARY, order.getPin(), null, 8, true))// 个人标识码
        .setField(53, ff.getField(FieldType.NUMERIC, 2600000000000000l, 16))// 安全控制信息
        .setField(55,
            ff.getField(FieldType.LLLBIN, order.getIcData(), null, 0, true))// IC卡数据域
        .setField(60, ff.getField(FieldType.LLLVAR, "00000500030000000000"))// 自定义域 .磁条卡60域名编码 或
                                                                            // IC卡60域编码
        .setField(61,
            getField61(ff, order.getPhone(), settleType, order.getFee(), order.getSettleFee()))
        .setField(128, ff.getField(FieldType.BINARY, "4239413241363734", null, 8, true))// 自定义域
    // .磁条卡60域名编码
    // 或
    // IC卡60域编码
    ;

  }

  private static FieldValue<CompositeField> getField61(FieldFactory ff, String phone,
      SettleType settleType, BigDecimal fee, BigDecimal settleFee) {


    FieldValue<String> field61_1 = ff.getField(FieldType.ALPHA, "02", 2);
    FieldValue<String> field61_2 = ff.getField(FieldType.ALPHA, phone, 15);
    FieldValue<String> field61_3 = ff.getField(FieldType.ALPHA, " ", 11);
    // 用户信息域
    CompositeField field61 = new CompositeField()//
        .addValue(field61_1)//
        .addValue(field61_2)//
        .addValue(field61_3);//

    if (settleType == SettleType.D0) {
      BigDecimal totalFee = fee.add(settleFee);
      FieldValue<BigDecimal> field61_4 = ff.getField(FieldType.AMOUNT, totalFee, 12);
      field61.addValue(field61_4);
    } else {

      FieldValue<String> field61_4 = ff.getField(FieldType.ALPHA, "", 12);
      field61.addValue(field61_4);
    }
    return ff.getField(FieldType.LLLVAR, field61, field61, 0, false);
  }
}
