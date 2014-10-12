package brainslug.util;

import brainslug.flow.Identifier;
import brainslug.flow.StringIdentifier;

import java.util.UUID;

public class UuidGenerator implements IdGenerator {
  @Override
  public Identifier generateId() {
    return new StringIdentifier(UUID.randomUUID().toString());
  }
}
