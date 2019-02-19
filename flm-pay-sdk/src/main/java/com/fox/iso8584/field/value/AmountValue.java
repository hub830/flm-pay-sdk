package com.fox.iso8584.field.value;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueFormatException;
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
  public byte[] format(String charset) throws FieldValueFormatException {
    BigDecimal v = (BigDecimal) value;
    try {
      return String.format("%012d", v.movePointRight(2).longValue()).getBytes(charset);
    } catch (UnsupportedEncodingException e) {
      throw new FieldValueFormatException(e);
    }
  }

  @Override
  public int getValueLength(String charset) {
    return 12;
  }

}
