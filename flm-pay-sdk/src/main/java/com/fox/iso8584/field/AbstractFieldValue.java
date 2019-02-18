package com.fox.iso8584.field;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import com.fox.iso8584.CustomField;

public abstract class AbstractFieldValue<T> implements FieldValue<T> {

  protected FieldType type;

  protected T value;

  protected CustomField<T> encoder;

  protected int length;

  protected String encoding;

  protected boolean binary;

  public AbstractFieldValue() {}

  public AbstractFieldValue(FieldType type, T value, CustomField<T> encoder, int length,
      String encoding, boolean binary) {
    super();
    this.type = type;
    this.value = value;
    this.encoder = encoder;
    this.length = length;
    this.encoding = encoding;
    this.binary = binary;
  }

  public FieldType getType() {
    return type;
  }

  public T getValue() {
    return value;
  }

  public CustomField<T> getEncoder() {
    return encoder;
  }

  public int getLength() {
    return length;
  }

  @Override
  public String getEncoding() {
    return encoding;
  }

  public boolean isBinary() {
    return binary;
  }

  @Override
  public int getValueLength() { 
    
    

    return length;
/*    try {
      if (binary) {
        return length;
      }
      return value.toString().getBytes(encoding).length;
      
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return 0;*/
  }

  /**
   * 将值格式化输出
   * 
   * @param charset
   * @param forceStringDecoding 是否对文本模式下的可变长度字段的长度标头进行解码
   * @return
   * @throws UnsupportedEncodingException
   */
  protected abstract byte[] format() throws UnsupportedEncodingException;

  @Override
  public void write(OutputStream out) throws UnsupportedEncodingException, IOException {
    byte[] data = encoder == null ? format() : encoder.encodeField(value).getBytes();
    out.write(data);
  }

  /** Returns a copy of the receiver that references the same value object. */
  @SuppressWarnings("unchecked")
  public FieldValue<T> clone() {
    try {
      return (FieldValue<T>) super.clone();
    } catch (CloneNotSupportedException ex) {
      return null;
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
