package com.fox.iso8584.field.parse;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueParseException;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.LllBinVarValue;

public class LllbinParse extends FieldParse {

  private final static LllbinParse INSTANCE = new LllbinParse();

  private LllbinParse() {}

  public static LllbinParse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String charset) throws FieldValueParseException {

    try {
      final int l = decodeLength(buf, pos, 3);

      byte[] _v = new byte[l];
      System.arraycopy(buf, pos + 3, _v, 0, l);

      if (custom == null) {
        LllBinVarValue<?> lllBinVarValue = new LllBinVarValue<>(_v, null);
        return lllBinVarValue;
      } else {
        T dec = custom.decodeField(new String(buf, pos + 3, l), charset);
        LllBinVarValue<?> lllBinVarValue = new LllBinVarValue<>(dec, custom);
        return lllBinVarValue;
      }
    } catch (Exception e) {
      throw new FieldValueParseException(e);
    }  
  }
}
