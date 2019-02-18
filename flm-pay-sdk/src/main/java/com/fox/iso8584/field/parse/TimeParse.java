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
import java.util.Calendar;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.TimeValue;

public class TimeParse extends FieldParse {

  private final static TimeParse INSTANCE = new TimeParse();

  private TimeParse() {}

  public static TimeParse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<T> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws ParseException, UnsupportedEncodingException {


    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.HOUR_OF_DAY, ((buf[pos] - 48) * 10) + buf[pos + 1] - 48);
    cal.set(Calendar.MINUTE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
    cal.set(Calendar.SECOND, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
    TimeValue timeValue = new TimeValue<>(cal.getTime(), null, encoding);
    return timeValue;


 
  }



}
