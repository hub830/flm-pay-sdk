/*
j8583 A Java implementation of the ISO8583 protocol
Copyright (C) 2011 Enrique Zamudio Lopez

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/
package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.Date4Value; 
 
public class Date4Parse extends FieldParse {
  
  private final static Date4Parse INSTANCE = new Date4Parse();

  private Date4Parse(){}

  public static Date4Parse getInstance(){
      return INSTANCE;
  }

  @Override
  public <T> FieldValue<T> parse(FieldParseInfo fpi, byte[] buf, int pos,
      CustomField<T> custom, String encoding) throws ParseException, UnsupportedEncodingException {

 
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);


    cal.set(Calendar.MONTH, Integer.parseInt(new String(buf, pos, 2, encoding), 10)-1);
    cal.set(Calendar.DATE, Integer.parseInt(new String(buf, pos+2, 2, encoding), 10));
     

    Date4Value date4Value = new Date4Value<>(cal.getTime(), null, encoding);
    return date4Value;
    
    
  }
  



}
