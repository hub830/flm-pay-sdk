package com.fox.iso8584.exception;
/**
 * 域输出异常
 * @author mux
 *
 */
public class FieldValueWriteException extends Exception {

  private static final long serialVersionUID = 5770390658812498742L;

  public FieldValueWriteException(Throwable cause) {
    super(cause);
  }

  public FieldValueWriteException(String message, Throwable cause) {
    super(message, cause);
  }

}
