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

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fox.iso8584.exception.MessageParseException;
import com.fox.iso8584.field.FieldFactory;
import com.fox.iso8584.field.FieldParseFactory;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldType;
import com.fox.iso8584.field.FieldValue;


/**
 * This class is used to create messages, either from scratch or from an existing String or byte
 * buffer. It can be configured to put default values on newly created messages, and also to know
 * what to expect when reading messages from an InputStream.
 * <P>
 * The factory can be configured to know what values to set for newly created messages, both from a
 * template (useful for fields that must be set with the same value for EVERY message created) and
 * individually (for trace [field 11] and message date [field 7]).
 * <P>
 * It can also be configured to know what fields to expect in incoming messages (all possible values
 * must be stated, indicating the date type for each). This way the messages can be parsed from a
 * byte buffer.
 * 
 * @author Enrique Zamudio
 */
public class MessageFactory {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  /** This map stores the message template for each message type. */
  private Map<Integer, IsoBody> typeTemplates = new HashMap<>();
  /** Stores the information needed to parse messages sorted by type. */
  protected Map<Integer, Map<Integer, FieldParseInfo>> parseMap = new HashMap<>();
  /** Stores the field numbers to be parsed, in order of appearance. */
  protected Map<Integer, List<Integer>> parseOrder = new HashMap<>();

  private TraceNumberGenerator traceGen;
  /** The ISO header to be included in each message type. */
  private Map<Integer, String> isoHeaders = new HashMap<>();
  /** A map for the custom field encoder/decoders, keyed by field number. */
  @SuppressWarnings("rawtypes")
  private Map<Integer, CustomField> customFields = new HashMap<>();
  /** Indicates if the current date should be set on new messages (field 7). */
  private boolean setDate;

  private int etx = -1;
  
  private boolean forceb2;
  /**
   * 源ID 用于构造消息头
   */
  private String sourceStationId;
  /**
   * 目标ID 用于构造消息头
   */
  private String destinationId;

  public void setSourceStationId(String sourceStationId) {
    this.sourceStationId = sourceStationId;
  }

  public void setDestinationId(String destinationId) {
    this.destinationId = destinationId;
  }

  /**
   * Sets or clears the flag to pass to new messages, to include a secondary bitmap even if it's not
   * needed.
   */
  public void setForceSecondaryBitmap(boolean flag) {
    forceb2 = flag;
  }

  public boolean isForceSecondaryBitmap() {
    return forceb2;
  }

  /** Specifies a map for custom field encoder/decoders. The keys are the field numbers. */
  @SuppressWarnings("rawtypes")
  public void setCustomFields(Map<Integer, CustomField> value) {
    customFields = value;
  }

  /** Sets the CustomField encoder for the specified field number. */
  public void setCustomField(int index, CustomField<?> value) {
    customFields.put(index, value);
  }

  /** Returns a custom field encoder/decoder for the specified field number, if one is available. */
  @SuppressWarnings("unchecked")
  public <F> CustomField<F> getCustomField(int index) {
    return customFields.get(index);
  }

  /** Returns a custom field encoder/decoder for the specified field number, if one is available. */
  @SuppressWarnings("unchecked")
  public <F> CustomField<F> getCustomField(Integer index) {
    return customFields.get(index);
  }

  /**
   * Tells the receiver to read the configuration at the specified path. This just calls
   * ConfigParser.configureFromClasspathConfig() with itself and the specified path at arguments,
   * but is really convenient in case the MessageFactory is being configured from within, say,
   * Spring.
   */
  public void setConfigPath(String path) throws IOException {
    ConfigParser.configureFromClasspathConfig(this, path);
    // Now re-set some properties that need to be propagated down to the recently assigned objects
    // setCharacterEncoding(encoding);
  }

  /**
   * Sets the ETX character to be sent at the end of the message. This is optional and the default
   * is -1, which means nothing should be sent as terminator.
   * 
   * @param value The ASCII value of the ETX character or -1 to indicate no terminator should be
   *        used.
   */
  public void setEtx(int value) {
    etx = value;
  }

