package com.fox.iso8584.field.value;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import com.fox.iso8584.CustomField;

@RunWith(JUnitPlatform.class)
@DisplayName("二进制字段格式化测试用例")
@SuppressWarnings(value = {"unchecked", "rawtypes"})
class BinaryValueTest {

  private String value;
  private String encoding;
  private int length;
  private CustomField encoder;

  @BeforeEach
  void setUp() throws Exception {
    value = "43A8BA686B2881C1";
    encoding = "GBK";
    length = 8;
    encoder = null;
  }

  @Test
  void testFormat_binary() throws DecoderException {
    byte[] decodeHex = Hex.decodeHex(value);
    BinaryValue binaryValue = new BinaryValue(decodeHex, encoder, length, encoding, true);
    assertNotNull(binaryValue);

    byte[] format = binaryValue.format();
    String encodeHex = Hex.encodeHexString(format).toUpperCase();
    assertEquals(value,encodeHex);
  }

  @Test
  void testFormat() throws DecoderException {
    BinaryValue binaryValue = new BinaryValue(value, encoder, length, encoding, true);
    assertNotNull(binaryValue);

    byte[] format = binaryValue.format();
    String encodeHex = Hex.encodeHexString(format).toUpperCase();
    assertEquals(value,encodeHex);
  }

  @Test
  void testToString_binary() throws DecoderException {
    byte[] decodeHex = Hex.decodeHex(value);
    BinaryValue binaryValue = new BinaryValue(decodeHex, encoder, length, encoding, true);
    assertNotNull(binaryValue);

    String toString = binaryValue.toString();
    assertEquals(value,toString);
  }

}
