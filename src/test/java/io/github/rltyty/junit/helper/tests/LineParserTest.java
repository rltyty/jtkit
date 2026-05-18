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

  record Input(String expected, String input) {
  }

  static Stream<Input> cases() {
    return Stream.of(
        new Input("abc", " abc  "),
        new Input("abc", "abc"),
        new Input("abc", "abc # comment "),
        new Input("a", "a#"),
        new Input("", ""),
        new Input("", "   "),
        new Input("", "# comment  "),
        new Input("", "  #   "),
        new Input("", " #"),
        new Input("", " #  #"),
        new Input("\"  #  \"", "  \"  #  \"  #  tailing comment "),
        new Input("'abcd'", "'abcd'"),
        new Input("\'abc\'", "\'abc\'"),
        new Input("\'  #  #  \'", "\'  #  #  \'"),
        new Input("\'  #  #  \'", "\'  #  #  \' # tailing comment"),
        new Input("a", " a # tailing comment"),
        new Input("[1, 2, 3]", "  [1, 2, 3]  # tailing comment"),
        new Input("[1,   2, 3]", "  [1,   2, 3]  # tailing comment"),
        new Input("[[1,2],[3,4]]", " [[1,2],[3,4]] # tailing comment"),
        new Input("[[\"a\",\"b\"],[\"c\"]]", "    [[\"a\",\"b\"],[\"c\"]] # a"),
        new Input("\\\\\' abc\'", "\\\\\' abc\' # comment "));
  }

  @ParameterizedTest
  @MethodSource("cases")
  void preProcessLine_test_via_MethodSource(Input c) {
    LineParser parser = new LineParser();
    assertEquals(c.expected, InvokePrivateHelper.invokePrivate(parser, "preProcessLine", c.input));
  }

  public record Input2(
      @ParseWith(DataType.STRING) String ln1,
      @ParseWith(DataType.STRING) String ln2,
      @ParseWith(DataType.STRING) String ln3) {
  }

  @FileData(type = Input2.class, path = "LineParser2.dat", loader = LineParserLoader.class)
  void getNextValidLine_test_via_fileinput(Input2 c) {
    System.out.println("[" + c.ln1() + "], [" + c.ln2() + "], [" + c.ln3() + "]");
  }

  public record Input3(
      @ParseWith(DataType.INTEGER) Integer I1,
      @ParseWith(DataType.DOUBLE) Double I2,
      @ParseWith(DataType.BOOLEAN) Boolean I3) {
  }

  @FileData(type = Input3.class, path = "LineParser3.dat", loader = LineParserLoader.class)
  void getSingle_test_via_fileinput(Input3 c) {
    System.out.println("[" + c.I1 + "], [" + c.I2 + "], [" + c.I3 + "]");
  }

  public record Input4(
      @ParseWith(DataType.STRING) String I1,
      @ParseWith(DataType.LIST_INTEGER) List<Integer> I2) {
  }

  @FileData(type = Input4.class, path = "LineParser4.dat", loader = LineParserLoader.class)
  void getList_test_via_fileinput(Input4 c) {
    System.out.println("[" + c.I1 + "], [" + c.I2 + "]");
  }

  public record Input5(
      @ParseWith(DataType.STRING) String I1,
      @ParseWith(DataType.LIST_2D_STRING) List<List<String>> I2) {
  }

  @FileData(type = Input5.class, path = "LineParser5.dat", loader = LineParserLoader.class)
  void getList2D_test_via_fileinput(Input5 c) {
    System.out.println("[" + c.I1 + "], [" + c.I2 + "]");
  }

  public record  Input6(
      @ParseWith(DataType.LIST_STRING) List<String> I1) {
  }

  @FileData(type = Input6.class, path = "LineParser6.dat", loader = LineParserLoader.class)
  void getList_test_comma_sep(Input6 c) {
    System.out.println(c.I1);
  }

}
