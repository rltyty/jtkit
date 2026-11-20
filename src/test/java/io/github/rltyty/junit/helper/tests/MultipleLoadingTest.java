/*
 * Copyright (c) 2026-present RLTYTY
 * Licensed under the MIT License.
 */

package io.github.rltyty.junit.helper.tests;

import java.util.concurrent.atomic.AtomicInteger;
import io.github.rltyty.jtkit.junit.helper.BaseTest;
import io.github.rltyty.jtkit.junit.helper.annotation.FileData;
import io.github.rltyty.jtkit.junit.helper.annotation.ParseWith;
import io.github.rltyty.jtkit.junit.helper.loader.FlatFileLoader;
import io.github.rltyty.jtkit.junit.helper.parser.DataType;
import io.github.rltyty.jtkit.junit.helper.parser.Scenario;

public class MultipleLoadingTest extends BaseTest {
  public record Instrument(
      @ParseWith(DataType.STRING) String name,
      @ParseWith(DataType.STRING) String fame)
      implements Scenario {}

  private static final AtomicInteger ins_num_1 = new AtomicInteger();
  private static final AtomicInteger ins_num_2 = new AtomicInteger();

  @FileData(path = "MultipleLoading.dat", loader = FlatFileLoader.class)
  public void load_same_file_method_1_test(Instrument ins) {
    ins_num_1.getAndIncrement();
    System.out.println(ins_num_1.get());  // 1, 2
  }


  @FileData(path = "MultipleLoading.dat", loader = FlatFileLoader.class)
  public void load_same_file_method_2_test(Instrument ins) {
    ins_num_2.getAndIncrement();
    System.out.println(ins_num_2.get());  // 1, 2
  }
}

