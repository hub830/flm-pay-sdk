package com.fox.iso8584.field.value;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.AbstractFieldValue;
import com.fox.iso8584.field.FieldType;

/**
 * 金额字段
 * 
 * @author mux
 *
 */
public class AmountValue<T> extends AbstractFieldValue<T> {

  public AmountValue(T value, CustomField<T> encoder) {
    super(FieldType.AMOUNT, value, encoder, 0);
  }

  @Override
  public byte[] format(String charset) throws UnsupportedEncodingException {
    BigDecimal v = (BigDecimal) value;
    return String.format("%012d", v.movePointRight(2).longValue()).getBytes(charset);
  }

  @Override
  public int getValueLength(String charset) {
    return 12;
  }

}
