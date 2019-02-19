package com.fox.iso8584.field.parse;

import java.math.BigDecimal;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueParseException;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.AmountValue;

public class AmountParse extends FieldParse {

  private final static AmountParse INSTANCE = new AmountParse();

  private AmountParse() {}

  public static AmountParse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws FieldValueParseException {
    try {
      String c = new String(buf, pos, 12, encoding);
      AmountValue<?> amountValue = new AmountValue<>(new BigDecimal(c).movePointLeft(2), null);
      return amountValue;
    } catch (Exception e) {
      throw new FieldValueParseException(e);
    }
  }
}
