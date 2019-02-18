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
public class Example2 {


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
    mfact.setTraceNumberGenerator(
        new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));
    mfact.setCharacterEncoding("GBK");

 

    System.err.println("----------------"); 

     byte[] aaa = "30323030F23C46C1A8E09A180000000000000001313636323539393630323435313438313736303030303030303030303030323730303030303132343139333833303038373539313139333833303031323432343130353331313035313030313030303630383438353031323730303834383530313131313337363235393936303234353134383137363D32343130323031303030303038383130303030303139303132343038373539313030303030303031373030303030303030303034343031B2CCC9D9C0D72020202020202020202020202020202020202020202020202020202020202020202031353643A8BA686B2881C1323630303030303030303030303030303134359F2608DD5BC16A368E26829F2701809F101307050103A00000010A010000000000E54570769F3704DB9246219F36020096950500000000009A031901249C01009F02060000002700005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34033F00009F3501229F1E0830303030303034388408A0000003330101029F0902008C9F4104000000163032303030303030353030303330303030303030303030303430303231333933323038333736332020202020202020202020202020203030303030303030313932304239413241363734".getBytes();
     test1();
     for(int i=0;i<900;i++)
     {
       int a = ((aaa[i] - 48) * 10) + aaa[i + 1] - 49;
       System.out.println(i+":"+a);
     }
     
     
     
     System.err.println(new String(aaa));
     IsoMessage message = mfact.parseMessage(Hex.decodeHex(new String(aaa)), 0);
     print(message.getIsoBody());
  }

  public static void test1() {
    byte[] aaa = "30313234313933383330".getBytes();
    System.out.println(aaa);
  }
}
