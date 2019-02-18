package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import com.fox.iso8584.CustomField;
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
  public <T> FieldValue<?>  parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws ParseException, UnsupportedEncodingException {
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
    } catch (StringIndexOutOfBoundsException ex) {
      throw new ParseException(String.format("Insufficient data for %s  of length %d, pos %d",
          fpi.getType(), length, pos), pos);
    }


  }



}
