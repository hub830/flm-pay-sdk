package com.fox.iso8584.exception;

import java.text.MessageFormat;
import com.fox.iso8584.field.FieldType;

public class FieldTypeNotSupportException extends RuntimeException {
  private static final long serialVersionUID = 5770390658812498742L;

  public FieldTypeNotSupportException(FieldType type) {
    super(format(type));
  }


  static String format(FieldType type) {

    return MessageFormat.format("不支持指定的Field类型[{0}]", //
        type);
  }
}
