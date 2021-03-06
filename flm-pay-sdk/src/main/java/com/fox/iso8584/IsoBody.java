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
public class IsoBody {

  static final byte[] HEX =
      new byte[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  /** This is where the values are stored. */
  @SuppressWarnings("rawtypes")
  protected IsoValue[] fields = new IsoValue[129];
  protected int etx = -1;
  /** Flag to enforce secondary bitmap even if empty. */
  private boolean forceb2;
  protected boolean forceStringEncoding;
  protected String encoding = System.getProperty("file.encoding");

  /** Creates a new empty message with no values set. */
  public IsoBody() {

  }

  /** If set, this flag will cause the secondary bitmap to be written even if it's not needed. */
  public void setForceSecondaryBitmap(boolean flag) {
    forceb2 = flag;
  }

  /**
   * Returns true if the secondary bitmap is always included in the message, even if it's not
   * needed. Default is false.
   */
  public boolean getForceSecondaryBitmap() {
    return forceb2;
  }

  /** Sets the encoding to use. */
  public void setCharacterEncoding(String value) {
    if (value == null) {
      throw new IllegalArgumentException("Cannot set null encoding.");
    }
    encoding = value;
  }

  /**
   * Returns the character encoding for Strings inside the message. Default is taken from the
   * file.encoding system property.
   */
  public String getCharacterEncoding() {
    return encoding;
  }

  /**
   * Specified whether the variable-length fields should encode their length headers using string
   * conversion with the proper character encoding. Default is false, which is the old behavior
   * (encoding as ASCII). This is only useful for text format.
   */
  public void setForceStringEncoding(boolean flag) {
    forceStringEncoding = flag;
  }

  /**
   * Sets the ETX character, which is sent at the end of the message as a terminator. Default is -1,
   * which means no terminator is sent.
   */
  public void setEtx(int value) {
    etx = value;
  }

  /**
   * Returns the stored value in the field, without converting or formatting it.
   * 
   * @param field The field number. 1 is the secondary bitmap and is not returned as such; real
   *        fields go from 2 to 128.
   */
  public <T> T getObjectValue(int field) {
    @SuppressWarnings("unchecked")
    IsoValue<T> v = fields[field];
    return v == null ? null : v.getValue();
  }

  /** Returns the IsoValue for the specified field. First real field is 2. */
  @SuppressWarnings("unchecked")
  public <T> IsoValue<T> getField(int field) {
    return fields[field];
  }

  /**
   * Stored the field in the specified index. The first field is the secondary bitmap and has index
   * 1, so the first valid value for index must be 2.
   * 
   * @return The receiver (useful for setting several fields in sequence).
   */
  public IsoBody setField(int index, IsoValue<?> field) {
    if (index < 2 || index > 128) {
      throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
    }
    if (field != null) {
      field.setCharacterEncoding(encoding);
    }
    fields[index] = field;
    return this;
  }

  /** Convenience method for setting several fields in one call. */
  public IsoBody setFields(Map<Integer, IsoValue<?>> values) {
    for (Map.Entry<Integer, IsoValue<?>> e : values.entrySet()) {
      setField(e.getKey(), e.getValue());
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
  public IsoBody setValue(int index, Object value, IsoType t, int length) {
    return setValue(index, value, null, t, length);
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
  public <T> IsoBody setValue(int index, T value, CustomField<T> encoder, IsoType t, int length) {
    if (index < 2 || index > 128) {
      throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
    }
    if (value == null) {
      fields[index] = null;
    } else {
      IsoValue<T> v = null;
      if (t.needsLength()) {
        v = new IsoValue<>(t, value, length, encoder);
      } else {
        v = new IsoValue<>(t, value, encoder);
      }
      v.setCharacterEncoding(encoding);
      fields[index] = v;
    }
    return this;
  }

  /**
   * A convenience method to set new values in fields that already contain values. The field's type,
   * length and custom encoder are taken from the current value. This method can only be used with
   * fields that have been previously set, usually from a template in the MessageFactory.
   * 
   * @param index The field's index
   * @param value The new value to be set in that field.
   * @return The message itself.
   * @throws IllegalArgumentException if there is no current field at the specified index.
   */
  public <T> IsoBody updateValue(int index, T value) {
    IsoValue<T> current = getField(index);
    if (current == null) {
      throw new IllegalArgumentException(
          "Value-only field setter can only be used on existing fields");
    } else {
      setValue(index, value, current.getEncoder(), current.getType(), current.getLength());
      getField(index).setCharacterEncoding(current.getCharacterEncoding());
      getField(index).setTimeZone(current.getTimeZone());
    }
    return this;
  }

  /**
   * Returns true is the message has a value in the specified field.
   * 
   * @param idx The field number.
   */
  public boolean hasField(int idx) {
    return fields[idx] != null;
  }

  /** Creates a BitSet for the bitmap. */
  protected BitSet createBitmapBitSet() {
    BitSet bs = new BitSet(forceb2 ? 128 : 64);
    for (int i = 2; i < 129; i++) {
      if (fields[i] != null) {
        bs.set(i - 1);
      }
    }
    if (forceb2) {
      bs.set(0);
    } else if (bs.length() > 64) {
      // Extend to 128 if needed
      BitSet b2 = new BitSet(128);
      b2.or(bs);
      bs = b2;
      bs.set(0);
    }
    return bs;
  }


  // These are for Groovy compat
  /**
   * Sets the specified value in the specified field, just like {@link #setField(int, IsoValue)}.
   */
  public <T> void putAt(int i, IsoValue<T> v) {
    setField(i, v);
  }

  /** Returns the IsoValue in the specified field, just like {@link #getField(int)}. */
  public <T> IsoValue<T> getAt(int i) {
    return getField(i);
  }

  // These are for Scala compat
  /**
   * Sets the specified value in the specified field, just like {@link #setField(int, IsoValue)}.
   */
  public <T> void update(int i, IsoValue<T> v) {
    setField(i, v);
  }

  /** Returns the IsoValue in the specified field, just like {@link #getField(int)}. */
  public <T> IsoValue<T> apply(int i) {
    return getField(i);
  }

  /**
   * Copies the specified fields from the other message into the recipient. If a specified field is
   * not present in the source message it is simply ignored.
   */
  public void copyFieldsFrom(IsoBody src, int... idx) {
    for (int i : idx) {
      IsoValue<Object> v = src.getField(i);
      if (v != null) {
        setValue(i, v.getValue(), v.getEncoder(), v.getType(), v.getLength());
      }
    }
  }

  /** Remove the specified fields from the message. */
  public void removeFields(int... idx) {
    for (int i : idx) {
      setField(i, null);
    }
  }

  /**
   * Returns true is the message contains all the specified fields. A convenience for m.hasField(x)
   * &amp;&amp; m.hasField(y) &amp;&amp; m.hasField(z) &amp;&amp; ...
   */
  public boolean hasEveryField(int... idx) {
    for (int i : idx) {
      if (!hasField(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true is the message contains at least one of the specified fields. A convenience for
   * m.hasField(x) || m.hasField(y) || m.hasField(z) || ...
   */
  public boolean hasAnyField(int... idx) {
    for (int i : idx) {
      if (hasField(i)) {
        return true;
      }
    }
    return false;
  }

  public byte[] writeData() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    byte[] bitmap = writeBinBitmap();
    // 输出位图
    try {
      bout.write(bitmap);
    } catch (IOException e) {
      e.printStackTrace();
    }
    // 输出消息体
    byte[] body = writeBody();
    try {
      bout.write(body);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bout.toByteArray();
  }

  /**
   * 输出消息体
   * 
   * @return
   */
  byte[] writeBody() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    // Fields
    for (int i = 2; i < 129; i++) {
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

  /**
   * 输出16进制位图
   * 
   * @return
   */
  byte[] writeBitmap() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    // Bitmap
    BitSet bs = createBitmapBitSet();
    // Write bitmap to stream
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
    return bout.toByteArray();
  }

  /**
   * 输出二进制位图
   * 
   * @return
   */
  byte[] writeBinBitmap() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    // Bitmap
    BitSet bs = createBitmapBitSet();

    int pos = 128;
    int b = 0;
    for (int i = 0; i < bs.size(); i++) {
      if (bs.get(i)) {
        b |= pos;
      }
      pos >>= 1;
      if (pos == 0) {
        bout.write(b);
        pos = 128;
        b = 0;
      }
    }

    return bout.toByteArray();
  }

}
