package io.github.rltyty.jtkit.junit.helper.loader;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import io.github.rltyty.jtkit.junit.helper.annotation.ParseWith;

public abstract class TestDataLoader<T> {

  Constructor<T> ctor;
  List<T> data;
  ParseWith[] argTypes;
  RecordComponent[] components;

  @SuppressWarnings("unchecked")
  public TestDataLoader(Class<T> scenarioType) {
    this.ctor = (Constructor<T>) scenarioType.getDeclaredConstructors()[0];
    this.data = new ArrayList<>();

    this.components = scenarioType.getRecordComponents();
    this.argTypes = new ParseWith[components.length];
    for (int i = 0; i < components.length; i++) {
      ParseWith ann = components[i].getAnnotation(ParseWith.class);
      if (ann == null)
        throw new IllegalStateException(
            "Record component '" + components[i].getName() + "' missing @ParseWith");
      this.argTypes[i] = ann;
    }
  }

  public abstract List<T> load(String path, Class<?> testClass) throws Exception;
}
