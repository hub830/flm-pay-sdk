/*
 * j8583 A Java implementation of the ISO8583 protocol Copyright (C) 2011 Enrique Zamudio Lopez
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA
 */
package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.IsoValue;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.LllBinVarValue;
import com.fox.iso8584.util.HexCodec;

public class LllbinParse extends FieldParse {

  private final static LllbinParse INSTANCE = new LllbinParse();

  private LllbinParse() {}

  public static LllbinParse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<T> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws ParseException, UnsupportedEncodingException {


    final int l = decodeLength(buf, pos, 3);

    byte[] _v = new byte[l];
    System.arraycopy(buf, pos+3, _v, 0, l);
    
    if (custom == null) {
      LllBinVarValue lllBinVarValue = new LllBinVarValue<>(_v, null, encoding, true);// TODO
                                                                                             // 是否二进制
      return lllBinVarValue;
    } else {
      try {
        T dec = custom.decodeField(l == 0 ? "" : new String(buf, pos + 3, l), encoding);

        LllBinVarValue lllBinVarValue = new LllBinVarValue<>(dec, custom, encoding, false);// TODO
                                                                                              // 是否二进制
        return lllBinVarValue;
      } catch (IndexOutOfBoundsException ex) {
        throw new ParseException(
            String.format("Insufficient data for LLLBIN , pos %d len %d", pos, l), pos);
      }
    }

  }



}
