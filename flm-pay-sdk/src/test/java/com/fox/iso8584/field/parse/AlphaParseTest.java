package com.fox.iso8584.field.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import com.fox.iso8584.exception.FieldValueParseException;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldType;
import com.fox.iso8584.field.FieldValue;

@RunWith(JUnitPlatform.class)
@DisplayName("ALPHA 字段解析器测试")
class AlphaParseTest {

  AlphaParse alphaParse = AlphaParse.getInstance();
  FieldParseInfo fieldParseInfo;
  String encoding;
  String value;
  byte[] buf;
  int pos;

  @BeforeAll
  public static void init() {

  }

  @BeforeEach
  public void setUp() throws DecoderException {
    value =
        "30323030F23C46C1A8E09A180000000000000001313636323539393630323435313438313736303030303030303030303030323730303030303132343139333833303038373539313139333833303031323432343130353331313035313030313030303630383438353031323730303834383530313131313337363235393936303234353134383137363D32343130323031303030303038383130303030303139303132343038373539313030303030303031373030303030303030303034343031B2CCC9D9C0D72020202020202020202020202020202020202020202020202020202020202020202031353643A8BA686B2881C1323630303030303030303030303030303134359F2608DD5BC16A368E26829F2701809F101307050103A00000010A010000000000E54570769F3704DB9246219F36020096950500000000009A031901249C01009F02060000002700005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34033F00009F3501229F1E0830303030303034388408A0000003330101029F0902008C9F4104000000163032303030303030353030303330303030303030303030303430303231333933323038333736332020202020202020202020202020203030303030303030313932304239413241363734";

    buf = Hex.decodeHex(value);

    encoding = "GBK";
    fieldParseInfo = new FieldParseInfo(FieldType.ALPHA, 40);
    pos = 194;
  }

  /**
   * 测试解析包含中文时的解析</br>
   * 需要注意包含中文时字段长度
   * 
   * @throws FieldValueParseException
   */
  @Test
  void testParseCustomerName() throws FieldValueParseException {
    FieldValue<?> fieldValue = alphaParse.parse(fieldParseInfo, buf, pos, null, encoding);
    assertNotNull(fieldValue);
    assertEquals(fieldValue.toString(), "蔡少雷", "INCONCEIVABLE!");

  }

}
