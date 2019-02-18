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
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.fox.iso8584.field.FieldFactory;
import com.fox.iso8584.field.FieldParseInfo;
import com.fox.iso8584.field.FieldType;
import com.fox.iso8584.field.FieldValue;

/**
 * This class is used to parse a XML configuration file and configure a MessageFactory with the
 * values from it.
 * 
 * @author Enrique Zamudio
 */
public class ConfigParser {

  private final static Logger log = LoggerFactory.getLogger(ConfigParser.class);

  /**
   * Creates a message factory configured from the default file, which is j8583.xml located in the
   * root of the classpath, using the specified ClassLoader.
   */
  public static MessageFactory createDefault(ClassLoader loader) throws IOException {
    if (loader.getResource("j8583.xml") == null) {
      log.warn("ISO8583 ConfigParser cannot find j8583.xml, returning empty message factory");
      return new MessageFactory();
    } else {
      return createFromClasspathConfig(loader, "j8583.xml");
    }
  }

  /**
   * Creates a message factory configured from the default file, which is j8583.xml located in the
   * root of the classpath, using the MessageFactory's ClassLoader.
   */
  public static MessageFactory createDefault() throws IOException {
    return createDefault(MessageFactory.class.getClassLoader());
  }

  /**
   * Creates a message factory from the specified path inside the classpath, using the specified
   * ClassLoader.
   */
  public static MessageFactory createFromClasspathConfig(String path) throws IOException {
    return createFromClasspathConfig(MessageFactory.class.getClassLoader(), path);
  }

  /**
   * Creates a message factory from the specified path inside the classpath, using MessageFactory's
   * ClassLoader.
   */
  public static MessageFactory createFromClasspathConfig(ClassLoader loader, String path)
      throws IOException {
    MessageFactory mfact = new MessageFactory();
    try (InputStream ins = loader.getResourceAsStream(path)) {
      if (ins != null) {
        log.debug("ISO8583 Parsing config from classpath file {}", path);
        parse(mfact, new InputSource(ins));
      } else {
        log.error("ISO8583 File not found in classpath: {}", path);
      }
    }
    return mfact;
  }

  /** Creates a message factory from the file located at the specified URL. */
  public static MessageFactory createFromUrl(URL url) throws IOException {
    MessageFactory mfact = new MessageFactory();
    try (InputStream stream = url.openStream()) {
      parse(mfact, new InputSource(stream));
    }
    return mfact;
  }

  /** Creates a messageFactory from the XML contained in the specified Reader. */
  public static MessageFactory createFromReader(Reader reader) throws IOException {
    MessageFactory mfact = new MessageFactory();
    parse(mfact, new InputSource(reader));
    return mfact;
  }

