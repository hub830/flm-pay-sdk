/*
 * j8583 A Java implementation of the ISO8583 protocol Copyright (C) 2007 Enrique Zamudio Lopez
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
package com.fox.iso8584.codecs;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueDecodeEncodeException;
import com.fox.iso8584.field.FieldParseFactory;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;


/**
 * A codec to manage subfields inside a field of a certain type.
 *
 * @author Enrique Zamudio Date: 25/11/13 11:25
 */
public class CompositeField implements CustomField<CompositeField> {

  /** Stores the subfields. */
  @SuppressWarnings("rawtypes")
  private List<FieldValue> values;
  /** Stores the parsers for the subfields. */
  private List<FieldParseInfo> parsers;

  @SuppressWarnings("rawtypes")
  public void setValues(List<FieldValue> values) {
    this.values = values;
  }

  @SuppressWarnings("rawtypes")
  public List<FieldValue> getValues() {
    return values;
  }

  public CompositeField addValue(FieldValue<?> v) {
    if (values == null) {
      values = new ArrayList<>(4);
    }
    values.add(v);
    return this;
  }

  @SuppressWarnings("unchecked")
  public <T> FieldValue<T> getField(int idx) {
    if (idx < 0 || idx >= values.size())
      return null;
    return values.get(idx);
  }

  public <T> T getObjectValue(int idx) {
    FieldValue<T> v = getField(idx);
    return v == null ? null : v.getValue();
  }


  public CompositeField addParser(FieldParseInfo fpi) {
    if (parsers == null) {
      parsers = new ArrayList<>(4);
    }
    parsers.add(fpi);
    return this;
  }

  @Override
  public CompositeField decodeField(String value, String charset)
      throws FieldValueDecodeEncodeException {
    @SuppressWarnings("rawtypes")
    List<FieldValue> vals = new ArrayList<>(parsers.size());
    byte[] buf = value.getBytes();
    int pos = 0;
    try {
      for (FieldParseInfo fpi : parsers) {
        FieldValue<?> v = FieldParseFactory.parse(fpi, buf, pos, null, charset);
        if (v != null) {
          pos += v.getValueLength(charset);
          vals.add(v);
        }
      }
      final CompositeField f = new CompositeField();
      f.setValues(vals);
      return f;
    } catch (Exception e) {
      throw new FieldValueDecodeEncodeException(e);
    }
  }


  @Override
  public String encodeField(CompositeField value, String charset) throws FieldValueDecodeEncodeException {
    try {
      final ByteArrayOutputStream bout = new ByteArrayOutputStream();
      for (FieldValue<?> v : value.getValues()) {
        v.write(bout, charset);
      }
      final byte[] buf = bout.toByteArray();
      return new String(buf, charset);
    } catch (Exception e) {
      throw new FieldValueDecodeEncodeException(e);
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("CompositeField[");
    if (values != null) {
      boolean first = true;
      for (FieldValue<?> v : values) {
        if (first)
          first = false;
        else
          sb.append(',');
        sb.append(v.toString());
      }
    }
    return sb.append(']').toString();
  }
}
