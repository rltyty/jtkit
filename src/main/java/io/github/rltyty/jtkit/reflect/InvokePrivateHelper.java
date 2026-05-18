package io.github.rltyty.jtkit.reflect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InvokePrivateHelper {

  private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.of(
      boolean.class, Boolean.class,
      byte.class, Byte.class,
      char.class, Character.class,
      short.class, Short.class,
      int.class, Integer.class,
      long.class, Long.class,
      float.class, Float.class,
      double.class, Double.class,
      void.class, Void.class);

  private static final int EXACT_MATCH = 100;
  private static final int WRAPPER_PRIMITIVE_MATCH = 80;
  private static final int ASSIGNABLE_MATCH = 60;
  private static final int NULL_MATCH = 40;
  private static final int NOT_MATCH = -1;

  public static <T> T invokePrivate(Object target, String methodName, Object... args) {
    return __invokePrivate(target, null, methodName, null, args);
  }

  public static <T> T invokePrivate(
      Object target, String methodName, Class<?>[] paramTypes, Object... args) {
    return __invokePrivate(target, null, methodName, paramTypes, args);
  }

  public static <T> T invokeStaticPrivate(
      Class<?> clazz, String methodName, Object... args) {
    return __invokePrivate(null, clazz, methodName, null, args);
  }

  public static <T> T invokeStaticPrivate(
      Class<?> clazz, String methodName, Class<?>[] paramTypes,
      Object... args) {
    return __invokePrivate(null, clazz, methodName, paramTypes, args);
  }

  /**
   * Support invocations of both instance methods and class methods.
   * NOTE:
   * When no explicit parameter type declaration (@paramTypes: null), null
   * argument should NOT be passed as part of @args, like (..., a, b, null,
   * b, null).
   */
  @SuppressWarnings("unchecked")
  private static <T> T __invokePrivate(Object target, Class<?> clazz,
      String methodName, Class<?>[] paramTypes, Object... args) {
    try {
      if (target == null && clazz == null)
        throw new IllegalArgumentException(
            "Neither class nor instance is provided.");
      Class<?> targetClass = (target != null) ? target.getClass() : clazz;
      Method method = null;
      if (paramTypes != null) {
        method = targetClass.getDeclaredMethod(methodName, paramTypes);
      } else {
        method = findBestMatchingMethod(targetClass, methodName, args);
      }
      method.setAccessible(true);
      return (T) method.invoke(target, args);
    } catch (Exception e) {
      throw new RuntimeException("Failed to invoke private method ["
          + methodName + "].", e);
    }
  }

  private static Method findBestMatchingMethod(
      Class<?> clazz, String methodName, Object[] args)
      throws NoSuchMethodException {
    Method[] methods = clazz.getDeclaredMethods();
    List<Method> candidates = new ArrayList<>();
    for (Method method : methods) {
      if (method.getName().equals(methodName) &&
          method.getParameterCount() == args.length) {
        candidates.add(method);
      }
    }
    if (candidates.isEmpty()) {
      throw new NoSuchMethodException("No method found: " + methodName);
    }
    if (candidates.size() == 1) {
      return candidates.get(0);
    }
    return findBestMatchingMethod(candidates, args);
  }

  private static Method findBestMatchingMethod(
      List<Method> candidates, Object[] args) throws NoSuchMethodException {
    Method bestMatch = null;
    int bestScore = NOT_MATCH;
    for (Method method : candidates) {
      int score = calculateMatchScore(method, args);
      if (score > bestScore) {
        bestScore = score;
        bestMatch = method;
      }
    }
    if (bestMatch == null) {
      throw new NoSuchMethodException("No compatible method found");
    }
    return bestMatch;
  }

  private static int calculateMatchScore(Method method, Object[] args) {
    Class<?>[] paramTypes = method.getParameterTypes();
    int score = 0;
    for (int i = 0; i < args.length; i++) {
      if (args[i] == null) {
        if (paramTypes[i].isPrimitive()) {
          return NOT_MATCH;
        } else {
          score += NULL_MATCH;
        }
      } else {
        if (paramTypes[i] == args[i].getClass()) {
          score += EXACT_MATCH;
        } else {
          if (isWrapperPrimitiveRel(paramTypes[i], args[i].getClass())) {
            score += WRAPPER_PRIMITIVE_MATCH;
          } else if (paramTypes[i].isAssignableFrom(args[i].getClass())) {
            score += ASSIGNABLE_MATCH;
          } else {
            return NOT_MATCH;
          }
        }
      }
    }
    return score;
  }

  private static boolean isWrapperPrimitiveRel(Class<?> expected, Class<?> actual) {
    if (expected.isPrimitive()) {
      return actual == PRIMITIVE_TO_WRAPPER.get(expected);
    } else {
      return expected == PRIMITIVE_TO_WRAPPER.get(actual);
    }
  }
}
