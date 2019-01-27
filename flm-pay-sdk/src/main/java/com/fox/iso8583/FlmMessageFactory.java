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
package com.fox.iso8583;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import com.fox.iso8583.parse.FieldParseInfo;
import com.fox.iso8583.parse.FlmConfigParser;

public class FlmMessageFactory<T extends FlmIsoMessage> extends MessageFactory<T> {


  protected T createIsoMessageWithBinaryHeader(byte[] binHeader) {
    return (T) new FlmIsoMessage(binHeader);
  }

  protected T createIsoMessage(String header) {
    return (T) new FlmIsoMessage(header);
  }


  /**
   * Tells the receiver to read the configuration at the specified path. This just calls
   * ConfigParser.configureFromClasspathConfig() with itself and the specified path at arguments,
   * but is really convenient in case the MessageFactory is being configured from within, say,
   * Spring.
   */
  public void setConfigPath(String path) throws IOException {
    FlmConfigParser.configureFromClasspathConfig(this, path);
    // Now re-set some properties that need to be propagated down to the recently assigned objects
    setCharacterEncoding(getCharacterEncoding());
    setForceStringEncoding(isForceStringEncoding());
  }

  public T newMessage(int type) {
    T m = super.newMessage(type);
    m.setHeaderValue(0, 46, IsoType.BINARY, 1);// 域1 该域存放报文头的字节数 该域的值必须为46
    m.setHeaderValue(1, 1, IsoType.BINARY, 1);// 域2 头标识和版本号 0表示该报文是一个生产报文
//    m.setHeaderValue(2, 0, IsoType.NUMERIC, 4);// 域3 报文总长度
    m.setHeaderValue(3, 62100000, IsoType.ALPHA, 11);// 域4 目的ID
    m.setHeaderValue(4, 48501111, IsoType.ALPHA, 11);// 域5 源ID
    m.setHeaderValue(5, 0, IsoType.ALPHA, 3);// 域6 保留使用
    m.setHeaderValue(6, 0, IsoType.ALPHA, 1);// 域7 批次号
    m.setHeaderValue(7, 0, IsoType.ALPHA, 8);// 域8 交易信息
    m.setHeaderValue(8, 0, IsoType.ALPHA, 1);// 域9 用户信息
    m.setHeaderValue(9, 0, IsoType.NUMERIC, 5);// 域10 拒绝码
    return m;
  }

