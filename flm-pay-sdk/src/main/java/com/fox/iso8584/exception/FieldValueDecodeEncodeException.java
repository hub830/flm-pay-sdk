package com.fox.iso8584.exception;

/**
 * 组合域编解码异常
 * @author mux
 *
 */
public class FieldValueDecodeEncodeException extends Exception {

  private static final long serialVersionUID = 5770390658812498742L;

  public FieldValueDecodeEncodeException(Throwable cause) {
    super(cause);
  }

  public FieldValueDecodeEncodeException(String message) {
    super(message);
  }

  public FieldValueDecodeEncodeException(String message, Throwable cause) {
    super(message, cause);
  }

}
