package com.fox.iso8584.field.value;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.AbstractFieldValue;
import com.fox.iso8584.field.FieldType;
import com.solab.iso8583.util.HexCodec;

/**
 * 变长类字段
 * 
 * @author mux
 *
 */
public abstract class VarValue<T> extends AbstractFieldValue<T> {


  public VarValue() {}

  public VarValue(FieldType type, T value, CustomField<T> encoder, String encoding) {
    super(type, value, encoder, 0, encoding);// 对于可变长字段，长度预设为0
    // 根据内容的实际长度去重设长度字段
    length = encoder == null ? value.toString().length() : encoder.encodeField(value).length();
    // this.forceStringEncoding = forceStringEncoding;
  }

  @Override
  protected byte[] format() throws UnsupportedEncodingException {
    return value.toString().getBytes(encoding);
  }

  @Override
  public void write(OutputStream out) throws UnsupportedEncodingException, IOException {


    byte[] data = encoder == null ? format() : encoder.encodeField(value).getBytes();
    // 输出可变长字段的总长度
    writeLengthHeader(out, data.length, encoding);
    // 输出可变长字段的内容
    out.write(data);
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
   */
  @Override
  public int getValueLength() {
    /*
     * length 在字段创建时会由构造方法根据值的实际长度设置
     */
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
