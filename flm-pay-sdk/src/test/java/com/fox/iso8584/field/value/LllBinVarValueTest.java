package com.fox.iso8584.field.value;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldValue;

@RunWith(JUnitPlatform.class)
@DisplayName("三位变长二进制字段格式化测试用例")
@SuppressWarnings(value = {"unchecked", "rawtypes"})
class LllBinVarValueTest {

  private String value;
  private String encoding;
  private int length;
  private CustomField encoder;

  @BeforeEach
  void setUp() throws Exception {
    value =
        "9F2608DD5BC16A368E26829F2701809F101307050103A00000010A010000000000E54570769F3704DB9246219F36020096950500000000009A031901249C01009F02060000002700005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34033F00009F3501229F1E0830303030303034388408A0000003330101029F0902008C9F410400000016";
    encoding = "GBK";
    length = 8;
    encoder = null;
  }

  @Test
  void testFormat() throws UnsupportedEncodingException {
    LllBinVarValue lllBin2VarValue = new LllBinVarValue(value, encoder, encoding);
    assertNotNull(lllBin2VarValue);
 
    byte[] format = lllBin2VarValue.format();
    String encodeHex = Hex.encodeHexString(format).toUpperCase();
    System.out.println("格式化:" + encodeHex);
    assertEquals(value, encodeHex);
  }
  
  @Test
  void testWriteData() throws IOException {
    LllBinVarValue lllBin2VarValue = new LllBinVarValue(value, encoder, encoding);
    assertNotNull(lllBin2VarValue);

    ByteArrayOutputStream bout = new ByteArrayOutputStream();    
    lllBin2VarValue.write(bout);     
    
    String encodeHex = Hex.encodeHexString(bout.toByteArray()).toUpperCase();
    System.out.println("格式化:" + encodeHex);

  }

  @Test
  void testGetValueLength() {
    LllBinVarValue lllBin2VarValue = new LllBinVarValue(value, encoder, encoding);
    assertNotNull(lllBin2VarValue);

    int valueLength = lllBin2VarValue.getValueLength();
    assertEquals(148, valueLength);
  }

}
