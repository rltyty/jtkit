package io.github.rltyty.jtkit.junit.helper.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class InputCaptor implements TestExecutionExceptionHandler {

  private static final ThreadLocal<Object> CURRENT_INPUT = new ThreadLocal<>();

  public static void store(Object input) {
    CURRENT_INPUT.set(input);
  }

  @Override
  public void handleTestExecutionException(ExtensionContext ctx, Throwable t)
      throws Throwable {
    Object input = CURRENT_INPUT.get();
    if (input != null) {
      throw new RuntimeException(
          "Exception on input: [" + input + "]", t);
    }
    throw t; // no input captured, rethrow as-is
  }

}
