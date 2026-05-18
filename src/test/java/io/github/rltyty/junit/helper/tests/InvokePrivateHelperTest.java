package io.github.rltyty.junit.helper.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.rltyty.jtkit.junit.helper.BaseTest;
import io.github.rltyty.jtkit.reflect.InvokePrivateHelper;

class A {
  private static int caseCount = 0;
  private static int insCounter = 0;
  private int x;
  private int y;

  public A(int x, int y) {
    this.x = x;
    this.y = y;
    insCounter++;
  }

  public static void addCaseCount() {
    caseCount++;
  }

  public static int getCaseCount() {
    return caseCount;
  }

  private int sum(int x, int y) {
    return x + y;
  }

  private static int sum2(Integer x, Integer y) {
    return x + y;
  }

  private int getX() {
    return x;
  }

  private static int getInsCounter() {
    return A.insCounter;
  }
}

public class InvokePrivateHelperTest extends BaseTest {

  A a, b, c;

  @Override
  @BeforeEach
  protected void setUp() {
    super.setUp();
    A.addCaseCount();
    this.a = new A(2, 3);
    this.b = new A(5, 7);
    this.c = new A(11, 13);
  }

  @Test
  public void invokePrivateStatic_noParamTypes_test() {

    assertEquals(3*A.getCaseCount(), (int)(InvokePrivateHelper.invokeStaticPrivate(A.class,
          "getInsCounter")));
    assertEquals(83, (int)(InvokePrivateHelper.invokeStaticPrivate(A.class,
          "sum2", 80, 3)));
  }

  @Test
  public void invokePrivateStatic_ParamTypes_test() {
    assertEquals(83, (int)(InvokePrivateHelper.invokeStaticPrivate(A.class,
          "sum2", new Class<?>[]{Integer.class, Integer.class}, 80, 3)));
  }

  @Test
  public void invokePrivate_noParamTypes_test() {
    assertEquals(11, (int)(InvokePrivateHelper.invokePrivate(c, "getX")));

    // Error: java.lang.RuntimeException: Failed to invoke private method [sum].
    // Cause: invokePrivate(b, "sum", 6, 9) will auto-box 6 and 9 from int to
    // Integer. Since no explicit paramTypes provided, Integer.class will be
    // used as paramTypes to search for the declared method. However, the
    // [sum] method accept only primitive type. Thus an exception is thrown.
    // Need to revise __invokePrivate and make it robust.
    // See: korhal.helper.InvokePrivateHelper.findBestMatchingMethod()
    assertEquals(15, (int)(InvokePrivateHelper.invokePrivate(b, "sum", 6, 9)));
  }

  @Test
  public void invokePrivate_ParamTypes_test() {
    assertEquals(15, (int)(InvokePrivateHelper.invokePrivate(
            b, "sum", new Class<?>[]{int.class, int.class}, 6, 9)));
  }
}
