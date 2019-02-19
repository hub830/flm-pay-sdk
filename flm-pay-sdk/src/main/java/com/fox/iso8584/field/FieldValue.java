package com.fox.iso8584.field;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import com.fox.iso8584.CustomField;
import com.fox.iso8584.exception.FieldValueDecodeEncodeException;
import com.fox.iso8584.exception.FieldValueWriteException;

public interface FieldValue<T> {

  /**
   * 获取值的实际长度</br>
   * 主要用于变长字段，返回的长度会包含值前面长度的长度
   * 
   * @return
   * @throws FieldValueDecodeEncodeException
   */
  int getValueLength(String charset) throws FieldValueDecodeEncodeException;

  /**
   * 获得字段类型
   * 
   * @return
   */
  FieldType getType();

  /**
   * 获取字段的值
   * 
   * @return
   */
  T getValue();

  /**
   * 获取字段定义的长度 </br>
   * 对于无需长度的字段返回0
   * 
   * @return
   */
  int getLength();

  /**
   * 获取自定义的编码集
   * 
   * @return
   */
  CustomField<T> getEncoder();

  /**
   * 将field的值按格式输出到流
   * 
   * @param out
   * @throws IOException
   * @throws UnsupportedEncodingException
   */
  void write(OutputStream out, String charset) throws FieldValueWriteException;

  FieldValue<T> clone();

}
