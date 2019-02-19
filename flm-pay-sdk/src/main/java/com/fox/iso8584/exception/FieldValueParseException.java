package com.fox.iso8584.exception;

/**
 * 域解析异常
 * @author mux
 *
 */
public class FieldValueParseException extends Exception {

  private static final long serialVersionUID = 5770390658812498742L;

  public FieldValueParseException(String message) {
    super(message);
  }

  public FieldValueParseException(Throwable cause) {
    super(cause);
  }

  public FieldValueParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
