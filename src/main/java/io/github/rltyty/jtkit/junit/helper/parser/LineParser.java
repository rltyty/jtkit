package io.github.rltyty.jtkit.junit.helper.parser;

import java.text.ParseException;
import java.util.List;
import java.util.Scanner;

public class LineParser extends Parser {
  private Scanner scanner = null;
  private int ln = 0;

  public LineParser() {
  }

  public LineParser(Scanner scanner) {
    this.scanner = scanner;
    this.ln = 0;
  }

  public int getLn() {
    return ln;
  }

  @Override
  public <T> T getSingle(Class<T> t) {
    T o = null;
    try {
      String line = getNextValidLine();
      if (line != null) {
        o = getSingle(line, t);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return o;
  }

  @Override
  public <E> List<E> getList(Class<E> t){
    List<E> list = null;
    try {
      String line = getNextValidLine();
      if (line != null) {
        list = getList(line, t);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return list;
  };

  @Override
  public <E> List<List<E>> getList2D(Class<E> t){
    List<List<E>> l = null;
    try {
      String line = getNextValidLine();
      if (line != null) {
        l = getList2D(line, t);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return l;
  };

  @Override
  public <T> T getPrimitiveArray(Class<?> primitiveType) {
    T arr = null;
    try {
      String line = getNextValidLine();
      if (line != null) {
        arr = getPrimitiveArray(line, primitiveType);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return arr;
  }

  public String getNextValidLine() throws ParseException {
    while (scanner.hasNextLine()) {
      ln++;
      String line = preProcessLine(scanner.nextLine());
      if (!line.isEmpty()) {
        return line;
      }
    }
    return null;
  }

  private String preProcessLine(String line) throws ParseException {
    String result = line.strip();

    // 1. Skip blank line
    if (result.isEmpty()) {
      return "";
    }

    // 2. Check quotes, escape characters and remove comment
    boolean escaped = false;
    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;

    for (int i = 0; i < result.length(); i++) {
      char c = result.charAt(i);
      if (escaped) {
        escaped = false;
        continue;
      }

      if (c == '\\' && (inSingleQuote || inDoubleQuote)) {
        escaped = true;
        continue;
      }

      if (c == '\'' && !inDoubleQuote) {
        inSingleQuote = !inSingleQuote;
        continue;
      }

      if (c == '"' && !inSingleQuote) {
        inDoubleQuote = !inDoubleQuote;
        continue;
      }

      if (c == '#' && !inSingleQuote && !inDoubleQuote) {
        return result.substring(0, i).strip(); // both comment and tailing
      }
    }

    if (inSingleQuote || inDoubleQuote) {
      throw new ParseException("Unterminated string literal", this.ln);
    }
    if (escaped) {
      throw new ParseException("Trailing escape character", this.ln);
    }
    return result;
  }
}
