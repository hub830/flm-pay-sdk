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
package com.fox.iso8584.field;

import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldTypeNotSupportException;
import com.fox.iso8584.field.value.AlphaValue;
import com.fox.iso8584.field.value.AmountValue;
import com.fox.iso8584.field.value.BinaryValue;
import com.fox.iso8584.field.value.Date10Value;
import com.fox.iso8584.field.value.Date4Value;
import com.fox.iso8584.field.value.DateExpValue;
import com.fox.iso8584.field.value.LllBin2VarValue;
import com.fox.iso8584.field.value.LllBinVarValue;
import com.fox.iso8584.field.value.LllvarValue;
import com.fox.iso8584.field.value.LlvarValue;
import com.fox.iso8584.field.value.NumericValue;
import com.fox.iso8584.field.value.TimeValue;



public class FieldFactory {

  private final String encoding;


  public FieldFactory(String encoding) {
    super();
    this.encoding = encoding; 
  }

  /**
   * 根据 field类型，返回对应的值 对象
   * 
   * @param type field类型
   * @param value 值
   * @return
   */
  public <T> FieldValue<T> getField(FieldType type, T value) {
    return getField(type, value, null, 0, false, false);
  }

  /**
   * 根据 field类型，返回对应的值 对象
   * 
   * @param type field类型
   * @param value 值
   * @param length 字段长度，变长字段 及 日期类型字段传0
   * @return
   */
  public <T> FieldValue<T> getField(FieldType type, T value, int length) {
    return getField(type, value, null, length, false, false);
  }

  /**
   * 根据 field类型，返回对应的值 对象
   * 
   * @param type field类型
   * @param value 值
   * @param encoder 自定义的编码解码器，可以为空
   * @param length 字段长度，变长字段 及 日期类型字段传0
   * @param binaryField 是否二进制字段 ，一般为否
   * @param forceStringEncoding 是否对文本模式下的可变长度字段的长度标头进行解码
   * @return
   */
  public <T> FieldValue<T> getField(FieldType type, T value, CustomField<T> encoder, int length,
      boolean binaryField, boolean forceStringEncoding) {
    switch (type) {
      case ALPHA:
        return new AlphaValue<T>(value, encoder, length, encoding);
      case AMOUNT:
        return new AmountValue<T>(value, encoder, encoding);
      case BINARY:
        return new BinaryValue<T>(value, encoder, length, encoding, binaryField);
      case DATE10:
        return new Date10Value<T>(value, encoder, encoding);
      case DATE12:
        break;
      case DATE14:
        break;
      case DATE4:
        return new Date4Value<T>(value, encoder, encoding);
      case DATE6:
        break;
      case DATE_EXP:
        return new DateExpValue<T>(value, encoder, encoding);
      case LLBCDBIN:
        break;
      case LLBIN:
        break;
      case LLLBCDBIN:
        break;
      case LLLBIN:
        return new LllBinVarValue<T>(value, encoder, encoding, binaryField);
      case LLLBIN2:
        return new LllBin2VarValue<T>(value, encoder, encoding, binaryField);
      case LLLLBCDBIN:
        break;
      case LLLLBIN:
        break;
      case LLLLVAR:
        break;
      case LLLVAR:
        return new LllvarValue<T>(value, encoder, encoding, binaryField);
      case LLVAR:
        return new LlvarValue<T>(value, encoder, encoding, binaryField);
      case NUMERIC:
        return new NumericValue<T>(value, encoder, length, encoding);
      case TIME:
        return new TimeValue<T>(value, encoder, encoding);
    }
    throw new FieldTypeNotSupportException(type);
  }
}
