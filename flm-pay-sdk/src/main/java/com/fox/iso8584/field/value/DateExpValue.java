package com.fox.iso8584.field.value;

import java.text.SimpleDateFormat;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 信用卡有效字段
 * 
 * @author mux
 *
 */
public class DateExpValue<T> extends DatetimeValue<T> {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyMM");

  public DateExpValue(T value, CustomField<T> encoder, String encoding) {
    super(FieldType.DATE_EXP, value, encoder, encoding);
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
