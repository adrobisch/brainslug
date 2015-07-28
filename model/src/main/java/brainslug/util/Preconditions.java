package brainslug.util;

import java.util.Collection;

public class Preconditions {
  static class AssertionException extends RuntimeException {
    public AssertionException(java.lang.String message) {
      super(message);
    }
  }

  public static <T> T singleItem(Collection<T> collection) {
    if (collection.size() == 1) {
      return collection.iterator().next();
    }
    throw new AssertionException("expected collection to have single item: " + collection);
  }

  public static String notEmpty(String input) {
    return notEmpty(input, "string");
  }

  public static String notEmpty(String input, String name) {
    if (notNull(input).trim().isEmpty()) {
      throw new AssertionException(name + " should not be empty");
    }
    return input;
  }

  public static <T> T notNull(T object) {
    return notNull(object, "object");
  }

  public static <T> T notNull(T object, String name) {
    if (object == null) {
      throw new AssertionException(name + " should not be null");
    }
    return object;
  }
}
