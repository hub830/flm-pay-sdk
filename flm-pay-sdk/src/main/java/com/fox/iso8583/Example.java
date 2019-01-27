package com.fox.iso8583;



import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.codec.binary.Hex;
import com.fox.iso8583.impl.SimpleTraceGenerator;
import com.fox.iso8583.parse.ConfigParser; 


/**
 * This little example program creates a message factory out of a XML config file, creates a new
 * message, and parses a couple of message from a text file.
 * 
 * @author Enrique Zamudio
 */
public class Example {

  public static void print(IsoMessage m) {
    System.out.printf("TYPE: %04x\n", m.getType());
    for (int i = 2; i < 128; i++) {
      if (m.hasField(i)) {
        System.out.printf("F %3d(%s): %s -> '%s'\n", i, m.getField(i).getType(),
            m.getObjectValue(i), m.getField(i).toString());
      }
    }
  }

  public static void main(String[] args) throws Exception {
    MessageFactory mfact = ConfigParser.createFromClasspathConfig("config.xml");
    mfact.setAssignDate(true);
    mfact.setTraceNumberGenerator(
        new SimpleTraceGenerator((int) (System.currentTimeMillis() % 100000)));
/*    LineNumberReader reader = new LineNumberReader(new InputStreamReader(
        Example.class.getClassLoader().getResourceAsStream("j8583/example/parse.txt")));
    String line = reader.readLine();
    while (line != null && line.length() > 0) {
      IsoMessage m = mfact.parseMessage(line.getBytes(), 12);
      print(m);
      line = reader.readLine();
    }
    reader.close();*/

    // Create a new message
    System.err.println("NEW MESSAGE");
    IsoMessage m = mfact.newMessage(0x200);
//    m.setBinary(true);
    m.setValue(4, new BigDecimal("501.25"), IsoType.AMOUNT, 0);
    m.setValue(12, new Date(), IsoType.TIME, 0);
    m.setValue(15, new Date(), IsoType.DATE4, 0);
    m.setValue(17, new Date(), IsoType.DATE_EXP, 0);
    m.setValue(37, 12345678, IsoType.NUMERIC, 12);
    m.setValue(41, "TEST-TERMINAL", IsoType.ALPHA, 16);
    FileOutputStream fout = new FileOutputStream("c:/hu/iso.bin");

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    
    m.write(bout, 2);

      byte[] bytes = bout.toByteArray();
      bout.close();
      System.out.println(
            Hex.encodeHexString(bytes).toUpperCase());
    print(m);
    System.err.println("PARSE BINARY FROM FILE");
    byte[] buf = new byte[2];
    FileInputStream fin = new FileInputStream("c:/hu/iso.bin");
    fin.read(buf);
    int len = ((buf[0] & 0xff) << 4) | (buf[1] & 0xff);
    buf = new byte[len];
    fin.read(buf);
    fin.close();
    mfact.setUseBinaryMessages(true);
    m = mfact.parseMessage(buf, mfact.getIsoHeader(0x200).length());
    print(m);
  }

}
