/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.provider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclaration;
import org.junit.jupiter.params.support.ParameterDeclarations;
import io.github.rltyty.jtkit.junit.helper.annotation.FileData;
import io.github.rltyty.jtkit.junit.helper.loader.LoaderFactory;
import io.github.rltyty.jtkit.junit.helper.loader.TestDataLoader;
import io.github.rltyty.jtkit.junit.helper.parser.Scenario;

/**
 * JUnit {@link ArgumentsProvider} that loads test scenarios from a file
 * declared via {@link FileData}.
 *
 * <p>The scenario type is inferred from the first parameter of the annotated
 * test method, so it does not need to be specified in the annotation itself.
 */
public class TestDataProvider implements ArgumentsProvider {
  /** Creates a new instance. */
  public TestDataProvider() {}

  /**
   * Provides test arguments by loading scenarios from the file declared
   * in the {@link FileData} annotation on the test method.
   *
   * @param parameters the parameter declarations of the test method
   * @param ctx        the current extension context
   * @return a stream of single-element {@link Arguments}, one per scenario
   * @throws IllegalStateException    if {@code @FileData} is missing
   * @throws IllegalArgumentException if the test method does not have exactly
   *                                  one parameter, or if it does not implement
   *                                  {@link Scenario}
   */
  @Override
  public Stream<? extends Arguments> provideArguments(
      ParameterDeclarations parameters, ExtensionContext ctx) {
    Method method = ctx.getRequiredTestMethod();

    FileData data = method.getAnnotation(FileData.class);
    if (data == null) {
      throw new IllegalStateException("Missing @FileData annotation");
    }

    // JUnit 5.13+ required
    List<ParameterDeclaration> declarations = parameters.getAll();
    if (declarations.size() != 1) {
      throw new IllegalArgumentException(
          "@FileData test method must have exactly one parameter: "
              + method.getName());
    }
    Class<?> scenarioTypeRaw = declarations.get(0).getParameterType();


    if (!Scenario.class.isAssignableFrom(scenarioTypeRaw)) {
      throw new IllegalArgumentException(
          "First parameter must implement Scenario: "
              + scenarioTypeRaw.getName());
    }

    if (!scenarioTypeRaw.isRecord()) {
      throw new IllegalArgumentException(
          "Scenario must be a record: " + scenarioTypeRaw.getName());
    }

    @SuppressWarnings("unchecked")
    Class<? extends Scenario> scenarioType =
        (Class<? extends Scenario>) scenarioTypeRaw;

    // used to locate test data source related to the test class
    Class<?> testClass = ctx.getRequiredTestClass();
    try {
      return loadData(data, scenarioType, testClass).stream()
          .map(Arguments::of);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load test data for: " + data.path(),
          e);
    }
  }

  private <T extends Scenario> List<T> loadData(FileData data,
      Class<T> scenarioType, Class<?> testClass) {
    TestDataLoader<T> loader = LoaderFactory.create(data.loader(), scenarioType);
    return loader.load(data.path(), testClass);
  }

}
