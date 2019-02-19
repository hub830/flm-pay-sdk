package com.fox.iso8584.field.parse;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueParseException;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.AlphaValue;

public class AlphaParse extends FieldParse {

  private final static AlphaParse INSTANCE = new AlphaParse();

  private AlphaParse() {}

  public static AlphaParse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws FieldValueParseException {
    int length = fpi.getLength();
    try {
      String _v = new String(buf, pos, length, encoding);
      if (custom == null) {
        AlphaValue<?> alphaValue = new AlphaValue<>(_v.trim(), null, length);
        return alphaValue;
      } else {
        T decoded = custom.decodeField(_v, encoding);
        AlphaValue<?> alphaValue = new AlphaValue<>(decoded, custom, length);
        return alphaValue;
      }
    } catch (Exception e) {
      throw new FieldValueParseException(e);
    }
  }
}
