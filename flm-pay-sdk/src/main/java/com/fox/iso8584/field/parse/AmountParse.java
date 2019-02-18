package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.AmountValue; 
 
public class AmountParse extends FieldParse {
  
  private final static AmountParse INSTANCE = new AmountParse();

  private AmountParse(){}

  public static AmountParse getInstance(){
      return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos,
      CustomField<T> custom, String encoding) throws ParseException, UnsupportedEncodingException {

 
    String c = new String(buf, pos, 12, encoding);
    try {
       AmountValue<?> amountValue = new AmountValue<>(new BigDecimal(c).movePointLeft(2), null);
       return amountValue;
    } catch (NumberFormatException ex) {
        throw new ParseException(String.format("Cannot read amount '%s' pos %d",
                c,  pos), pos);
    } catch (IndexOutOfBoundsException ex) {
        throw new ParseException(String.format(
                "Insufficient data for AMOUNT, pos %d", pos), pos);
    }

    
  }
  



}
