package io.github.rltyty.jtkit.junit.helper;

public class TestUtils {

  public static final String COLOR_RESET = "\u001b[0m";
  public static final String COLOR_RED = "\u001b[31m";
  public static final String COLOR_GREEN = "\u001b[32m";
  public static final String COLOR_YELLOW = "\u001b[33m";
  public static final String COLOR_BLUE = "\u001b[34m";

  /**
   * Keep 2's complement of a signed integer as a signed integer.
   */
  public static int twosComplement(int i) {
    // return (i == Integer.MIN_VALUE) ? i : -i;
    return ~i + 1;
  }

  public static void print_dec_bin32(int i) {
    System.out.println(to11DigitDecimalString(i) + " (dec), " + to32BitBinaryString(i) + " (bin)");
  }

  public static String to32BitBinaryString(int value) {
    String binary = Integer.toBinaryString(value);
    // Pad with leading zeros to 32 characters
    return String.format("%32s", binary).replace(' ', '0');
  }

  public static String to11DigitDecimalString(int value) {
    return String.format("%11s", String.valueOf(value));
  }

  public static void print_sep(String msg) {
    print_sep(msg, '-');
  }

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

  public static void main(String args[]) {
    print_dec_bin32(7);
    print_dec_bin32(-7);
    System.out.println(TestUtils.COLOR_RED + "hello" + TestUtils.COLOR_RESET);
  }
}
