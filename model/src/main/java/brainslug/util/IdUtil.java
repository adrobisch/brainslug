package brainslug.util;

import brainslug.flow.model.EnumIdentifier;
import brainslug.flow.model.Identifier;
import brainslug.flow.model.StringIdentifier;

public class IdUtil {
  public static Identifier id(Enum idValue) {
    return new EnumIdentifier(idValue);
  }

  public static Identifier id(String idValue) {
    return new StringIdentifier(idValue);
  }
}