  protected static <T extends IsoBody> void parseHeaders(final NodeList nodes,
      final MessageFactory mfact) throws IOException {
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);
      int type = parseType(elem.getAttribute("type"));
      if (type == -1) {
        throw new IOException("Invalid type for ISO8583 header: " + elem.getAttribute("type"));
      }
      String header = elem.getChildNodes().item(0).getNodeValue();
      boolean binHeader = "true".equals(elem.getAttribute("binary"));
      if (log.isTraceEnabled()) {
        log.trace("Adding {}ISO8583 header for type {}: {}", binHeader ? "binary " : "",
            elem.getAttribute("type"), header);
      }
      mfact.setIsoHeader(type, header);
    }

  }

  protected static <T extends IsoBody> void parseTemplates(final NodeList nodes,
      final MessageFactory mfact) throws IOException {
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);
      int type = parseType(elem.getAttribute("type"));
      if (type == -1) {
        throw new IOException("Invalid ISO8583 type for template: " + elem.getAttribute("type"));
      }

      IsoBody m = new IsoBody();
      // m.setType(type);
      m.setCharacterEncoding(mfact.getCharacterEncoding());
      NodeList fields = elem.getElementsByTagName("field");
      for (int j = 0; j < fields.getLength(); j++) {
        Element f = (Element) fields.item(j);
        if (f.getParentNode() == elem) {
          final int num = Integer.parseInt(f.getAttribute("num"));
          FieldValue<?> v = getTemplateField(f, mfact, true);
           m.setField(num, v);
        }
      }
      mfact.addMessageTemplate(type, m);
    }
  }

  /**
   * Creates an IsoValue from the XML definition in a message template. If it's for a toplevel field
   * and the message factory has a codec for this field, that codec is assigned to that field. For
   * nested fields, a CompositeField is created and populated.
   */
  protected static <M extends IsoBody> FieldValue<?> getTemplateField(Element f,
      MessageFactory mfact, boolean toplevel) {
    final int num = Integer.parseInt(f.getAttribute("num"));
    final String typedef = f.getAttribute("type");
    int length = 0;
    if (f.getAttribute("length").length() > 0) {
      length = Integer.parseInt(f.getAttribute("length"));
    }
    final FieldType itype = FieldType.valueOf(typedef);
    final String v;
    if (f.getChildNodes().getLength() == 0) {
      v = "";
    } else {
      v = f.getChildNodes().item(0).getNodeValue();
    }
    final CustomField<Object> cf = toplevel ? mfact.getCustomField(num) : null;
    FieldValue<?> rv;
    if (cf == null) {
      rv = FieldFactory.getField(itype, v, length, "GBK");
    } else {
      rv = FieldFactory.getField(itype, cf.decodeField(v, "GBK"), length, "GBK");
    }

    return rv;
  }

  protected static FieldParseInfo getParser(Element f, MessageFactory mfact) {
    FieldType type = FieldType.valueOf(f.getAttribute("type"));
    int length = 0;
    if (f.getAttribute("length").length() > 0) {
      length = Integer.parseInt(f.getAttribute("length"));
    }

    FieldParseInfo fp = new FieldParseInfo(type, length);

    NodeList subs = f.getElementsByTagName("field");
    if (subs != null && subs.getLength() > 0) {
      List<FieldParseInfo> list = new ArrayList<FieldParseInfo>();
      for (int i = 0; i < subs.getLength(); i++) {
        Element sf = (Element) subs.item(i);
        if (sf.getParentNode() == f) {
          list.add(getParser(sf, mfact));
        }
      }
      fp.setSubFieldParse(list);
    }
    return fp;
  }

  protected static <T extends IsoBody> void parseGuides(final NodeList nodes,
      final MessageFactory mfact) throws IOException {
    HashMap<Integer, HashMap<Integer, FieldParseInfo>> guides = new HashMap<>();
    for (int i = 0; i < nodes.getLength(); i++) {
      Element elem = (Element) nodes.item(i);
      int type = parseType(elem.getAttribute("type"));
      if (type == -1) {
        throw new IOException("Invalid ISO8583 type for parse guide: " + elem.getAttribute("type"));
      }

      HashMap<Integer, FieldParseInfo> parseMap = new HashMap<>();
      NodeList fields = elem.getElementsByTagName("field");
      for (int j = 0; j < fields.getLength(); j++) {
        Element f = (Element) fields.item(j);
        if (f.getParentNode() == elem) {
          int num = Integer.parseInt(f.getAttribute("num"));
          parseMap.put(num, getParser(f, mfact));
        }
      }
      mfact.setParseMap(type, parseMap);
      guides.put(type, parseMap);
    }
  }


  /**
   * Reads the XML from the stream and configures the message factory with its values.
   * 
   * @param mfact The message factory to be configured with the values read from the XML.
   * @param source The InputSource containing the XML configuration.
   */
  protected static <T extends IsoBody> void parse(MessageFactory mfact, InputSource source)
      throws IOException {
    final DocumentBuilderFactory docfact = DocumentBuilderFactory.newInstance();
    DocumentBuilder docb = null;
    Document doc = null;
    try {
      docb = docfact.newDocumentBuilder();
      doc = docb.parse(source);
    } catch (ParserConfigurationException | SAXException ex) {
      log.error("ISO8583 Cannot parse XML configuration", ex);
      return;
    }
    final Element root = doc.getDocumentElement();

    parseHeaders(root.getElementsByTagName("header"), mfact);
    parseTemplates(root.getElementsByTagName("template"), mfact);
    // Read the parsing guides
    parseGuides(root.getElementsByTagName("parse"), mfact);
  }

  /**
   * Configures a MessageFactory using the default configuration file j8583.xml. This is useful if
   * you have a MessageFactory created using Spring for example.
   */
  public static <T extends IsoBody> void configureFromDefault(MessageFactory mfact)
      throws IOException {
    if (mfact.getClass().getClassLoader().getResource("j8583.xml") == null) {
      log.warn("ISO8583 config file j8583.xml not found!");
    } else {
      configureFromClasspathConfig(mfact, "j8583.xml");
    }
  }

  /**
   * This method attempts to open a stream from the XML configuration in the specified URL and
   * configure the message factory from that config.
   */
  public static <T extends IsoBody> void configureFromUrl(MessageFactory mfact, URL url)
      throws IOException {
    try (InputStream stream = url.openStream()) {
      parse(mfact, new InputSource(stream));
    }
  }

  /**
   * Configures a MessageFactory using the configuration file at the path specified (will be
   * searched within the classpath using the MessageFactory's ClassLoader). This is useful for
   * configuring Spring-bound instances of MessageFactory for example.
   */
  public static <T extends IsoBody> void configureFromClasspathConfig(MessageFactory mfact,
      String path) throws IOException {
    try (InputStream ins = mfact.getClass().getClassLoader().getResourceAsStream(path)) {
      if (ins != null) {
        log.debug("ISO8583 Parsing config from classpath file {}", path);
        parse(mfact, new InputSource(ins));
      } else {
        log.warn("ISO8583 File not found in classpath: {}", path);
      }
    }
  }

  /** Configures a MessageFactory using the XML data obtained from the specified Reader. */
  public static <T extends IsoBody> void configureFromReader(MessageFactory mfact, Reader reader)
      throws IOException {
    parse(mfact, new InputSource(reader));
  }

  /**
   * Parses a message type expressed as a hex string and returns the integer number. For example,
   * "0200" or "200" return the number 512 (0x200)
   */
  private static int parseType(String type) throws IOException {
    if (type.length() % 2 == 1) {
      type = "0" + type;
    }
    if (type.length() != 4) {
      return -1;
    }
    return ((type.charAt(0) - 48) << 12) | ((type.charAt(1) - 48) << 8)
        | ((type.charAt(2) - 48) << 4) | (type.charAt(3) - 48);
  }

}
