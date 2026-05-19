package io.github.rltyty.jtkit.junit.helper;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import io.github.rltyty.jtkit.junit.helper.extension.TestCaseCaptor;
import io.github.rltyty.jtkit.junit.helper.extension.TimerExtension;
import io.github.rltyty.jtkit.junit.helper.parser.Scenario;

@ExtendWith(TimerExtension.class)
@ExtendWith(TestCaseCaptor.class)
public abstract class BaseTest {
  // Verbose mode
  private static final boolean VERBOSE = Boolean.getBoolean("test.verbose");
  // Time consuming tests enabled
  private static final boolean SLOW_TESTS_ENABLED = Boolean.getBoolean("test.slow.tests.enabled");

  public static boolean isSlowTestsEnabled() {
    return SLOW_TESTS_ENABLED;
  }

  public static boolean isVerbose() {
    return VERBOSE;
  }

  @BeforeAll
  public static void setUpAll() {
    // System.out.println("BaseTest: Global initialization.");
  }

  @BeforeEach
  protected void setUp() {
    // System.out.println("BaseTest: Case initialization.");
  }

  @AfterEach
  protected void tearDown() {
    // System.out.println("BaseTest: Clean work for this case.");
  }

  @AfterAll
  public static void tearDownAll() {
    // System.out.println("BaseTest: Global clean work.");
  }

  // call this at the start of every test to register current test case
  protected void captureTestCase(Scenario scenario) {
    TestCaseCaptor.store(scenario.toStr());
  }

}
