package com.fox.iso8583;



import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import com.fox.iso8583.codecs.CompositeField;
import com.fox.iso8583.codecs.CustomField43;
import com.fox.iso8583.impl.SimpleTraceGenerator;
import com.fox.iso8583.parse.FlmConfigParser;
import top.lemno.pay.commons.dto.UnifiedPayOrder;
import top.lemno.pay.commons.enums.SettleType;


/**
 * This little example program creates a message factory out of a XML config file, creates a new
 * message, and parses a couple of message from a text file.
 * 
 * @author Enrique Zamudio
 */
public class ExampleFlm2 {

  public static void print(FlmIsoMessage m) throws UnsupportedEncodingException {
    System.out.printf("TYPE: %04x\n", m.getType());
    for (int i = 2; i < 128; i++) {
      if (m.hasField(i)) {
        IsoValue<Object> field = m.getField(i);
        Object value = field.getValue();

        System.out.printf("F %3d(%s): %s -> '%s'('%s')\n", i, field.getType(), value, field.toString(),Hex.encodeHexString(field.toString().getBytes("GBK")).toUpperCase());

        if (value instanceof CompositeField) {
          CompositeField cFeild = (CompositeField) value;
          List<IsoValue> list = cFeild.getValues();
          for (int j = 0; j < list.size(); j++) {
            IsoValue isoValue = list.get(j);
            System.out.printf("SUB_F %3d(%s): %s -> '%s'('%s')\n", j, isoValue.getType(),
                isoValue.getValue(), isoValue.toString(),Hex.encodeHexString(isoValue.toString().getBytes("GBK")).toUpperCase());
          }
        }
      }
    }
  }

  public static void printHeader(FlmIsoMessage m) {
    System.out.printf("TYPE: %04x\n", m.getType());
    for (int i = 0; i < 10; i++) {
      if (m.hasHeaderField(i)) {
        IsoValue<Object> field = m.getHeaderField(i);
        Object value = field.getValue();

        System.out.printf("F %3d(%s): %s -> '%s'('%s')\n", i, field.getType(), value, field.toString(),Hex.encodeHexString(field.toString().getBytes()).toUpperCase());

      }
    }
  }
  public static void main(String[] args) throws Exception {
    CustomField43 customField43 = new CustomField43();
    customField43.setEncoding("GBK");
    customField43.setValue("陈放");
    FlmMessageFactory<FlmIsoMessage> mfact =
        FlmConfigParser.createFromClasspathConfig("com/fox/iso8583/config.xml");
    mfact.setCharacterEncoding("GBK");
    mfact.setAssignDate(true);
    mfact.setTraceNumberGenerator(
        new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));
    mfact.setCustomField(43, customField43);
    SettleType settleType = SettleType.D0;
    DateTime dt = new DateTime(2019, 01, 21, 10, 33, 37, 500);;
    Date today = dt.toDate();
    UnifiedPayOrder order = UnifiedPayOrder.builder()//
        .acctNum("4392250036389830")//
        .amount(new BigDecimal(100.50))//
        .transTime(today)//
        .dateExpr(today)//
        .mcc("5411")//
        .posEntryModeCode("02")// UN_IC_CODE
        .cardSeqId("5")//
        .track2("8")//
        .referenceNum("111111111111")//
        .terminalNum("00000001")//
        .customerNo("700000000000002")//
        .customerName("陈放")//
        .pin("101F389141C27515")//
        .icData("6")//
        .phone("13800138000")//
        .fee(new BigDecimal(1))//
        .settleFee(new BigDecimal(3))//
        .build();



    // Create a new message
    System.err.println("NEW MESSAGE");
    FlmIsoMessage m = mfact.newMessage(0x200);

//  m.setBinary(true);

