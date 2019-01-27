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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Map;
import org.apache.commons.codec.binary.Hex;

/**
 * Represents an ISO8583 message. This is the core class of the framework. Contains the bitmap which
 * is modified as fields are added/removed. This class makes no assumptions as to what types belong
 * in each field, nor what fields should each different message type have; that is left for the
 * developer, since the different ISO8583 implementations can vary greatly.
 * 
 * @author Enrique Zamudio
 */
public class FlmIsoMessage extends IsoMessage {

  public final static int REQUEST_TYPE_LENGTH = 4;

  public final static int HEAD_LENGTH = 46;

  /** Creates a new empty message with no values set. */
  public FlmIsoMessage() {}

  /** Creates a new message with the specified ISO header. This will be prepended to the message. */
  protected FlmIsoMessage(String header) {
    super(header);
  }

  /**
   * Creates a new message with the specified binary ISO header. This will be prepended to the
   * message.
   */
  protected FlmIsoMessage(byte[] binaryHeader) {
    super(binaryHeader);
  }

  /** This is where the values are stored. */
  @SuppressWarnings("rawtypes")
  private IsoValue[] headerFields = new IsoValue[10];

  /**
   * Returns the stored value in the field, without converting or formatting it.
   * 
   * @param field The field number. 1 is the secondary bitmap and is not returned as such; real
   *        fields go from 2 to 128.
   */
  public <T> T getHeaderObjectValue(int field) {
    @SuppressWarnings("unchecked")
    IsoValue<T> v = headerFields[field];
    return v == null ? null : v.getValue();
  }

  /** Returns the IsoValue for the specified field. First real field is 2. */
  @SuppressWarnings("unchecked")
  public <T> IsoValue<T> getHeaderField(int field) {
    return headerFields[field];
  }


  /**
   * Stored the field in the specified index. The first field is the secondary bitmap and has index
   * 1, so the first valid value for index must be 2.
   * 
   * @return The receiver (useful for setting several fields in sequence).
   */
  public IsoMessage setHeaderField(int index, IsoValue<?> field) {
    if (index < 2 || index > 128) {
      throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
    }
    if (field != null) {
      field.setCharacterEncoding(getCharacterEncoding());
    }
    headerFields[index] = field;
    return this;
  }

  /** Convenience method for setting several fields in one call. */
  public IsoMessage setHeaderFields(Map<Integer, IsoValue<?>> values) {
    for (Map.Entry<Integer, IsoValue<?>> e : values.entrySet()) {
      setHeaderField(e.getKey(), e.getValue());
    }
    return this;
  }


  /**
   * Sets the specified value in the specified field, creating an IsoValue internally.
   * 
   * @param index The field number (2 to 128)
   * @param value The value to be stored.
   * @param t The ISO type.
   * @param length The length of the field, used for ALPHA and NUMERIC values only, ignored with any
   *        other type.
   * @return The receiver (useful for setting several values in sequence).
   */
  public IsoMessage setHeaderValue(int index, Object value, IsoType t, int length) {
    return setHeaderValue(index, value, null, t, length);
  }

  /**
   * Sets the specified value in the specified field, creating an IsoValue internally.
   * 
   * @param index The field number (2 to 128)
   * @param value The value to be stored.
   * @param encoder An optional CustomField to encode/decode the value.
   * @param t The ISO type.
   * @param length The length of the field, used for ALPHA and NUMERIC values only, ignored with any
   *        other type.
   * @return The receiver (useful for setting several values in sequence).
   */
  public <T> IsoMessage setHeaderValue(int index, T value, CustomField<T> encoder, IsoType t,
      int length) {
    if (index < 0 || index > 9) {
      throw new IndexOutOfBoundsException("Field index must be between 0 and 9");
    }
    if (value == null) {
      headerFields[index] = null;
    } else {
      IsoValue<T> v = null;
      if (t.needsLength()) {
        v = new IsoValue<>(t, value, length, encoder);
      } else {
        v = new IsoValue<>(t, value, encoder);
      }
      v.setCharacterEncoding(getCharacterEncoding());
      headerFields[index] = v;
    }
    return this;
  }


  /**
   * Returns true is the message has a value in the specified field.
   * 
   * @param idx The field number.
   */
  public boolean hasHeaderField(int idx) {
    return headerFields[idx] != null;
  }

