package brainslug.util;

import brainslug.flow.model.EnumIdentifier;
import brainslug.flow.model.Identifier;
import brainslug.flow.model.StringIdentifier;

public class IdUtil {
  public static Identifier id(Object idValue) {
    if (idValue instanceof Enum) {
      return new EnumIdentifier((Enum) idValue);
    } else if (idValue instanceof String) {
      return new StringIdentifier((String) idValue);
    }
    throw new IllegalArgumentException(String.format("cant create id from value %s", idValue));
  }
}
