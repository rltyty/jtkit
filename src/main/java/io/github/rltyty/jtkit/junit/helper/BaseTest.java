/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import io.github.rltyty.jtkit.junit.helper.extension.TestCaseCaptor;
import io.github.rltyty.jtkit.junit.helper.extension.TimerExtension;
import io.github.rltyty.jtkit.junit.helper.parser.Scenario;

/**
 * Base class for JUnit tests.
 *
 * <p>Provides:
 * <ul>
 *   <li>Shared JUnit extensions: {@link TimerExtension} for optional
 *       per-test timing, and {@link TestCaseCaptor} for capturing the
 *       current test scenario on failure.</li>
 *   <li>System property flags: {@code test.verbose} and
 *       {@code test.long.running.tests.enabled}.</li>
 *   <li>Lifecycle hooks ({@code setUpAll}, {@code setUp},
 *       {@code tearDown}, {@code tearDownAll}) that subclasses can
 *       override as needed. The base implementations are intentionally
 *       empty.</li>
 *  <li>etc.</li>
 * </ul>
 */
@ExtendWith({TimerExtension.class, TestCaseCaptor.class})
public abstract class BaseTest {
  /** No-arg constructor for subclasses. */
  protected BaseTest() {}

  // Verbose mode
  private static final boolean VERBOSE = Boolean.getBoolean("test.verbose");
  // Time consuming tests enabled
  private static final boolean LONG_RUNNING_TESTS_ENABLED =
      Boolean.getBoolean("test.long.running.tests.enabled");

  /**
   * Returns {@code true} if long running tests are enabled via {@code
   * -Dtest.long.running.tests.enabled=true}.
   *
   * @return {@code true} if long running tests are enabled
   */
  public static boolean isLongRunningTestsEnabled() {
    return LONG_RUNNING_TESTS_ENABLED;
  }

  /**
   * Checks whether verbose output is enabled via {@code -Dtest.verbose=true}.
   *
   * @return {@code true} if enabled; {@code false} otherwise
   */
  public static boolean isVerbose() {
    return VERBOSE;
  }

  /** Runs once before all tests in the subclass. Override to add global setup. */
  @BeforeAll
  public static void setUpAll() {}

  /** Runs before each test. Override to add per-test setup. */
  @BeforeEach
  protected void setUp() {}

  /** Runs after each test. Override to add per-test cleanup. */
  @AfterEach
  protected void tearDown() {}

  /** Runs once after all tests in the subclass. Override to add global cleanup. */
  @AfterAll
  public static void tearDownAll() {}

  /**
   * Registers the given scenario as the current test input.
   * Call this at the start of each parameterized test so that
   * {@link TestCaseCaptor} can include the scenario in the failure message.
   *
   * @param scenario the current test scenario
   */
  protected void captureTestCase(Scenario scenario) {
    TestCaseCaptor.store(scenario.toStr());
  }

}
