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
import com.fox.iso8584.IsoType;
import com.fox.iso8584.IsoValue;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.NumericValue;
import com.fox.iso8584.util.Bcd;

public class NumericParse extends FieldParse {

  private final static NumericParse INSTANCE = new NumericParse();

  private NumericParse() {}

  public static NumericParse getInstance() {
    return INSTANCE;
  }


  public <T> FieldValue<T> parse(FieldParseInfo fpi, final byte[] buf, final int pos,
      final CustomField<T> custom, String encoding)
      throws ParseException, UnsupportedEncodingException {


    int length = fpi.getLength();

    try {
      String _v = new String(buf, pos, length, encoding);
      if (_v.length() != length) {
        _v = new String(buf, pos, buf.length - pos, encoding).substring(0, length);
      }
      if (custom == null) {
        NumericValue numericValue = new NumericValue<>(_v, null, length, encoding);
        return numericValue;
      } else {
        T decoded = custom.decodeField(_v, encoding);

        NumericValue numericValue = new NumericValue<>(decoded, custom, length, encoding);
        return numericValue;
      }
    } catch (StringIndexOutOfBoundsException ex) {
      throw new ParseException(String.format("Insufficient data for %s  of length %d, pos %d",
          fpi.getType(), length, pos), pos);
    }

  }

}
