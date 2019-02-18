package com.fox.iso8584.field.parse;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Calendar;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.FieldParse;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.Date10Value;

public class Date10Parse extends FieldParse {

  private final static Date10Parse INSTANCE = new Date10Parse();

  private Date10Parse() {}

  public static Date10Parse getInstance() {
    return INSTANCE;
  }

  @Override
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws ParseException, UnsupportedEncodingException {

    // A SimpleDateFormat in the case of dates won't help because of the missing data
    // we have to use the current date for reference and change what comes in the buffer
    Calendar cal = Calendar.getInstance();
    // Set the month in the date

    cal.set(Calendar.MONTH, Integer.parseInt(new String(buf, pos, 2, encoding), 10)-1);
    cal.set(Calendar.DATE, Integer.parseInt(new String(buf, pos+2, 2, encoding), 10));
    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(new String(buf, pos+4, 2, encoding), 10));
    cal.set(Calendar.MINUTE, Integer.parseInt(new String(buf, pos+6, 2, encoding), 10));
    cal.set(Calendar.SECOND, Integer.parseInt(new String(buf, pos+8, 2, encoding), 10));
    cal.set(Calendar.MILLISECOND, 0);
    Date10Value<?> date10Value = new Date10Value<>(cal.getTime(), null);

    return date10Value;

  }



}
