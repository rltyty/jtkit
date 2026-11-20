/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.loader;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import io.github.rltyty.jtkit.junit.helper.annotation.ParseWith;
import io.github.rltyty.jtkit.junit.helper.parser.LineParser;
import io.github.rltyty.jtkit.junit.helper.parser.Parser;
import io.github.rltyty.jtkit.junit.helper.parser.Scenario;
import io.github.rltyty.jtkit.junit.helper.parser.ScenarioUtils;
import io.github.rltyty.jtkit.junit.helper.parser.ScenarioUtils.CacheKey;
import io.github.rltyty.jtkit.junit.helper.parser.ScenarioUtils.ScenarioMetadata;

/**
 * Loads test scenarios from a flat text file, one scenario per block of lines.
 * Each record component is read from a single line in declaration order,
 * according to its {@link ParseWith} annotation.
 *
 * <p>Loaded scenarios are cached by file path and scenario type; each call
 * to {@link #load} returns deep copies so tests cannot mutate shared state.
 *
 * @param <T> the {@link Scenario} type this loader produces
 */
public class FlatFileLoader<T extends Scenario> extends TestDataLoader<T> {

  /**
   * Constructs a loader for the given scenario type.
   *
   * @param scenarioType the class of the scenario record to load
   */
  public FlatFileLoader(Class<T> scenarioType) {
    super(scenarioType);
  }

  /**
   * {@inheritDoc}
   *
   * @throws IllegalArgumentException if the resource cannot be found on the classpath
   */
  @Override
  public List<T> load(String path, Class<?> testClass) {
    String fullPath = testClass.getPackageName().replace('.', '/') + "/" + path;
    CacheKey key = new CacheKey(fullPath, scenarioType);
    List<T> data = (List<T>) ScenarioUtils.getOrLoadScenarios(key,
        () -> loadInternal(key.id(), testClass));
    return data.stream().map(ScenarioUtils::copyScenario).toList();
  }

  private List<T> loadInternal(String fullPath, Class<?> testClass) {
    InputStream is = testClass.getClassLoader().getResourceAsStream(fullPath);
    if (is == null) {
      throw new IllegalArgumentException("Resource not found: " + fullPath);
    }
    ScenarioMetadata<T> meta = ScenarioUtils.getOrLoadMetadata(scenarioType);
    ParseWith[] argTypes = meta.argTypes();
    RecordComponent[] comps = meta.comps();
    Constructor<T> ctor = meta.ctor();

    List<T> data = new ArrayList<>();

    try (Scanner sc = new Scanner(is)) {
      LineParser parser = new LineParser(sc);

      boolean done = false;
      while (!done) {
        Object[] args = new Object[argTypes.length];
        for (int i = 0; i < argTypes.length; i++) {
          Object value = Parser.HIGH_PARSERS.get(argTypes[i].value().arity)
              .apply(parser, argTypes[i].value().clazz);
          if (value != null) {
            args[i] = value;
            continue;
          }
          if (i != 0) {
            System.err.println(
                "Broken test data input for component [" + comps[i].getName()
                    + "] (index " + i + ") on line [" + parser.getLn()
                    + "]. Stopping load. " + data.size() + " set(s) loaded.");
          } // else: clean EOF at first component, no more test data sets
          done = true;
          break;
        }
        if (!done) {
          data.add(ctor.newInstance(args));
        }
      }
    } catch (InstantiationException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException(
          "Failed to instantiate scenario " + scenarioType.getSimpleName(), e);
    }

    return data;
  }

}
