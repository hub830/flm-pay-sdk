package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.value.LlvarValue;

public class LlvarParse extends FieldParse {

  private final static LlvarParse INSTANCE = new LlvarParse();

  private LlvarParse() {}

  public static LlvarParse getInstance() {
    return INSTANCE;
  }

  public <T> LlvarValue<?> parse(FieldParseInfo fpi, final byte[] buf, final int pos,
      final CustomField<T> custom, String encoding)
      throws ParseException, UnsupportedEncodingException {
    if (pos < 0) {
      throw new ParseException("Invalid LLVAR field", pos);
    } else if (pos + 2 > buf.length) {
      throw new ParseException(String.format("Insufficient data for LLVAR header, pos %d", pos),
          pos);
    }
    final int len = decodeLength(buf, pos, 2);
    if (len < 0) {
      throw new ParseException("Invalid LLVAR length", pos);
    } else if (len + pos + 2 > buf.length) {
      throw new ParseException("Insufficient data for LLVAR field", pos);
    }
    String _v;
    try {
      _v = len == 0 ? "" : new String(buf, pos + 2, len, encoding);
    } catch (IndexOutOfBoundsException ex) {
      throw new ParseException("Insufficient data for LLVAR header", pos);
    }
    // This is new: if the String's length is different from the specified
    // length in the buffer, there are probably some extended characters.
    // So we create a String from the rest of the buffer, and then cut it to
    // the specified length.
    if (_v.length() != len) {
      _v = new String(buf, pos + 2, buf.length - pos - 2, encoding).substring(0, len);
    }
    if (custom == null) {
      LlvarValue<?> value = new LlvarValue<>(_v, null);
      return value;
      // return new IsoValue<>(type, _v, len, null);
    } else {
      T dec = custom.decodeField(_v, encoding);

      return new LlvarValue<>(dec, custom);
    }
  }



}
