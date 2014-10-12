package brainslug.util;

import brainslug.flow.EnumIdentifier;
import brainslug.flow.Identifier;
import brainslug.flow.StringIdentifier;

public class IdUtil {
  public static Identifier id(Enum idValue) {
    if (idValue == null) {
      return null;
    }
    return new EnumIdentifier(idValue);
  }

  public static Identifier id(String idValue) {
    if (idValue == null) {
      return null;
    }
    return new StringIdentifier(idValue);
  }
}
