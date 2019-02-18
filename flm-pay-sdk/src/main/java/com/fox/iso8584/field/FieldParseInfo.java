package com.fox.iso8584.field;

import java.util.List;

public class FieldParseInfo {
  private FieldType type;
  private int length;
  private List<FieldParseInfo> subFieldParse;

  public FieldParseInfo(FieldType type, int length) {
    super();
    this.type = type;
    this.length = length;
  }

  public FieldType getType() {
    return type;
  }

  public void setType(FieldType type) {
    this.type = type;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public List<FieldParseInfo> getSubFieldParse() {
    return subFieldParse;
  }

  public void setSubFieldParse(List<FieldParseInfo> subFieldParse) {
    this.subFieldParse = subFieldParse;
  }


}
