package com.fox.iso8584.field;

public enum FieldType {

  /** A fixed-length numeric value. It is zero-filled to the left. */
  NUMERIC,
  /** A fixed-length alphanumeric value. It is filled with spaces to the right. */
  ALPHA,
  /** A variable length alphanumeric value with a 2-digit header length. */
  LLVAR,
  /** A variable length alphanumeric value with a 3-digit header length. */
  LLLVAR,
  /** A date in format YYYYMMddHHmmss */
  DATE14,
  /** A date in format MMddHHmmss */
  DATE10,
  /** A date in format MMdd */
  DATE4,
  /** A date in format yyMM */
  DATE_EXP,
  /** Time of day in format HHmmss */
  TIME,
  /** An amount, expressed in cents with a fixed length of 12. */
  AMOUNT,
  /** Similar to ALPHA but holds byte arrays instead of strings. */
  BINARY,
  /** Similar to LLVAR but holds byte arrays instead of strings. */
  LLBIN,
  /** Similar to LLLVAR but holds byte arrays instead of strings. */
  LLLBIN,
  /** Similar to LLLVAR but holds byte arrays instead of strings. 长度部分为十进制数*/
  LLLBIN2,
  /** variable length with 4-digit header length. */
  LLLLVAR,
  /** variable length byte array with 4-digit header length. */
  LLLLBIN,
  /** Similar to LLBIN but with a BCD encoded length. */
  LLBCDBIN,
  /** Similar to LLLBIN but with a BCD encoded length. */
  LLLBCDBIN,
  /** Similar to LLLLBIN but with a BCD encoded length. */
  LLLLBCDBIN,
  /** Date in format yyMMddHHmmss. */
  DATE12,
  /** Date in format yyMMdd */
  DATE6;
 
 
}
