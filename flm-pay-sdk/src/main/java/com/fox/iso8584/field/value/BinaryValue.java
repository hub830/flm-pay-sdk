package com.fox.iso8584.field.value;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.AbstractFieldValue;
import com.fox.iso8584.field.FieldType;
import com.solab.iso8583.util.HexCodec;

/**
 * 二进制字段
 * 
 * @author mux
 *
 */
public class BinaryValue<T> extends AbstractFieldValue<T> {


  public BinaryValue(T value, CustomField<T> encoder, int length, String encoding,
      boolean binaryField) {
    super(FieldType.BINARY, value, encoder, length, encoding, binaryField);
  }

  @Override
  protected byte[] format() {
    byte[] v;
    if (binary) {
      v = formatBinary();

    } else {
      v = formatAscii();
    }


    byte[] result = new byte[length];

    for (int i = 0; i < length; i++) {
      result[i] = 0;
    }

    System.arraycopy(v, 0, result, 0, v.length);
    return result;
  }

  private byte[] formatBinary() {
    if (value instanceof byte[]) {
      return (byte[]) value;
    } else {
      byte[] binval = HexCodec.hexDecode(value.toString());
      return binval;
    }
  }

  private byte[] formatAscii() {

    if (value instanceof byte[]) {
      final byte[] _v = (byte[]) value;
      return HexCodec.hexEncode(_v, 0, _v.length).getBytes();
    } else {
      return value.toString().getBytes();
    }
  }

  @Override
  public String toString() {
    if (value instanceof byte[]) {
      final byte[] _v = (byte[]) value;
      return HexCodec.hexEncode(_v, 0, _v.length);
    } else {
      return value.toString();
    }
  }

}
