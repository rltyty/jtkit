/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper;

/**
 * Utilities for test output and bit-level integer inspection.
 */
public class TestUtils {

  /** Utility class; not instantiable. */
  private TestUtils() {}

  /** ANSI reset escape code. */
  public static final String COLOR_RESET = "\u001b[0m";
  /** ANSI red color escape code. */
  public static final String COLOR_RED = "\u001b[31m";
  /** ANSI green color escape code. */
  public static final String COLOR_GREEN = "\u001b[32m";
  /** ANSI yellow color escape code. */
  public static final String COLOR_YELLOW = "\u001b[33m";
  /** ANSI blue color escape code. */
  public static final String COLOR_BLUE = "\u001b[34m";

  /**
   * Returns the two's complement negation of {@code i} as a signed integer.
   * Equivalent to {@code -i} for all values except {@link Integer#MIN_VALUE},
   * which maps to itself.
   *
   * @param i the integer to negate
   * @return the two's complement of {@code i}
   */
  public static int twosComplement(int i) {
    // return (i == Integer.MIN_VALUE) ? i : -i;
    return ~i + 1;
  }

  /**
   * Prints {@code i} as both an 11-digit decimal and a 32-bit binary string.
   *
   * @param i the integer to print
   */
  public static void print_dec_bin32(int i) {
    System.out.println(to11DigitDecimalString(i) + " (dec), " + to32BitBinaryString(i) + " (bin)");
  }

  /**
   * Returns {@code value} as a zero-padded 32-character binary string.
   *
   * @param value the integer to convert
   * @return a 32-character binary string representation
   */
  public static String to32BitBinaryString(int value) {
    String binary = Integer.toBinaryString(value);
    // Pad with leading zeros to 32 characters
    return String.format("%32s", binary).replace(' ', '0');
  }

  /**
   * Returns {@code value} as an 11-character right-aligned decimal string.
   *
   * @param value the integer to convert
   * @return an 11-character decimal string representation
   */
  public static String to11DigitDecimalString(int value) {
    return String.format("%11s", String.valueOf(value));
  }


  /**
   * Prints a separator line of 30 dashes on each side of {@code msg}.
   *
   * @param msg the message to embed in the separator
   */
  public static void print_sep(String msg) {
    print_sep(msg, '-');
  }

  /**
   * Prints a separator line of 30 repetitions of {@code c} on each side of {@code msg}.
   *
   * @param msg the message to embed in the separator
   * @param c   the character to use for the separator line
   */
  public static void print_sep(String msg, char c) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 30; i++)
      sb.append(c);
    sb.append("  ");
    sb.append(msg);
    sb.append("  ");
    for (int i = 0; i < 30; i++)
      sb.append(c);
    System.out.println(sb.toString());
  }

  /**
   * Development entry point for manual testing; not part of the public API.
   *
   * @param args command-line arguments (unused)
   */
  public static void main(String args[]) {
    print_dec_bin32(7);
    print_dec_bin32(-7);
    System.out.println(TestUtils.COLOR_RED + "hello" + TestUtils.COLOR_RESET);
  }
}
