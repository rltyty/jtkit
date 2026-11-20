/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.loader;

import java.util.List;
import io.github.rltyty.jtkit.junit.helper.parser.Scenario;

/**
 * Abstract base class for loading test data from external sources.
 *
 * @param <T> the {@link Scenario} type this loader produces
 */
public abstract class TestDataLoader<T extends Scenario> {

  /** The scenario record class this loader produces. */
  protected final Class<T> scenarioType;

  /**
   * Constructs a loader for the given scenario type.
   *
   * @param scenarioType the class of the scenario record to load
   */
  public TestDataLoader(Class<T> scenarioType) {
    this.scenarioType = scenarioType;
  }

  /**
   * Loads all test scenarios from the given path, resolved relative to
   * {@code testClass}'s package on the classpath.
   *
   * @param path      the resource path, relative to the test class's package
   * @param testClass the test class used to locate the resource
   * @return a list of loaded scenarios
   */
  public abstract List<T> load(String path, Class<?> testClass);
}
