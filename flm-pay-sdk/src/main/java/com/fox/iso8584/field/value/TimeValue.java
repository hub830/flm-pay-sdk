package com.fox.iso8584.field.value;

import java.text.SimpleDateFormat;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 时间字段
 * 
 * @author mux
 *
 */
public class TimeValue<T> extends DatetimeValue<T> {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");

  public TimeValue(T value, CustomField<T> encoder) {
    super(FieldType.TIME, value, encoder);
  }

  @Override
  SimpleDateFormat getSimpleDateFormat() {
    return sdf;
  }

  @Override
  public int getValueLength(String charset) {
    return 6;
  }
}
