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

import java.util.List;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.codecs.CompositeField;
import com.fox.iso8584.exception.FieldTypeNotSupportException;
import com.fox.iso8584.exception.FieldValueParseException;
import com.fox.iso8584.field.parse.AlphaParse;
import com.fox.iso8584.field.parse.AmountParse;
import com.fox.iso8584.field.parse.BinaryParse;
import com.fox.iso8584.field.parse.Date10Parse;
import com.fox.iso8584.field.parse.Date4Parse;
import com.fox.iso8584.field.parse.DateExpParse;
import com.fox.iso8584.field.parse.LllbinParse;
import com.fox.iso8584.field.parse.LllvarParse;
import com.fox.iso8584.field.parse.LlvarParse;
import com.fox.iso8584.field.parse.NumericParse;
import com.fox.iso8584.field.parse.TimeParse;



public class FieldParseFactory {


  public static   FieldValue<?>  parse(//
      FieldParseInfo fpi, //
      byte[] buf, //
      int pos, //
      CustomField<?>  custom, //
      String encoding//
  ) throws FieldValueParseException {

    FieldParse fieldParse = getField(fpi.getType());
    FieldValue<?>  field;
    List<FieldParseInfo> subFieldParse = fpi.getSubFieldParse();
    if (subFieldParse != null && subFieldParse.size() > 0) {
      final CompositeField cf = new CompositeField();
      for (FieldParseInfo sfpi : subFieldParse) {
        cf.addParser(sfpi);
      }
      field =   fieldParse.parse(fpi, buf, pos, cf, encoding);
    } else {
      field = fieldParse.parse(fpi, buf, pos, custom, encoding);
    }
    return field;
  }

  private static FieldParse getField(FieldType type) {
    FieldParse fieldParse = null;
    switch (type) {
      case ALPHA:
        fieldParse = AlphaParse.getInstance();
        break;
      case AMOUNT:
        fieldParse = AmountParse.getInstance();
        break;
      case BINARY:
        fieldParse = BinaryParse.getInstance();
        break;
      case DATE10:
        fieldParse = Date10Parse.getInstance();
        break;
      case DATE12:
        break;
      case DATE14:
        break;
      case DATE4:
        fieldParse = Date4Parse.getInstance();
        break;
      case DATE6:
        break;
      case DATE_EXP:
        fieldParse = DateExpParse.getInstance();
        break;
      case LLBCDBIN:
        break;
      case LLBIN:
        break;
      case LLLBCDBIN:
        break;
      case LLLBIN:
        fieldParse = LllbinParse.getInstance();
        break;
      case LLLLBCDBIN:
        break;
      case LLLLBIN:
        break;
      case LLLLVAR:
        break;
      case LLLVAR:
        fieldParse = LllvarParse.getInstance();
        break;
      case LLVAR:
        fieldParse = LlvarParse.getInstance();
        break;
      case NUMERIC:
        fieldParse = NumericParse.getInstance();
        break;
      case TIME:
        fieldParse = TimeParse.getInstance();
        break;
      default:
        break;
    }
    if (fieldParse != null) {
      return fieldParse;
    }
    throw new FieldTypeNotSupportException(type);
  }
}
