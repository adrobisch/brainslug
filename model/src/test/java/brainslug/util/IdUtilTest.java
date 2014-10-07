package brainslug.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class IdUtilTest {
  @Test
  public void shouldReturnNullOnNullArgument() {
    String nullString = null;

    assertNull(IdUtil.id(nullString));
  }
}