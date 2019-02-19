package com.fox.iso8584.field.value;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueDecodeEncodeException;
import com.fox.iso8584.exception.FieldValueFormatException;
import com.fox.iso8584.exception.FieldValueWriteException;
import com.fox.iso8584.field.AbstractFieldValue;
import com.fox.iso8584.field.FieldType;

/**
 * 变长类字段
 * 
 * @author mux
 *
 */
public abstract class VarValue<T> extends AbstractFieldValue<T> {


  public VarValue() {}

  public VarValue(FieldType type, T value, CustomField<T> encoder) {
    super(type, value, encoder, 0);// 对于可变长字段，长度预设为0
  }

  @Override
  protected byte[] format(String charset) throws FieldValueFormatException {
    try {
      return value.toString().getBytes(charset);
    } catch (UnsupportedEncodingException e) {
      throw new FieldValueFormatException(e);
    }
  }

  @Override
  public void write(OutputStream out, String charset) throws FieldValueWriteException {
    byte[] data;
    try {
      if (encoder == null) {
        data = format(charset);
      } else {
        data = encoder.encodeField(value, charset).getBytes();
      }
      // 输出可变长字段的总长度
      writeLengthHeader(out, data.length, charset);
      // 输出可变长字段的内容
      out.write(data);
    } catch (Exception e) {
      throw new FieldValueWriteException(e);
    }
  }

  /**
   * 输出可变长字段 的总长度
   * 
   * @param outs
   * @throws IOException
   */
  protected void writeLengthHeader(final OutputStream outs, int valueLength, String encoding)
      throws IOException {
    final int headerLength = getHeaderLength();

    // write the length in ASCII
    if (headerLength == 4) {
      outs.write((valueLength / 1000) + 48);
      outs.write(((valueLength % 1000) / 100) + 48);
    } else if (headerLength == 3) {
      outs.write((valueLength / 100) + 48);
    }
    if (valueLength >= 10) {
      outs.write(((valueLength % 100) / 10) + 48);
    } else {
      outs.write(48);
    }
    outs.write((valueLength % 10) + 48);
  }

  protected abstract int getHeaderLength();

  /**
   * 对于变长的字段 ，值的长度为值的实际长度加上字段头的长度
   * @throws FieldValueDecodeEncodeException 
   * @throws FieldValueWriteException 
   */
  @Override
  public int getValueLength(String charset) throws FieldValueDecodeEncodeException  {
    /*
     * length 根据内容的实际长度去重设长度字段
     */
    if (encoder == null) {
      length = value.toString().length();
    } else {
      length = encoder.encodeField(value, charset).length();
    }
    return length + getHeaderLength();
  }

  @Override
  public String toString() {
    if (value instanceof byte[]) {
      final byte[] _v = (byte[]) value;
      return new String(_v);
    } else {
      return value.toString();
    }
  }
}
