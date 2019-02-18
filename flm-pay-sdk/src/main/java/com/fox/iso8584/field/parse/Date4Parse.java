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
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos,
      CustomField<T> custom, String encoding) throws ParseException, UnsupportedEncodingException {

 
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);


    cal.set(Calendar.MONTH, Integer.parseInt(new String(buf, pos, 2, encoding), 10)-1);
    cal.set(Calendar.DATE, Integer.parseInt(new String(buf, pos+2, 2, encoding), 10));
     

    Date4Value<?> date4Value = new Date4Value<>(cal.getTime(), null, encoding);
    return date4Value;
    
    
  }
  



}