  /**
   * Returns true is the message contains all the specified fields. A convenience for m.hasField(x)
   * &amp;&amp; m.hasField(y) &amp;&amp; m.hasField(z) &amp;&amp; ...
   */
  public boolean hasHeaderEveryField(int... idx) {
    for (int i : idx) {
      if (!hasHeaderField(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true is the message contains at least one of the specified fields. A convenience for
   * m.hasField(x) || m.hasField(y) || m.hasField(z) || ...
   */
  public boolean hasHeaderAnyField(int... idx) {
    for (int i : idx) {
      if (hasHeaderField(i)) {
        return true;
      }
    }
    return false;
  }

  public ByteBuffer writeToBuffer(int lengthBytes) {

    if (lengthBytes > 4) {
      throw new IllegalArgumentException("The length header can have at most 4 bytes");
    }

    byte[] bodyData = writeData();
    int totalLength = bodyData.length + HEAD_LENGTH + REQUEST_TYPE_LENGTH;
    setHeaderValue(2, totalLength, IsoType.NUMERIC, 4);// 域3 报文总长度
    byte[] headerData = writeHeaderData();


    ByteBuffer buf = ByteBuffer.allocate(totalLength);

    if (lengthBytes > 0) {
      if (etx > -1) {
        totalLength++;
      }
      if (lengthBytes == 4) {
        buf.put((byte) ((totalLength & 0xff000000) >> 24));
      }
      if (lengthBytes > 2) {
        buf.put((byte) ((totalLength & 0xff0000) >> 16));
      }
      if (lengthBytes > 1) {
        buf.put((byte) ((totalLength & 0xff00) >> 8));
      }
      buf.put((byte) (totalLength & 0xff));
    }
    System.err.println("totalLength:"+new String(buf.array()));
    System.err.println("headerData:"+new String(headerData));
    System.err.println("bodyData:"+new String(bodyData));
    buf.put(headerData);
    buf.put(bodyData);

    buf.flip();
    return buf;

  }

  public void write(OutputStream outs, int lengthBytes) throws IOException {


    if (lengthBytes > 4) {
      throw new IllegalArgumentException("The length header can have at most 4 bytes");
    }
    byte[] bodyData = writeData();

    int totalLength = bodyData.length + HEAD_LENGTH + REQUEST_TYPE_LENGTH;
    setHeaderValue(2, totalLength, IsoType.NUMERIC, 4);// 域3 报文总长度
    byte[] headerData = writeHeaderData();

    if (lengthBytes > 0) {
      if (etx > -1) {
        totalLength++;
      }
      byte[] buf = new byte[lengthBytes];
      int pos = 0;
      if (lengthBytes == 4) {
        buf[0] = (byte) ((totalLength & 0xff000000) >> 24);
        pos++;
      }
      if (lengthBytes > 2) {
        buf[pos] = (byte) ((totalLength & 0xff0000) >> 16);
        pos++;
      }
      if (lengthBytes > 1) {
        buf[pos] = (byte) ((totalLength & 0xff00) >> 8);
        pos++;
      }
      buf[pos] = (byte) (totalLength & 0xff);
      outs.write(buf);
    }


    outs.write(headerData);
    outs.write(bodyData);
    // ETX
    if (etx > -1) {
      outs.write(etx);
    }
    outs.flush();
  }


  /** Writes the message to a memory stream and returns a byte array with the result. */
  private byte[] writeHeaderData() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    // Fields
    for (int i = 0; i < 10; i++) {
      IsoValue<?> v = headerFields[i];
      if (v != null) {
        try {
          v.write(bout, binaryFields, false);
        } catch (IOException ex) {
          // should never happen, writing to a ByteArrayOutputStream
        }
      }
    }
    return bout.toByteArray();
  }

  /** Writes the message to a memory stream and returns a byte array with the result. */
  public byte[] writeData() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    // Message Type
    try {
      bout.write(String.format("%04x", type).getBytes(encoding));
    } catch (IOException ex) {
      // should never happen, writing to a ByteArrayOutputStream
    }

    // Bitmap
    BitSet bs = createBitmapBitSet();

    ByteArrayOutputStream bout2 = null;
    if (forceStringEncoding) {
      bout2 = bout;
      bout = new ByteArrayOutputStream();
    }
    int pos = 0;
    int lim = bs.size() / 4;
    for (int i = 0; i < lim; i++) {
      int nibble = 0;
      if (bs.get(pos++))
        nibble |= 8;
      if (bs.get(pos++))
        nibble |= 4;
      if (bs.get(pos++))
        nibble |= 2;
      if (bs.get(pos++))
        nibble |= 1;
      bout.write(HEX[nibble]);
    }
    if (forceStringEncoding) {
      final String _hb = new String(bout.toByteArray());
      bout = bout2;
      try {
        bout.write(_hb.getBytes(encoding));
      } catch (IOException ignore) {
        // never happen
      }
    }

    // Fields
    for (int i = 2; i < 129; i++) {
      IsoValue<?> v = fields[i];
      if (v != null) {
        try {
          v.write(bout, binaryFields, forceStringEncoding);
        } catch (IOException ex) {
          // should never happen, writing to a ByteArrayOutputStream
        }
      }
    }

    return bout.toByteArray();
  }

}
