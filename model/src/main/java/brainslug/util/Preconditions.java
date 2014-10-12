package brainslug.util;

public class Preconditions {
  static class AssertionException extends RuntimeException {
    public AssertionException(java.lang.String message) {
      super(message);
    }
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
