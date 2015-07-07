package brainslug.util;

import brainslug.flow.definition.EnumIdentifier;
import brainslug.flow.definition.Identifier;
import brainslug.flow.definition.StringIdentifier;

public class IdUtil {
  public static Identifier<?> id(Enum idValue) {
    if (idValue == null) {
      return null;
    }
    return new EnumIdentifier(idValue);
  }

  public static Identifier<?> id(String idValue) {
    if (idValue == null) {
      return null;
    }
    return new StringIdentifier(idValue);
  }
}
