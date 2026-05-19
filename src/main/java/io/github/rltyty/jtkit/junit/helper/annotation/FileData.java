package io.github.rltyty.jtkit.junit.helper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import io.github.rltyty.jtkit.junit.helper.loader.TestDataLoader;
import io.github.rltyty.jtkit.junit.helper.provider.TestDataProvider;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest(name = "{displayName} [{index}]")
@ArgumentsSource(TestDataProvider.class)
public @interface FileData {
  String path(); // file path

  @SuppressWarnings("rawtypes")
  Class<? extends TestDataLoader> loader(); // annotation elements cannot be
                                            // generic type parameterized
}
