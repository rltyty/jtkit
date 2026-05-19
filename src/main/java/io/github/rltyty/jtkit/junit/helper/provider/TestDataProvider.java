package io.github.rltyty.jtkit.junit.helper.provider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.ParameterDeclarations;
import io.github.rltyty.jtkit.junit.helper.annotation.FileData;
import io.github.rltyty.jtkit.junit.helper.loader.TestDataLoader;

public class TestDataProvider implements ArgumentsProvider {

  @Override
  public Stream<? extends Arguments> provideArguments(
      ParameterDeclarations parameters, ExtensionContext ctx) {
    FileData data = ctx.getTestMethod()
        .flatMap(m -> Optional.ofNullable(m.getAnnotation(FileData.class)))
        .orElseThrow(() -> new IllegalArgumentException("Missing @TestData annotation"));
    // extract type from the first parameter of the test method
    Class<?> scenarioType = ctx.getRequiredTestMethod().getParameterTypes()[0];
    // used to locate test data source related to the test class
    Class<?> testClass = ctx.getRequiredTestClass();
    try {
      return loadData(data, scenarioType, testClass).stream()
          .map(Arguments::of);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load test data for: " + data.path(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> List<T> loadData(FileData data, Class<?> scenarioType,
      Class<?> testClass) throws Exception {
    TestDataLoader<T> loader = (TestDataLoader<T>) data.loader()
        .getDeclaredConstructor(Class.class).newInstance(scenarioType);
    return loader.load(data.path(), testClass);
  }

}
