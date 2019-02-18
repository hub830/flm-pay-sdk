package com.fox.iso8584.field.value;

import java.io.UnsupportedEncodingException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.field.AbstractFieldValue;
import com.fox.iso8584.field.FieldType;

/**
 * 固定长度的字母或数字值。右边填补空格。
 * 
 * @author mux
 *
 */
public class AlphaValue<T> extends AbstractFieldValue<T> {


  public AlphaValue(T value, CustomField<T> encoder, int length, String encoding) {
    super(FieldType.ALPHA, value, encoder, length, encoding);
  }

  @Override
  protected byte[] format() throws UnsupportedEncodingException {
    String v;
    if (value == null) {
      v = "";
    } else {
      v = value.toString();
    }
    if (v.length() > length) {
      v = v.substring(0, length);
    } else if (v.length() != length) {
      // 如果值包含中文的话，格式化出来的值 长度可能 不正确，需要做特殊处理
      v = String.format(String.format("%%-%ds", length), v);
      v = chineseSubString(v, length, encoding);
    }
    return v.getBytes(encoding);
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
  private static String chineseSubString(String s, int num, String charset) {
    try {
      int changdu = s.getBytes(charset).length;
      if (changdu > num) {
        s = s.substring(0, s.length() - 1);
        s = chineseSubString(s, num, charset);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return s;
  }
}
