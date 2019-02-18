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
import org.apache.commons.codec.binary.Hex;

public class IsoMessage {

  public final static int REQUEST_TYPE_LENGTH = 4;
  
  /** The message type. */
  protected int type;

  private IsoHeader isoHeader;

  private IsoBody isoBody;

  protected String encoding = System.getProperty("file.encoding");

  public IsoMessage(int type) {
    super();
    this.type = type;
  }

  public IsoMessage(int type, IsoHeader isoHeader, IsoBody isoBody) {
    super();
    this.type = type;
    this.isoHeader = isoHeader;
    this.isoBody = isoBody;
  }

  public IsoHeader getIsoHeader() {
    return isoHeader;
  }

  public void setIsoHeader(IsoHeader isoHeader) {
    this.isoHeader = isoHeader;
  }

  public IsoBody getIsoBody() {
    return isoBody;
  }

  public void setIsoBody(IsoBody isoBody) {
    this.isoBody = isoBody;
  }

  /** Sets the ISO message type. Common values are 0x200, 0x210, 0x400, 0x410, 0x800, 0x810. */
  public void setType(int value) {
    type = value;
  }

  /** Returns the ISO message type. */
  public int getType() {
    return type;
  }

  /**
   * 返回报文</br>
   * 报文格式： 报文长度 + 报文头 + 报文类型 + 位图 + 报文体
   * 
   * @throws IOException
   */
  public byte[] writeData(String charset) throws IOException {

    byte[] bodyData = isoBody.writeData(charset);

    int totalLength = bodyData.length + IsoHeader.HEAD_LENGTH + REQUEST_TYPE_LENGTH;
    // 设置报文总长度
    isoHeader.setTotalLengtn(totalLength);
    /*
     * 开始组装报文
     */
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    // 输出 报文长度
    // bout.write(totalLength);
    String mesageLength = String.format("%04d", totalLength);
    bout.write(
        Hex.encodeHexString(mesageLength.getBytes(encoding)).toUpperCase().getBytes(encoding));
    // 输出 报文头
    bout.write(Hex.encodeHexString(isoHeader.writeData(charset)).toUpperCase().getBytes());
    // 输出 报文类型
    String messageType = String.format("%04x", type);
    bout.write(
        Hex.encodeHexString(messageType.getBytes(encoding)).toUpperCase().getBytes(encoding));
    // 位图 + 报文体
    bout.write(Hex.encodeHexString(bodyData).toUpperCase().getBytes());
    return bout.toByteArray();
  }
}
