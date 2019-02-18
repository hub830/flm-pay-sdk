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
  public <T> FieldValue<?> parse(FieldParseInfo fpi, byte[] buf, int pos, CustomField<T> custom,
      String encoding) throws ParseException, UnsupportedEncodingException {

    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.HOUR_OF_DAY, ((buf[pos] - 48) * 10) + buf[pos + 1] - 48);
    cal.set(Calendar.MINUTE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
    cal.set(Calendar.SECOND, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
    TimeValue<?> timeValue = new TimeValue<>(cal.getTime(), null);
    return timeValue;
  }
}
