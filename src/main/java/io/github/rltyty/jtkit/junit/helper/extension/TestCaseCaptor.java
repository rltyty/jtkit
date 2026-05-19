package io.github.rltyty.jtkit.junit.helper.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class TestCaseCaptor implements TestExecutionExceptionHandler {

  private static final ThreadLocal<Object> CURRENT_DATA = new ThreadLocal<>();

  public static void store(Object data) {
    CURRENT_DATA.set(data);
  }

  @Override
  public void handleTestExecutionException(ExtensionContext ctx, Throwable t)
      throws Throwable {
    Object data = CURRENT_DATA.get();
    if (data != null) {
      throw new RuntimeException(
          "Exception on input: [" + data + "]", t);
    }
    throw t; // no input captured, rethrow as-is
  }

}
