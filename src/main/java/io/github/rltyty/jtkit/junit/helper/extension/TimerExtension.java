package io.github.rltyty.jtkit.junit.helper.extension;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import io.github.rltyty.jtkit.junit.helper.TestUtils;

public class TimerExtension implements BeforeTestExecutionCallback,
    AfterTestExecutionCallback {

  private static final String START_TIME = "start time";
  private static final String TIMER_ENABLED_PROP = "test.timer.enabled";
  private static final Boolean TIMER_ENABLED = Boolean.getBoolean(TIMER_ENABLED_PROP);

  private boolean isTimerEnabled() {
    return TIMER_ENABLED;
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    if (isTimerEnabled())
      context.getStore(ExtensionContext.Namespace.create(context.getRequiredTestMethod()))
          .put(START_TIME, System.nanoTime());
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    if (isTimerEnabled()) {
      ExtensionContext.Store store = context
          .getStore(ExtensionContext.Namespace.create(context.getRequiredTestMethod()));
      long startTime = store.remove(START_TIME, long.class);
      long duration = System.nanoTime() - startTime;
      long s = duration / 1_000_000_000;
      long ms = (duration % 1_000_000_000) / 1_000_000;
      long us = (duration % 1_000_000) / 1_000;
      long ns = duration % 1_000;
      System.out.printf(TestUtils.COLOR_RED + "%-35s" + TestUtils.COLOR_RESET
          + ": %4d s %3d ms %3d µs %3d ns\n", context.getDisplayName(), s, ms, us, ns);
    }
  }
}
