package com.fox.iso8584.field;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fox.iso8584.CustomField;

public abstract class AbstractFieldValue<T> implements FieldValue<T> , Cloneable  {

  private static Logger log = LoggerFactory.getLogger(AbstractFieldValue.class);

  protected FieldType type;

  protected T value;

  protected CustomField<T> encoder;

  protected int length;

  public AbstractFieldValue() {}

  public AbstractFieldValue(FieldType type, T value, CustomField<T> encoder, int length) {
    super();
    this.type = type;
    this.value = value;
    this.encoder = encoder;
    this.length = length;
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

  /**
   * 对于不可变长字段，值 的长度直接为其定义时设置的长度
   */
  @Override
  public int getValueLength(String charset) {
    return length;
  }

  /**
   * 将值格式化输出
   * 
   * @param charset
   * @param forceStringDecoding 是否对文本模式下的可变长度字段的长度标头进行解码
   * @return
   * @throws UnsupportedEncodingException
   */
  protected abstract byte[] format(String charset) throws UnsupportedEncodingException;

  @Override
  public void write(OutputStream out, String charset)
      throws UnsupportedEncodingException, IOException {
    byte[] data =
        encoder == null ? format(charset) : encoder.encodeField(value, charset).getBytes();
    out.write(data);
  }

  /** Returns a copy of the receiver that references the same value object. */
  @SuppressWarnings("unchecked")
  public FieldValue<T> clone() {
    try {
      return (FieldValue<T>) super.clone();
    } catch (CloneNotSupportedException ex) {
      log.error("FieldValue clone 失败", ex);
      return null;
    }
  }

  @Override
  public String toString() {
    return value.toString();
  }

}