  public T parseMessage(byte[] buf, int isoHeaderLength, boolean binaryIsoHeader)
          throws ParseException, UnsupportedEncodingException {
      final int minlength = isoHeaderLength+(binaryHeader?2:4)+(binBitmap||binaryHeader ? 8:16);
      if (buf.length < minlength) {
          throw new ParseException("Insufficient buffer length, needs to be at least " + minlength, 0);
      }
      final T m;
      if (binaryIsoHeader && isoHeaderLength > 0) {
          byte[] _bih = new byte[isoHeaderLength];
          System.arraycopy(buf, 0, _bih, 0, isoHeaderLength);
          m = createIsoMessageWithBinaryHeader(_bih);
      } else {
          m = createIsoMessage(isoHeaderLength > 0 ?
                  new String(buf, 0, isoHeaderLength, encoding) : null);
      }
      m.setCharacterEncoding(encoding);
      final int type;
      if (binaryHeader) {
          type = ((buf[isoHeaderLength] & 0xff) << 8) | (buf[isoHeaderLength + 1] & 0xff);
      } else if (forceStringEncoding) {
          type = Integer.parseInt(new String(buf, isoHeaderLength, 4, encoding), 16);
      } else {
          type = ((buf[isoHeaderLength] - 48) << 12)
                  | ((buf[isoHeaderLength + 1] - 48) << 8)
                  | ((buf[isoHeaderLength + 2] - 48) << 4)
                  | (buf[isoHeaderLength + 3] - 48);
      }
      m.setType(type);
      //Parse the bitmap (primary first)
      final BitSet bs = new BitSet(64);
      int pos = 0;
      if (binaryHeader || binBitmap) {
          final int bitmapStart = isoHeaderLength + (binaryHeader ? 2 : 4);
          for (int i = bitmapStart; i < 8+bitmapStart; i++) {
              int bit = 128;
              for (int b = 0; b < 8; b++) {
                  bs.set(pos++, (buf[i] & bit) != 0);
                  bit >>= 1;
              }
          }
          //Check for secondary bitmap and parse if necessary
          if (bs.get(0)) {
              if (buf.length < minlength + 8) {
                  throw new ParseException("Insufficient length for secondary bitmap", minlength);
              }
              for (int i = 8+bitmapStart; i < 16+bitmapStart; i++) {
                  int bit = 128;
                  for (int b = 0; b < 8; b++) {
                      bs.set(pos++, (buf[i] & bit) != 0);
                      bit >>= 1;
                  }
              }
              pos = minlength + 8;
          } else {
              pos = minlength;
          }
      } else {
          //ASCII parsing
          try {
              final byte[] bitmapBuffer;
              if (forceStringEncoding) {
                  byte[] _bb = new String(buf, isoHeaderLength+4, 16, encoding).getBytes();
                  bitmapBuffer = new byte[36+isoHeaderLength];
                  System.arraycopy(_bb, 0, bitmapBuffer, 4+isoHeaderLength, 16);
              } else {
                  bitmapBuffer = buf;
              }
              for (int i = isoHeaderLength + 4; i < isoHeaderLength + 20; i++) {
                  if (bitmapBuffer[i] >= '0' && bitmapBuffer[i] <= '9') {
                      bs.set(pos++, ((bitmapBuffer[i] - 48) & 8) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 48) & 4) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 48) & 2) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 48) & 1) > 0);
                  } else if (bitmapBuffer[i] >= 'A' && bitmapBuffer[i] <= 'F') {
                      bs.set(pos++, ((bitmapBuffer[i] - 55) & 8) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 55) & 4) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 55) & 2) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 55) & 1) > 0);
                  } else if (bitmapBuffer[i] >= 'a' && bitmapBuffer[i] <= 'f') {
                      bs.set(pos++, ((bitmapBuffer[i] - 87) & 8) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 87) & 4) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 87) & 2) > 0);
                      bs.set(pos++, ((bitmapBuffer[i] - 87) & 1) > 0);
                  }
              }
              //Check for secondary bitmap and parse it if necessary
              if (bs.get(0)) {
                  if (buf.length < minlength + 16) {
                      throw new ParseException("Insufficient length for secondary bitmap", minlength);
                  }
                  if (forceStringEncoding) {
                      byte[] _bb = new String(buf, isoHeaderLength+20, 16, encoding).getBytes();
                      System.arraycopy(_bb, 0, bitmapBuffer, 20+isoHeaderLength, 16);
                  }
                  for (int i = isoHeaderLength + 20; i < isoHeaderLength + 36; i++) {
                      if (bitmapBuffer[i] >= '0' && bitmapBuffer[i] <= '9') {
                          bs.set(pos++, ((bitmapBuffer[i] - 48) & 8) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 48) & 4) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 48) & 2) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 48) & 1) > 0);
                      } else if (bitmapBuffer[i] >= 'A' && bitmapBuffer[i] <= 'F') {
                          bs.set(pos++, ((bitmapBuffer[i] - 55) & 8) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 55) & 4) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 55) & 2) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 55) & 1) > 0);
                      } else if (bitmapBuffer[i] >= 'a' && bitmapBuffer[i] <= 'f') {
                          bs.set(pos++, ((bitmapBuffer[i] - 87) & 8) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 87) & 4) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 87) & 2) > 0);
                          bs.set(pos++, ((bitmapBuffer[i] - 87) & 1) > 0);
                      }
                  }
                  pos = 16 + minlength;
              } else {
                  pos = minlength;
              }
          } catch (NumberFormatException ex) {
              ParseException _e = new ParseException("Invalid ISO8583 bitmap", pos);
              _e.initCause(ex);
              throw _e;
          }
      }
      //Parse each field
      Map<Integer, FieldParseInfo> parseGuide = parseMap.get(type);
      List<Integer> index = parseOrder.get(type);
      if (index == null) {
          log.error(String.format("ISO8583 MessageFactory has no parsing guide for message type %04x [%s]",
              type, new String(buf)));
          throw new ParseException(String.format(
                  "ISO8583 MessageFactory has no parsing guide for message type %04x [%s]",
                  type,
                  new String(buf)), 0);
      }
      //First we check if the message contains fields not specified in the parsing template
      boolean abandon = false;
      for (int i = 1; i < bs.length(); i++) {
          if (bs.get(i) && !index.contains(i+1)) {
              log.warn("ISO8583 MessageFactory cannot parse field {}: unspecified in parsing guide for type {}",
                      i+1, Integer.toString(type, 16));
              abandon = true;
          }
      }
      if (abandon) {
          throw new ParseException("ISO8583 MessageFactory cannot parse fields", 0);
      }
      //Now we parse each field
      if (binaryFields) {
          for (Integer i : index) {
              FieldParseInfo fpi = parseGuide.get(i);
              if (bs.get(i - 1)) {
                  if (ignoreLast && pos >= buf.length && i.intValue() == index.get(index.size() -1)) {
                      log.warn("Field {} is not really in the message even though it's in the bitmap", i);
                      bs.clear(i - 1);
                  } else {
                      CustomField<?> decoder = fpi.getDecoder();
                      if (decoder == null) {
                          decoder = getCustomField(i);
                      }
                      IsoValue<?> val = fpi.parseBinary(i, buf, pos, decoder);
                      m.setField(i, val);
                      if (val != null) {
                          if (val.getType() == IsoType.NUMERIC || val.getType() == IsoType.DATE10
                                  || val.getType() == IsoType.DATE4
                                  || val.getType() == IsoType.DATE12
                                  || val.getType() == IsoType.DATE14
                                  || val.getType() == IsoType.DATE_EXP
                                  || val.getType() == IsoType.AMOUNT
                                  || val.getType() == IsoType.TIME) {
                              pos += (val.getLength() / 2) + (val.getLength() % 2);
                          } else {
                              pos += val.getLength();
                          }
                          if (val.getType() == IsoType.LLVAR || val.getType() == IsoType.LLBIN || val.getType() == IsoType.LLBCDBIN ) {
                              pos++;
                          } else if (val.getType() == IsoType.LLLVAR
                                  || val.getType() == IsoType.LLLBIN
                                  || val.getType() == IsoType.LLLBCDBIN
                                  || val.getType() == IsoType.LLLLVAR
                                  || val.getType() == IsoType.LLLLBIN
                                  || val.getType() == IsoType.LLLLBCDBIN) {
                              pos += 2;
                          }
                      }
                  }
              }
          }
      } else {
          for (Integer i : index) {
              FieldParseInfo fpi = parseGuide.get(i);
              if (bs.get(i - 1)) {
                  if (ignoreLast && pos >= buf.length && i.intValue() == index.get(index.size() -1)) {
                      log.warn("Field {} is not really in the message even though it's in the bitmap", i);
                      bs.clear(i - 1);
                  } else {
                      CustomField<?> decoder = fpi.getDecoder();
                      if (decoder == null) {
                          decoder = getCustomField(i);
                      }
                      IsoValue<?> val = fpi.parse(i, buf, pos, decoder);
                      m.setField(i, val);
                      //To get the correct next position, we need to get the number of bytes, not chars
                      pos += val.toString().getBytes(fpi.getCharacterEncoding()).length;
                      if (val.getType() == IsoType.LLVAR || val.getType() == IsoType.LLBIN || val.getType() == IsoType.LLBCDBIN) {
                          pos += 2;
                      } else if (val.getType() == IsoType.LLLVAR || val.getType() == IsoType.LLLBIN || val.getType() == IsoType.LLLBCDBIN) {
                          pos += 3;
                      } else if (val.getType() == IsoType.LLLLVAR || val.getType() == IsoType.LLLLBIN || val.getType() == IsoType.LLLLBCDBIN) {
                          pos += 4;
                      }
                  }
              }
          }
      }
      m.setBinaryHeader(binaryHeader);
      m.setBinaryFields(binaryFields);
      m.setBinaryBitmap(binBitmap);
      return m;
  }
}