    // 请求类型
    // m.setValue(0, 0200, IsoType.NUMERIC, 4);
    // 主账号
    m.setValue(2, order.getAcctNum(), IsoType.LLVAR, 0);
    // 交易处理码
    // m.setValue(3, null, IsoType.NUMERIC, 6);
    // PadType.LEFT_ZERO 交易金额(分)
    m.setValue(4, order.getAmount(), IsoType.AMOUNT, 0);
    // 交易传输时间
    m.setValue(7, order.getTransTime(), IsoType.DATE10, 10);
    // 系统跟踪号
    m.setValue(11, order.getReferenceNum().substring(6, 12), IsoType.NUMERIC, 6);
    // 受卡方所在地时间
    m.setValue(12, order.getTransTime(), IsoType.TIME, 0);
    // 受卡方所在地日期
    m.setValue(13, order.getTransTime(), IsoType.DATE4, 0);
    // 卡有效期
//    m.setValue(14, order.getDateExpr(), IsoType.DATE_EXP, 0);
    // 清算日期
    // m.setValue(15, null, IsoType.DATE4, 0);
    // 商户分类编码(MCC)
    m.setValue(18, order.getMcc(), IsoType.NUMERIC, 4);
    // 服务点输入方式码
    m.setValue(22, order.getPosEntryModeCode(), IsoType.NUMERIC, 3);
    // 卡序列号
//    m.setValue(23, order.getCardSeqId(), IsoType.NUMERIC, 3);
    // 服务点条件码
    // m.setValue(25, null, IsoType.NUMERIC, 2);
    // 服务点PIN获取码
    // m.setValue(26, null, IsoType.NUMERIC, 2);
    // 受理方标识码
     m.setValue(32, "85272070", IsoType.LLVAR, 0);
    // 发送方标识码
    // m.setValue(33, null, IsoType.LLVAR, 0);
    // 第二磁道数据
    m.setValue(35, order.getTrack2(), IsoType.LLVAR, 0);
    // 检索参考号
    m.setValue(37, order.getReferenceNum(), IsoType.ALPHA, 12);
    // 授权标识应答码
    // m.setValue(38, null, IsoType.ALPHA, 6);
    // 应答码
    // m.setValue(39, null, IsoType.ALPHA, 2);
    // 终端号
    m.setValue(41, order.getTerminalNum(), IsoType.ALPHA, 8);
    // 商户号
    m.setValue(42, order.getCustomerNo(), IsoType.ALPHA, 15);
    // PadType.RIGHT_SPACE 商户名称
    m.setValue(43, customField43,customField43, IsoType.ALPHA, 40);
    // 附加数据－私有
    // m.setValue(48, null, IsoType.LLLVAR, 0);
    // 交易货币代码
    // m.setValue(49, null, IsoType.ALPHA, 3);
    // 个人标识码
    m.setValue(52, order.getPin(), IsoType.ALPHA, 8);
    // 安全控制信息
    // m.setValue(53, null, IsoType.NUMERIC, 16);
    // IC卡数据域
    m.setValue(55, order.getIcData(), IsoType.LLLVAR, 0);


    /**
     * F60.IC卡60域编码
     */
    String IC_60_FIELD = "00000500030000000000";
    /**
     * F60.磁条卡60域名编码
     */
    String UN_IC_60_FIELLD = "00000200030000000000";
    // 自定义域
    m.setValue(60, UN_IC_60_FIELLD, IsoType.LLLVAR, 0);
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
    m.setValue(61, field61, field61, IsoType.LLLVAR, 0);



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
    m.setValue(128, null, IsoType.BINARY, 8);

    m.debugString();


    ByteBuffer byteBuffer = m.writeToBuffer(2);

    // 组出的报文必须 为totalLength(报文总长) + head(头) + requestType(请求类型) + bitMap + body
    System.out.println( new String(byteBuffer.array()));
    System.out.println(Hex.encodeHexString(byteBuffer).toUpperCase());
    
    FileOutputStream fout = new FileOutputStream("c:/hu/iso.bin");
    m.write(fout, 2);  
    fout.close();  
    print(m); 
    

    System.err.println("PARSE BINARY FROM FILE");
    byte[] buf = new byte[2];
    FileInputStream fin = new FileInputStream("c:/hu/iso.bin");
    fin.read(buf);
    int len = ((buf[0] & 0xff) << 4) | (buf[1] & 0xff);
    buf = new byte[len];
    fin.read(buf);
    fin.close();
//    mfact.setUseBinaryMessages(true);
     
    m = mfact.parseMessage(buf, mfact.getIsoHeader(0x200).length());
    print(m);
  }


}
