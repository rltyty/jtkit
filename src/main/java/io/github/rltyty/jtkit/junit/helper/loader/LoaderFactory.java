/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.loader;

import java.lang.reflect.InvocationTargetException;
import io.github.rltyty.jtkit.junit.helper.parser.Scenario;

/**
 * Factory for instantiating {@link TestDataLoader} implementations via reflection.
 */
public final class LoaderFactory {

  /** Utility class; not instantiable. */
  private LoaderFactory() {}

  /**
   * Creates a new instance of the given loader class for the specified scenario type.
   *
   * <p>The loader class must declare a single-argument constructor of the form
   * {@code MyLoader(Class<T> scenarioType)}.
   *
   * @param <T>          the scenario type
   * @param loaderClass  the loader class to instantiate
   * @param scenarioType the scenario record class to pass to the loader constructor
   * @return a new loader instance
   * @throws IllegalStateException if the required constructor is not found
   * @throws RuntimeException      if instantiation fails
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static <T extends Scenario> TestDataLoader<T> create(
      Class<? extends TestDataLoader> loaderClass, Class<T> scenarioType) {

    try {
      return (TestDataLoader<T>) loaderClass
        .getDeclaredConstructor(Class.class)
        .newInstance(scenarioType);
    } catch (NoSuchMethodException e) {
      throw new IllegalStateException("Loader " + loaderClass.getName()
          + " must declare constructor (Class<T>)", e);
    } catch (InstantiationException | IllegalAccessException
        | InvocationTargetException e) {
      throw new RuntimeException(
          "Failed to instantiate loader " + loaderClass.getName(), e);
    }
  }
}
