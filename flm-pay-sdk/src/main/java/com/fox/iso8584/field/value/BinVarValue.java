package com.fox.iso8584.field.value;

import java.io.UnsupportedEncodingException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;
import com.solab.iso8583.util.HexCodec;

/**
 * 二进制变长字段
 * 
 * @author mux
 *
 */
public abstract class BinVarValue<T> extends VarValue<T> {

  public BinVarValue(FieldType type, T value, CustomField<T> encoder, String encoding,
      boolean binaryField) {
    super(type, value, encoder, encoding, binaryField);

    if (value instanceof byte[]) {
      length = ((byte[]) value).length;
    } else {
      length = value.toString().length() / 2 + (value.toString().length() % 2);
    }
  }


  @Override
  protected byte[] format() throws UnsupportedEncodingException {
    byte[] v;
    if (binary) {
      v = formatBinary();
    } else {
      v = formatAscii();
    }
    return v;
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
