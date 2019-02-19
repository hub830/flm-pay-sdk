package com.fox.iso8584.field.parse;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueParseException;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.LllvarValue;

public class LllvarParse extends FieldParse {

  private final static LllvarParse INSTANCE = new LllvarParse();

  private LllvarParse() {}

  public static LllvarParse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws FieldValueParseException {

    try {
      final int len = decodeLength(buf, pos, 3);
      String _v = len == 0 ? "" : new String(buf, pos + 3, len, encoding);
      // This is new: if the String's length is different from the specified length in the
      // buffer, there are probably some extended characters. So we create a String from
      // the rest of the buffer, and then cut it to the specified length.
      if (_v.length() != len) {
        _v = new String(buf, pos + 3, buf.length - pos - 3, encoding).substring(0, len);
      }
      if (custom == null) {
        LllvarValue<?> lllvarValue = new LllvarValue<>(_v, null);
        return lllvarValue;
      } else {
        T decoded = custom.decodeField(_v, encoding);
        LllvarValue<?> lllvarValue = new LllvarValue<>(decoded, custom);
        return lllvarValue;
      }
    } catch (Exception e) {
      throw new FieldValueParseException(e);
    }
  }
}
