package com.fox.iso8584.enums;

import lombok.ToString;

/**
 * 8583消息类型
 * 
 * @author mux
 *
 */
@ToString
public enum MessageType {
  PAY("付款",200), //
  REVERSE("冲正",300)//
  ;//

  private String name;

  private int type;

  private MessageType(String name, int type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

}
