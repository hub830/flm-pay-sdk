package com.fox.iso8584.field.value;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 三字节头长度变长二进制字段
 * 
 * @author mux
 *
 */
public class LllBinVarValue<T> extends BinVarValue<T> {


  public LllBinVarValue(T value, CustomField<T> encoder, String encoding ) {
    super(FieldType.LLLBIN, value, encoder, encoding);
  }


  @Override
  protected int getHeaderLength() {
    return 3;
  }

}
