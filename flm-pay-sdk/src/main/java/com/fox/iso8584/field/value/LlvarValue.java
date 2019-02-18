package com.fox.iso8584.field.value;

import java.io.UnsupportedEncodingException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldType;

/**
 * 二字节头长度变长类字段
 * 
 * @author mux
 *
 */
public class LlvarValue<T> extends VarValue<T> {

  public LlvarValue(T value, CustomField<T> encoder, String encoding 
      ) {
    super(FieldType.LLVAR, value, encoder, encoding);
  }

  @Override
  protected int getHeaderLength() {
    return 2;
  }

  protected int decodeLength(byte[] buf, int pos, int digits) throws UnsupportedEncodingException {

    switch (digits) {
      case 2:
        return ((buf[pos] - 48) * 10) + (buf[pos + 1] - 48);
      case 3:
        return ((buf[pos] - 48) * 100) + ((buf[pos + 1] - 48) * 10) + (buf[pos + 2] - 48);
      case 4:
        return ((buf[pos] - 48) * 1000) + ((buf[pos + 1] - 48) * 100) + ((buf[pos + 2] - 48) * 10)
            + (buf[pos + 3] - 48);
    }
    return -1;
  }


}
