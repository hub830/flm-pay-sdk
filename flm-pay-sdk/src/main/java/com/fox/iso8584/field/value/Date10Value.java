package com.fox.iso8584.field.value;

import java.text.SimpleDateFormat;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 10位日期类字段 输出格式 MMddHHmmss
 * 
 * @author mux
 *
 */
public class Date10Value<T> extends DatetimeValue<T> {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("MMddHHmmss");

  public Date10Value(T value, CustomField<T> encoder) {
    super(FieldType.DATE10, value, encoder);
  }

  SimpleDateFormat getSimpleDateFormat() {
    return sdf;
  }

  @Override
  public int getValueLength(String charset) {
    return 10;
  }
  
}
