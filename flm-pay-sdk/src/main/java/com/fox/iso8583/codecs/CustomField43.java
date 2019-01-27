package com.fox.iso8583.codecs;

import java.io.UnsupportedEncodingException;
import com.fox.iso8583.CustomField;

/**
 * This is an example of a custom field codec, which converts between strings and instances of this
 * same class. It's used to test the encoding and decoding of custom fields by the message factory.
 * 
 * @author Enrique Zamudio
 */
public class CustomField43 implements CustomField<CustomField43> {

  private String value;

  private String encoding;


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  @Override
  public CustomField43 decodeField(String value) {
    CustomField43 cf = null;
    try {
      if (value != null) {
        cf = new CustomField43();
          
        value = new String(value.getBytes(encoding), 0, 40, encoding);
        cf.setValue(value);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return cf;
  }

  @Override
  public String encodeField(CustomField43 field) {
    // 如果值包含中文的话，格式化出来的值 长度可能 不正确，需要做特殊处理
    String value = String.format(String.format("%%-%ds", 40), field.getValue());
    return chineseSubString(value, 40, encoding);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof CustomField43)) {
      return false;
    }
    CustomField43 other = (CustomField43) obj;
    if (other.getValue() == null) {
      return value == null;
    } else {
      return other.getValue().equals(value);
    }
  }

  @Override
  public int hashCode() {
    return (value == null ? 0 : value.hashCode());
  }

  /**
   * 判断传进来的字符串，是否 大于指定的字节，如果大于递归调用 直到小于指定字节数 ，一定要指定字符编码，因为各个系统字符编码都不一样，字节数也不一样
   * 
   * @param s 原始字符串
   * @param num 传进来指定字节数
   * @return String 截取后的字符串
   * 
   * @throws UnsupportedEncodingException
   */
  private static String chineseSubString(String s, int num, String encoding) {
    try {
//      byte[] bytes = s.getBytes(encoding);
      int changdu = s.getBytes(encoding).length;
      if (changdu > num) {
        s = s.substring(0, s.length() - 1);
        s = chineseSubString(s, num, encoding);
      }
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return s;
  }
}
