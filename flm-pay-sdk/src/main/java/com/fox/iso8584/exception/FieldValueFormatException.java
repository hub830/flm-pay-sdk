package com.fox.iso8584.exception;

/**
 * 域格式化输出异常
 * @author mux
 *
 */
public class FieldValueFormatException extends Exception {
  private static final long serialVersionUID = 5770390658812498742L;


  public FieldValueFormatException(Throwable cause) {
    super(cause);
  }

  public FieldValueFormatException(String message, Throwable cause) {
    super(message, cause);
  }
}
