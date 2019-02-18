package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.BinaryValue;

public class BinaryParse extends FieldParse {

  private final static BinaryParse INSTANCE = new BinaryParse();

  private BinaryParse() {}

  public static BinaryParse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws ParseException, UnsupportedEncodingException {

    int length = fpi.getLength();
    byte[] _v = new byte[length];
    System.arraycopy(buf, pos, _v, 0, length);
    if (custom == null) {
      BinaryValue<?> binaryValue = new BinaryValue<>(_v, null, length);

      return binaryValue;

    } else {
      String v = new String(buf, pos, length * 2, encoding);
      T dec = custom.decodeField(v, encoding);

      return new BinaryValue<>(dec, custom, length);
    }
  }
}
