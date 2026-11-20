/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.jtkit.junit.helper.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import io.github.rltyty.jtkit.junit.helper.parser.DataType;

/**
 * Declares the {@link DataType} to use when parsing a scenario record component.
 * Applied to record components to drive {@link io.github.rltyty.jtkit.junit.helper.parser.Parser}.
 */
@Target(ElementType.RECORD_COMPONENT) 
@Retention(RetentionPolicy.RUNTIME)
public @interface ParseWith {
  /**
   * The data type governing how this component is parsed from the input source.
   *
   * @return the data type
   */
  DataType value();
}