  public int getEtx() {
    return etx;
  }

  /**
   * Creates a new message of the specified type, with optional trace and date values as well as any
   * other values specified in a message template. If the factory is set to use binary messages,
   * then the returned message will be written using binary coding.
   * 
   * @param type The message type, for example 0x200, 0x400, etc.
   */
  public IsoMessage newMessage(int type) {

    // Copy the values from the template
    IsoBody templ = typeTemplates.get(type);
    IsoBody body = createIsoBody(templ);

    IsoHeader header = new IsoHeader(sourceStationId, destinationId);

    IsoMessage message = new IsoMessage(type, header, body);
    return message;
  }



  /**
   * Creates a new message instance from the buffer, which must contain a valid ISO8583 message. If
   * the factory is set to use binary messages then it will try to parse a binary message.
   * 
   * @param buf The byte buffer containing the message. Must not include the length header.
   * @param isoHeaderLength The expected length of the ISO header, after which the message type and
   *        the rest of the message must come.
   */
  public IsoMessage parseMessage(byte[] buf, int isoHeaderLength, String charset)
      throws MessageParseException {
    final int minlength = isoHeaderLength + 4;
    if (buf.length < minlength) {
      throw new MessageParseException("Insufficient buffer length, needs to be at least " + minlength);
    }

    final int type;
    type = ((buf[isoHeaderLength] - 48) << 12) | ((buf[isoHeaderLength + 1] - 48) << 8)
        | ((buf[isoHeaderLength + 2] - 48) << 4) | (buf[isoHeaderLength + 3] - 48);
    
    IsoBody body = new IsoBody();

    // Parse the bitmap (primary first)
    final BitSet bs = new BitSet(64);
    int pos = parseBinBitmap(bs, buf, isoHeaderLength);

    // Parse each field
    Map<Integer, FieldParseInfo> parseGuide = parseMap.get(type);
    List<Integer> index = parseOrder.get(type);
    if (index == null) {

      throw new MessageParseException(
          String.format("ISO8583 MessageFactory has no parsing guide for message type %04x [%s]",
              type, new String(buf)));
    }
    // First we check if the message contains fields not specified in the parsing template
    boolean abandon = false;
    for (int i = 1; i < bs.length(); i++) {
      if (bs.get(i) && !index.contains(i + 1)) {
        log.warn(
            "ISO8583 MessageFactory cannot parse field {}: unspecified in parsing guide for type {}",
            i + 1, Integer.toString(type, 16));
        abandon = true;
      }
    }
    if (abandon) {
      throw new MessageParseException("ISO8583 MessageFactory cannot parse fields");
    }
    // Now we parse each field
    try {
      for (Integer i : index) {
        FieldParseInfo fpi = parseGuide.get(i);
        if (bs.get(i - 1)) {
          CustomField<?> decoder = getCustomField(i);

          FieldValue<?> field = FieldParseFactory.parse(fpi, buf, pos, decoder, charset);
          body.setField(i, field);
          pos += field.getValueLength(charset);
        }
      }
    } catch (Exception e) {
      throw new MessageParseException(e);
    }

    final IsoMessage message = new IsoMessage(type);
    message.setIsoBody(body);
    return message;
  }

