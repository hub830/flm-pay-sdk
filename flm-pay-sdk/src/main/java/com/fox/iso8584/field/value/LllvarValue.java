package com.fox.iso8584.field.value;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 三字节头长度变长字段
 * 
 * @author mux
 *
 */
public class LllvarValue<T> extends VarValue<T> {

  public LllvarValue(T value, CustomField<T> encoder) {
    super(FieldType.LLLVAR, value, encoder);
  }

  @Override
  protected int getHeaderLength() {
    return 3;
  }

}
