package com.fox.iso8584.field.value;

import java.io.IOException;
import java.io.OutputStream;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 三字节头长度变长二进制字段
 * 
 * @author mux
 *
 */
public class LllBin2VarValue<T> extends BinVarValue<T> {


  public LllBin2VarValue(T value, CustomField<T> encoder, String encoding, boolean binaryField) {
    super(FieldType.LLLBIN2, value, encoder, encoding, binaryField);
  }


  @Override
  protected int getHeaderLength() {
    return 3;
  }

/*
  @Override
  protected void writeLengthHeader(OutputStream outs, int valueLength, String encoding)
      throws IOException {
    final int headerLength = getHeaderLength();
    // write the length in ASCII

    outs.write((valueLength / 100) + 48);
    
    if (valueLength >= 10) {
      outs.write(((valueLength % 100) / 10) + 48);
    } else {
      outs.write(48);
    }
    outs.write((valueLength % 10) + 48);
  }*/

}
