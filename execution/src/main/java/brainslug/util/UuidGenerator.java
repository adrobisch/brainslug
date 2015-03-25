package brainslug.util;

import brainslug.flow.definition.Identifier;
import brainslug.flow.definition.StringIdentifier;

import java.util.UUID;

public class UuidGenerator implements IdGenerator {
  @Override
  public Identifier generateId() {
    return new StringIdentifier(UUID.randomUUID().toString());
  }
}
