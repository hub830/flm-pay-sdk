/*
 * j8583 A Java implementation of the ISO8583 protocol Copyright (C) 2007 Enrique Zamudio Lopez
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
package com.fox.iso8584;


import java.io.ByteArrayOutputStream;
import com.fox.iso8584.exception.FieldValueWriteException;
import com.fox.iso8584.field.FieldValue;
import com.fox.iso8584.field.value.AlphaValue;
import com.fox.iso8584.field.value.BinaryValue;
import com.fox.iso8584.field.value.NumericValue;


public class IsoHeader {
  public final static int HEAD_LENGTH = 46;

  /** This is where the values are stored. */
  @SuppressWarnings("rawtypes")
  protected FieldValue[] fields = new FieldValue[10];
  
  /** Creates a new empty message with no values set. */
  public IsoHeader(String sourceStationId, String destinationId) {
    byte[] headerLength = {HEAD_LENGTH};
    fields[0] = new BinaryValue<byte[]>(headerLength, null, 1);// 域1 该域存放报文头的字节数 该域的值必须为46
    fields[1] = new BinaryValue<Integer>(1, null, 1);// 域2 头标识和版本号 0表示该报文是一个生产报文
    // fields[2] = new IsoValue<Integer>(IsoType.NUMERIC, 2, 4);// 域3 报文总长度 需要在输出数据时才能计算出来
    fields[3] = new AlphaValue<String>(destinationId, null, 11);// 域4 目的ID
    fields[4] = new AlphaValue<String>(sourceStationId, null, 11);// 域5 源ID
    fields[5] = new NumericValue<Integer>(0, null, 3);// 域6 保留使用
    fields[6] = new NumericValue<Integer>(0, null, 1);// 域7 批次号
    fields[7] = new NumericValue<Integer>(0, null, 8);// 域8 交易信息
    fields[8] = new AlphaValue<Integer>(0, null, 1);// 域9 用户信息
    fields[9] = new NumericValue<Integer>(0, null, 5);// 域10 拒绝码
  }

  /**
   * 设置报文总的长度
   * 
   * @param length
   */
  public void setTotalLengtn(int length) {
    fields[2] = new NumericValue<Integer>(length, null, 4);// 域3 报文总长度 需要在输出数据时才能计算出来
  }

  public byte[] writeData(String charset) throws FieldValueWriteException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    // Fields
    for (int i = 0; i < 10; i++) {
      FieldValue<?> v = fields[i];
      if (v != null) {
          v.write(bout, charset);
      }
    }
    return bout.toByteArray();
  }

}
