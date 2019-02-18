package com.fox.iso8584.field.value;

import java.text.SimpleDateFormat;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 4位日期类字段 输出格式MMdd
 * 
 * @author mux
 *
 */
public class Date4Value<T> extends DatetimeValue<T> {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("MMdd");

  public Date4Value(T value, CustomField<T> encoder, String encoding) {
    super(FieldType.DATE4, value, encoder, encoding);
  }

  @Override
  SimpleDateFormat getSimpleDateFormat() {
    return sdf;
  }

  @Override
  public int getValueLength() {
    return 4;
  }
}
