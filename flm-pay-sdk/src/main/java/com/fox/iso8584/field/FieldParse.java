package com.fox.iso8584.field;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import com.fox.iso8584.CustomField;

public abstract class FieldParse {

  public abstract <T> FieldValue<?> parse(FieldParseInfo fpi, final byte[] buf, final int pos,
      final CustomField<T> custom, String encoding) throws ParseException, UnsupportedEncodingException;


  protected int decodeLength(byte[] buf, int pos, int digits) throws UnsupportedEncodingException {

    switch (digits) {
      case 2:
        return ((buf[pos] - 48) * 10) + (buf[pos + 1] - 48);
      case 3:
        return ((buf[pos] - 48) * 100) + ((buf[pos + 1] - 48) * 10) + (buf[pos + 2] - 48);
      case 4:
        return ((buf[pos] - 48) * 1000) + ((buf[pos + 1] - 48) * 100) + ((buf[pos + 2] - 48) * 10)
            + (buf[pos + 3] - 48);
    }
    return -1;
  }
}
