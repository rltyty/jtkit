/*
 * Copyright (c) 2026-present RLTYTY Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

/**
 * JUnit extension that captures the current test scenario and includes it
 * in the failure message when a test throws an exception.
 *
 * <p>Call {@link #store(Object)} at the start of each test (or use
 * {@link io.github.rltyty.jtkit.junit.helper.extension.TestCaseCaptor#store(Object)})
 * to register the active input.
 */
public class TestCaseCaptor
    implements TestExecutionExceptionHandler, AfterTestExecutionCallback {

  /** Creates a new instance. */
  public TestCaseCaptor() {}

  private static final ThreadLocal<Object> CURRENT_DATA = new ThreadLocal<>();

  /**
   * Stores {@code data} as the current test input for the calling thread.
   * Should be called at the start of each parameterized test.
   *
   * @param data the scenario or string representation to capture
   */
  public static void store(Object data) {
    CURRENT_DATA.set(data);
  }

  /**
   * Wraps the thrown exception with the captured input data, if any,
   * to make failure messages easier to diagnose.
   *
   * @param ctx the current extension context
   * @param t   the exception thrown by the test
   * @throws Throwable always — either wrapped with input context or as-is
   */
  @Override
  public void handleTestExecutionException(ExtensionContext ctx, Throwable t)
      throws Throwable {
    Object data = CURRENT_DATA.get();
    if (data != null) {
      throw new RuntimeException("Exception on input: [" + data + "]", t);
    }
    throw t; // no input captured, rethrow as-is
  }

  /**
   * Clears the captured input data after each test, regardless of outcome.
   * Prevents stale data from leaking into subsequent tests when threads are reused.
   *
   * @param ctx the current extension context
   */
  @Override
  public void afterTestExecution(ExtensionContext ctx) {
    CURRENT_DATA.remove();
  }

}
