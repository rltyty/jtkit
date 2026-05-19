package io.github.rltyty.junit.helper.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import io.github.rltyty.jtkit.junit.helper.BaseTest;
import io.github.rltyty.jtkit.junit.helper.annotation.FileData;
import io.github.rltyty.jtkit.junit.helper.annotation.ParseWith;
import io.github.rltyty.jtkit.junit.helper.loader.LineParserLoader;
import io.github.rltyty.jtkit.junit.helper.parser.DataType;
import io.github.rltyty.jtkit.junit.helper.parser.LineParser;
import io.github.rltyty.jtkit.reflect.InvokePrivateHelper;

public class LineParserTest extends BaseTest {
  @ParameterizedTest
  @CsvSource({
      "abc,       ' abc  '",
      "abc,       'abc'",
      "abc,       'abc # tailing comment'",
      "a,         'a#b'",
      "'abc',     ' abc  '",
      "'',        '  # comment '",
      "\" # \",   '  \" # \" # tailing comment'",
  })
  void preProcessLine_test_via_CvsSource(String expected, String input) {
    LineParser parser = new LineParser();
    assertEquals(expected, InvokePrivateHelper.invokePrivate(parser, "preProcessLine", input));
  }

  record Case(String expected, String input) {
  }

  static Stream<Case> cases() {
    return Stream.of(
        new Case("abc", " abc  "),
        new Case("abc", "abc"),
        new Case("abc", "abc # comment "),
        new Case("a", "a#"),
        new Case("", ""),
        new Case("", "   "),
        new Case("", "# comment  "),
        new Case("", "  #   "),
        new Case("", " #"),
        new Case("", " #  #"),
        new Case("\"  #  \"", "  \"  #  \"  #  tailing comment "),
        new Case("'abcd'", "'abcd'"),
        new Case("\'abc\'", "\'abc\'"),
        new Case("\'  #  #  \'", "\'  #  #  \'"),
        new Case("\'  #  #  \'", "\'  #  #  \' # tailing comment"),
        new Case("a", " a # tailing comment"),
        new Case("[1, 2, 3]", "  [1, 2, 3]  # tailing comment"),
        new Case("[1,   2, 3]", "  [1,   2, 3]  # tailing comment"),
        new Case("[[1,2],[3,4]]", " [[1,2],[3,4]] # tailing comment"),
        new Case("[[\"a\",\"b\"],[\"c\"]]", "    [[\"a\",\"b\"],[\"c\"]] # a"),
        new Case("\\\\\' abc\'", "\\\\\' abc\' # comment "));
  }

  @ParameterizedTest
  @MethodSource("cases")
  void preProcessLine_test_via_MethodSource(Case c) {
    LineParser parser = new LineParser();
    assertEquals(c.expected, InvokePrivateHelper.invokePrivate(parser, "preProcessLine", c.input));
  }

  public record Case2(
      @ParseWith(DataType.STRING) String ln1,
      @ParseWith(DataType.STRING) String ln2,
      @ParseWith(DataType.STRING) String ln3) {
  }

  @FileData(path = "LineParser2.dat", loader = LineParserLoader.class)
  void getNextValidLine_test_via_fileinput(Case2 c) {
    System.out.println("[" + c.ln1() + "], [" + c.ln2() + "], [" + c.ln3() + "]");
  }

  public record Case3(
      @ParseWith(DataType.INTEGER) Integer I1,
      @ParseWith(DataType.DOUBLE) Double I2,
      @ParseWith(DataType.BOOLEAN) Boolean I3) {
  }

  @FileData(path = "LineParser3.dat", loader = LineParserLoader.class)
  void getSingle_test_via_fileinput(Case3 c) {
    System.out.println("[" + c.I1 + "], [" + c.I2 + "], [" + c.I3 + "]");
  }

  public record Case4(
      @ParseWith(DataType.STRING) String I1,
      @ParseWith(DataType.LIST_INTEGER) List<Integer> I2) {
  }

  @FileData(path = "LineParser4.dat", loader = LineParserLoader.class)
  void getList_test_via_fileinput(Case4 c) {
    System.out.println("[" + c.I1 + "], [" + c.I2 + "]");
  }

  public record Case5(
      @ParseWith(DataType.STRING) String I1,
      @ParseWith(DataType.LIST_2D_STRING) List<List<String>> I2) {
  }

  @FileData(path = "LineParser5.dat", loader = LineParserLoader.class)
  void getList2D_test_via_fileinput(Case5 c) {
    System.out.println("[" + c.I1 + "], [" + c.I2 + "]");
  }

  public record  Case6(
      @ParseWith(DataType.LIST_STRING) List<String> I1) {
  }

  @FileData(path = "LineParser6.dat", loader = LineParserLoader.class)
  void getList_test_comma_sep(Case6 c) {
    System.out.println(c.I1);
  }

}
