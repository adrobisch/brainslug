package brainslug.flow.instance;

import brainslug.flow.definition.Identifier;
import brainslug.util.Option;

public interface FlowInstanceToken {
    Identifier getId();

    Identifier getNodeId();

    Option<Identifier> getSourceNodeId();

    Option<Identifier> getInstanceId();

    boolean isDead();

    boolean isFinal();
  }
