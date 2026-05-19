package io.github.rltyty.jtkit.junit.helper.loader;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import io.github.rltyty.jtkit.junit.helper.parser.LineParser;
import io.github.rltyty.jtkit.junit.helper.parser.Parser;

public class FlatFileLoader<T> extends TestDataLoader<T> {

  public FlatFileLoader(Class<T> scenarioType) {
    super(scenarioType);
  }

  @Override
  public List<T> load(String path, Class<?> testClass) throws Exception {
    String fullPath = testClass.getPackageName().replace('.', '/') + "/" + path;
    InputStream is = testClass.getClassLoader().getResourceAsStream(fullPath);
    if (is == null) {
      throw new IllegalArgumentException("Resource not found: " + fullPath);
    }

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
            System.err.println("Broken test data input for component ["
                + components[i].getName() + "] (index " + i + ") on line ["
                + parser.getLn() + "]. Stopping load. " + data.size()
                + " set(s) loaded.");
          } // else: clean EOF at first component, no more test data sets
          done = true;
          break;
        }
        if (!done) {
          data.add(ctor.newInstance(args));
        }
      }
    }
    return data;
  }

}
