/*
 * Copyright (c) 2026-present RLTYTY Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.parser;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import io.github.rltyty.jtkit.junit.helper.annotation.ParseWith;

/**
 * Utility class for scenario reflection, caching, and copying. Not
 * instantiable.
 */
public final class ScenarioUtils {

  private ScenarioUtils() {}

  /**
   * Precomputed reflection metadata for a scenario record type, used to avoid
   * repeated reflection lookups during test data loading.
   *
   * @param <T> the scenario type
   * @param ctor the canonical constructor of the scenario record
   * @param comps the record components, retaining name and type information
   * @param argTypes the {@link ParseWith} annotations on each component, used
   *        to drive parsing
   */
  public static record ScenarioMetadata<T extends Scenario>(Constructor<T> ctor,
      RecordComponent[] comps, ParseWith[] argTypes) {
  }

  /**
   * Cache key combining a file path identifier and scenario type, used to avoid
   * reloading the same test data multiple times.
   *
   * @param id the file path identifier
   * @param scenarioType the scenario record class
   */
  public static record CacheKey(String id, Class<?> scenarioType) {
  }

  private static final Map<CacheKey, List<?>> SCENARIO_CACHE =
      new ConcurrentHashMap<>();

  private static final Map<Class<? extends Scenario>, ScenarioMetadata<? extends Scenario>> METADATA_CACHE =
      new ConcurrentHashMap<>();

  /**
   * Returns cached scenarios for {@code key}, loading them via {@code loader}
   * if not already cached.
   *
   * @param <T> the scenario type
   * @param key the cache key
   * @param loader the supplier invoked on cache miss
   * @return the cached or freshly loaded scenario list
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> getOrLoadScenarios(CacheKey key,
      Supplier<List<T>> loader) {
    return (List<T>) SCENARIO_CACHE.computeIfAbsent(key, k -> loader.get());
  }

  /**
   * Returns cached {@link ScenarioMetadata} for {@code scenarioType}, computing
   * and caching it on first access.
   *
   * @param <T> the scenario type
   * @param scenarioType the scenario record class
   * @return the metadata for the given type
   */
  @SuppressWarnings("unchecked")
  public static <T extends Scenario> ScenarioMetadata<T> getOrLoadMetadata(
      Class<T> scenarioType) {

    return (ScenarioMetadata<T>) METADATA_CACHE.computeIfAbsent(scenarioType,
        cls -> buildMetadata(cls));
  }

  private static <T extends Scenario> ScenarioMetadata<T> buildMetadata(
      Class<T> scenarioType) {
    try {
      RecordComponent[] comps = scenarioType.getRecordComponents();
      Class<?>[] types = Arrays.stream(comps).map(RecordComponent::getType)
          .toArray(Class<?>[]::new);
      Constructor<T> ctor = scenarioType.getDeclaredConstructor(types);
      ParseWith[] argTypes = Arrays.stream(comps)
          .map(ScenarioUtils::getParseWith)
          .toArray(ParseWith[]::new);
      return new ScenarioMetadata<>(ctor, comps, argTypes);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Failed to build MetaData: ", e);
    }
  }

  private static ParseWith getParseWith(RecordComponent comp) {
    ParseWith ann = comp.getAnnotation(ParseWith.class);
    if (ann == null)
      throw new IllegalStateException(
          "Record component '" + comp.getName() + "' missing @ParseWith");
    return ann;
  }

  /**
   * Returns a deep copy of {@code scenario} by reconstructing it via its
   * canonical constructor, deep-copying any {@code List} or {@code List<List>}
   * components.
   *
   * @param <T> the scenario type
   * @param scenario the scenario to copy
   * @return a new instance with independently mutable container fields
   */
  public static final <T extends Scenario> T copyScenario(T scenario) {
    try {
      @SuppressWarnings("unchecked")
      Class<T> scenarioClass = (Class<T>) scenario.getClass();
      ScenarioMetadata<T> meta = getOrLoadMetadata(scenarioClass);

      RecordComponent[] comps = meta.comps();

      Object[] args = new Object[comps.length];

      for (int i = 0; i < comps.length; i++) {
        Object value = comps[i].getAccessor().invoke(scenario);
        args[i] = deepCopy(value);
      }

      return (T) meta.ctor().newInstance(args);

    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to clone scenario: " + scenario.getClass().getSimpleName(),
          e);
    }
  }

  private static Object deepCopy(Object value) {
    if (value == null)
      return null;

    // primitive array including String[]
    if (value.getClass().isArray()) {
      int len = Array.getLength(value);
      Object copy = Array.newInstance(value.getClass().getComponentType(), len);
      System.arraycopy(value, 0, copy, 0, len);
      return copy;
    }

    // List<List<?>>
    if (value instanceof List<?> outer && !outer.isEmpty()
        && outer.get(0) instanceof List<?>) {
      List<Object> copy = new ArrayList<>(outer.size());
      for (Object inner : outer) {
        copy.add(new ArrayList<>((List<?>) inner));
      }
      return copy;
    }

    // List<?>
    if (value instanceof List<?> list) {
      return new ArrayList<>(list);
    }

    // boxed primitives, String, enums, etc.
    return value;
  }

}
