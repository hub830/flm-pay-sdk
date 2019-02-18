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

  /**
   * 指定是否对文本模式下的可变长度字段的长度标头进行解码 使用正确的字符串转换和字符编码。</br>
   * 默认值为false，这意味着使用 ASCII码解码的旧行为。
   */
  protected boolean forceStringEncoding = false;

  public VarValue() {}

  public VarValue(FieldType type, T value, CustomField<T> encoder, String encoding,
      boolean binaryField ) {
    super(type, value, encoder, 0, encoding, binaryField);// 对于可变长字段，长度预设为0
    // 根据内容的实际长度去重设长度字段
    length = encoder == null ? value.toString().length() : encoder.encodeField(value).length();
//    this.forceStringEncoding = forceStringEncoding;
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
    if (binary) {
      if (headerLength == 4) {
        outs.write((((valueLength % 10000) / 1000) << 4) | ((valueLength % 1000) / 100));
      } else if (headerLength == 3) {
        outs.write(valueLength / 100); // 00 to 09 automatically in BCD
      }
      // BCD encode the rest of the length
      outs.write((((valueLength % 100) / 10) << 4) | (valueLength % 10));
    } else if (forceStringEncoding) {
      String lhead = Integer.toString(valueLength);
      final int ldiff = headerLength - lhead.length();
      if (ldiff == 1) {
        lhead = '0' + lhead;
      } else if (ldiff == 2) {
        lhead = "00" + lhead;
      } else if (ldiff == 3) {
        lhead = "000" + lhead;
      }
      outs.write(encoding == null ? lhead.getBytes() : lhead.getBytes(encoding));
    } else {
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
    return length+ getHeaderLength();
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
