package com.fox.iso8584;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Test1 {

  public static void main(String[] args) throws DecoderException, IOException {


    String aaa = "9f2608dd5bc16a368e26829f2701809f101307050103a00000010a010000000000e54570769f3704db9246219f36020096950500000000009a031901249c01009f02060000002700005f2a02015682027c009f1a0201569f03060000000000009f3303e0e1c89f34033f00009f3501229f1e0830303030303034388408a0000003330101029f0902008c9f410400000016";
    byte[] decodeHex = Hex.decodeHex(
        aaa
            .toCharArray());

    IsoValue<byte[]> field55 = new IsoValue<byte[]>(IsoType.LLLBIN_2, decodeHex);
    field55.setBinaryField(true);


    System.err.println(field55.toString());
    System.err.println("---------------");
    ByteArrayOutputStream boutTemp = new ByteArrayOutputStream();
    field55.write(boutTemp, true, false);
    System.err.println("---------------");
    System.err.println(new String(boutTemp.toByteArray()));
    System.err.println("---------------");
    System.err.println(Hex.encodeHexString(boutTemp.toByteArray()).toUpperCase());

  }
}
