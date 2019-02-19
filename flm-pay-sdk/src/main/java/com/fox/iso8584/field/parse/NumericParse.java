package com.fox.iso8584.field.parse;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueParseException;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.NumericValue;

public class NumericParse extends FieldParse {

  private final static NumericParse INSTANCE = new NumericParse();

  private NumericParse() {}

  public static NumericParse getInstance() {
    return INSTANCE;
  }

  public <T> FieldValue<?> parse(FieldParseInfo fpi, final byte[] buf, final int pos,
      final CustomField<T> custom, String encoding) throws FieldValueParseException {

    int length = fpi.getLength();

    try {
      String _v = new String(buf, pos, length, encoding);
      if (_v.length() != length) {
        _v = new String(buf, pos, buf.length - pos, encoding).substring(0, length);
      }
      if (custom == null) {
        NumericValue<?> numericValue = new NumericValue<>(_v, null, length);
        return numericValue;
      } else {
        T decoded = custom.decodeField(_v, encoding);
        NumericValue<?> numericValue = new NumericValue<>(decoded, custom, length);
        return numericValue;
      }
    } catch (Exception e) {
      throw new FieldValueParseException(e);
    }
  }

}
