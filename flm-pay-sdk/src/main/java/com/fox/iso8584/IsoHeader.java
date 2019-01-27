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
import java.io.IOException;


public class IsoHeader {

  /** This is where the values are stored. */
  @SuppressWarnings("rawtypes")
  protected IsoValue[] fields = new IsoValue[10];
  protected boolean forceStringEncoding;
  protected String encoding = System.getProperty("file.encoding");


  /** Creates a new empty message with no values set. */
  public IsoHeader(String sourceStationId, String destinationId, String encoding) {
    this.encoding = encoding;
    byte[] headerLength = {46};
    fields[0] = new IsoValue<byte[]>(IsoType.BINARY,headerLength, 1) ;// 域1 该域存放报文头的字节数 该域的值必须为46
    fields[0].setBinaryField(true);
    fields[1] = new IsoValue<Integer>(IsoType.BINARY, 1, 1);// 域2 头标识和版本号 0表示该报文是一个生产报文
    fields[1].setBinaryField(true);
    // fields[2] = new IsoValue<Integer>(IsoType.NUMERIC, 2, 4);// 域3 报文总长度 需要在输出数据时才能计算出来
    fields[3] = new IsoValue<String>(IsoType.ALPHA, destinationId, 11);// 域4 目的ID
    fields[4] = new IsoValue<String>(IsoType.ALPHA, sourceStationId, 11);// 域5 源ID
    fields[5] = new IsoValue<Integer>(IsoType.NUMERIC, 0, 3);// 域6 保留使用
    fields[6] = new IsoValue<Integer>(IsoType.NUMERIC, 0, 1);// 域7 批次号
    fields[7] = new IsoValue<Integer>(IsoType.NUMERIC, 0, 8);// 域8 交易信息
    fields[8] = new IsoValue<Integer>(IsoType.ALPHA, 0, 1);// 域9 用户信息
    fields[9] = new IsoValue<Integer>(IsoType.NUMERIC, 0, 5);// 域10 拒绝码
    for (IsoValue<?> v : fields) {
      if (v != null) {
        v.setCharacterEncoding(encoding);
      }
    }
  }

  /**
   * 设置报文总的长度
   * 
   * @param length
   */
  public void setTotalLengtn(int length) {
    fields[2] = new IsoValue<Integer>(IsoType.NUMERIC, length, 4);// 域3 报文总长度 需要在输出数据时才能计算出来
  }

  public byte[] writeData() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    // Fields
    for (int i = 0; i < 10; i++) {
      IsoValue<?> v = fields[i];
      if (v != null) {
        try {
          v.write(bout, v.isBinaryField(), forceStringEncoding);
        } catch (IOException ex) {
          // should never happen, writing to a ByteArrayOutputStream
        }
      }
    }
    return bout.toByteArray();
  }

}
