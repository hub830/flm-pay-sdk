package com.fox.iso8584.exception;

/**
 * 域解析异常
 * @author mux
 *
 */
public class MessageParseException extends Exception {

  private static final long serialVersionUID = 5770390658812498742L;

  public MessageParseException(String message) {
    super(message);
  }

  public MessageParseException(Throwable cause) {
    super(cause);
  }

  public MessageParseException(String message, Throwable cause) {
    super(message, cause);
  }

}
