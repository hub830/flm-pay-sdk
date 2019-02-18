

package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.DateExpValue; 
 
public class DateExpParse extends FieldParse {
  
  private final static DateExpParse INSTANCE = new DateExpParse();

  private DateExpParse(){}

  public static DateExpParse getInstance(){
      return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos,
      CustomField<T> custom, String encoding) throws ParseException, UnsupportedEncodingException {
   
 
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.DATE, 1);
    //Set the month in the date

    cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - (cal.get(Calendar.YEAR) % 100)
            + Integer.parseInt(new String(buf, pos, 2, encoding), 10));
    cal.set(Calendar.MONTH, Integer.parseInt(new String(buf, pos+2, 2, encoding), 10)-1); 

    DateExpValue<?> dateExpValue = new DateExpValue<>(cal.getTime(), null, encoding);
    
    return dateExpValue;
  }
  



}
