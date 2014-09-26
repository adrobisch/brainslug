package brainslug.util;

public class Preconditions {
  static class AssertionException extends RuntimeException {
    public AssertionException(java.lang.String message) {
      super(message);
    }
  }

  public static void assertNotNull(Object object) {
    if (object == null) {
      throw new AssertionException("object should not be null");
    }
  }
}