  private int parseBinBitmap(BitSet bs, byte[] buf, int isoHeaderLength) throws MessageParseException {

    final int minlength = isoHeaderLength + 4 + 8;
    // final BitSet bs = new BitSet(64);
    int pos = 0;
    final int bitmapStart = isoHeaderLength + 4;
    for (int i = bitmapStart; i < 8 + bitmapStart; i++) {
      int bit = 128;
      for (int b = 0; b < 8; b++) {
        bs.set(pos++, (buf[i] & bit) != 0);
        bit >>= 1;
      }
    }
    // Check for secondary bitmap and parse if necessary
    if (bs.get(0)) {
      if (buf.length < minlength + 8) {
        throw new MessageParseException("Insufficient length for secondary bitmap");
      }
      for (int i = 8 + bitmapStart; i < 16 + bitmapStart; i++) {
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
    return pos;
  }


  /**
   * Creates a Iso message, override this method in the subclass to provide your own implementations
   * of IsoMessage.
   * 
   * @param header The optional ISO header that goes before the message type
   * @return IsoMessage
   */
  protected IsoBody createIsoBody(IsoBody templ) {

    IsoBody body = new IsoBody();
    body.setForceSecondaryBitmap(forceb2);

    // Copy the values from the template
    if (templ != null) {
      for (int i = 2; i <= 128; i++) {
        if (templ.hasField(i)) {
          // We could detect here if there's a custom object with a CustomField,
          // but we can't copy the value so there's no point.
          body.setField(i, templ.getField(i).clone());
        }
      }
    }
    if (traceGen != null) {
      body.setField(11, FieldFactory.getField(FieldType.NUMERIC, traceGen.nextTrace(), 6));
    }
    if (setDate) {
      body.setField(7, FieldFactory.getField(FieldType.DATE10, new Date()));
    }

    return body;
  }


  /**
   * Sets whether the factory should set the current date on newly created messages, in field 7.
   * Default is false.
   */
  public void setAssignDate(boolean flag) {
    setDate = flag;
  }

  /**
   * Returns true if the factory is assigning the current date to newly created messages (field 7).
   * Default is false.
   */
  public boolean getAssignDate() {
    return setDate;
  }

  /**
   * Sets the generator that this factory will get new trace numbers from. There is no default
   * generator.
   */
  public void setTraceNumberGenerator(TraceNumberGenerator value) {
    traceGen = value;
  }

  /** Returns the generator used to assign trace numbers to new messages. */
  public TraceNumberGenerator getTraceNumberGenerator() {
    return traceGen;
  }

  /**
   * Sets the ISO header to be used in each message type.
   * 
   * @param value A map where the keys are the message types and the values are the ISO headers.
   */
  public void setIsoHeaders(Map<Integer, String> value) {
    isoHeaders.clear();
    isoHeaders.putAll(value);
  }

  /**
   * Sets the ISO header for a specific message type.
   * 
   * @param type The message type, for example 0x200.
   * @param value The ISO header, or NULL to remove any headers for this message type.
   */
  public void setIsoHeader(int type, String value) {
    if (value == null) {
      isoHeaders.remove(type);
    } else {
      isoHeaders.put(type, value);
    }
  }

  /** Returns the ISO header used for the specified type. */
  public String getIsoHeader(int type) {
    return isoHeaders.get(type);
  }

  /**
   * Adds a message template to the factory. If there was a template for the same message type as
   * the new one, it is overwritten.
   */
  public void addMessageTemplate(int type, IsoBody templ) {
    if (templ != null) {
      typeTemplates.put(type, templ);
    }
  }

  /** Removes the message template for the specified type. */
  public void removeMessageTemplate(int type) {
    typeTemplates.remove(type);
  }

  /**
   * Returns the template for the specified message type. This allows templates to be modified
   * programmatically.
   */
  public IsoBody getMessageTemplate(int type) {
    return typeTemplates.get(type);
  }

  /**
   * Sets a map with the fields that are to be expected when parsing a certain type of message.
   * 
   * @param type The message type.
   * @param map A map of FieldParseInfo instances, each of which define what type and length of
   *        field to expect. The keys will be the field numbers.
   */
  public void setParseMap(int type, Map<Integer, FieldParseInfo> map) {
    parseMap.put(type, map);
    ArrayList<Integer> index = new ArrayList<>();
    index.addAll(map.keySet());
    Collections.sort(index);
    log.trace(String.format("ISO8583 MessageFactory adding parse map for type %04x with fields %s",
        type, index));
    parseOrder.put(type, index);
  }

}
