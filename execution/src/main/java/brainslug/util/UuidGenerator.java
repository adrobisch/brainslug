package brainslug.util;

import brainslug.flow.context.IdGenerator;
import brainslug.flow.model.Identifier;
import brainslug.flow.model.StringIdentifier;

import java.util.UUID;

public class UuidGenerator implements IdGenerator {
  @Override
  public Identifier generateId() {
    return new StringIdentifier(UUID.randomUUID().toString());
  }
}
