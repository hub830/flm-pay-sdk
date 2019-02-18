package com.fox.iso8584.field.value;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.AbstractFieldValue;
import com.fox.iso8584.field.FieldType;

/**
 * 数字类字段
 * 
 * @author mux
 *
 */
public class NumericValue<T> extends AbstractFieldValue<T> {


  public NumericValue(T value, CustomField<T> encoder, int length, String encoding) {
    super(FieldType.NUMERIC, value, encoder, length, encoding, false);
  }

  @Override
  protected byte[] format() {
    if (value instanceof Number) {
      String x = String.format(String.format("%%0%dd", length), value);
      if (x.length() > length) {
        throw new IllegalArgumentException(
            "Numeric value is larger than intended length: " + value + " LEN " + length);
      }
      return x.getBytes();
    } else {
      char[] c = new char[length];
      char[] x = value.toString().toCharArray();
      if (x.length > length) {
        throw new IllegalArgumentException(
            "Numeric value is larger than intended length: " + value + " LEN " + length);
      }
      int lim = c.length - x.length;
      for (int i = 0; i < lim; i++) {
        c[i] = '0';
      }
      System.arraycopy(x, 0, c, lim, x.length);
      return new String(c).getBytes();
    }
  }
}
