package com.fox.iso8584.field.value;

import java.text.SimpleDateFormat;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.AbstractFieldValue;
import com.fox.iso8584.field.FieldType;

/**
 * 日期类字段
 * 
 * @author mux
 *
 */
public abstract class DatetimeValue<T> extends AbstractFieldValue<T> {

  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  
  public DatetimeValue(FieldType type, T value, CustomField<T> encoder) {
    super(type, value, encoder, 0);
  }

  @Override
  protected byte[] format(String charset) {
    SimpleDateFormat sdf = getSimpleDateFormat();
    return sdf.format(value).getBytes();
  }

  abstract SimpleDateFormat getSimpleDateFormat();

  @Override
  public String toString() {
    return sdf.format(value);
  }

}
