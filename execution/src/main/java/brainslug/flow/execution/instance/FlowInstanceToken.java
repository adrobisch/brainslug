package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.util.Option;

public interface FlowInstanceToken {
    Identifier getId();

    Identifier getNodeId();

    Option<Identifier> getSourceNodeId();

    Identifier getInstanceId();

    boolean isDead();

    boolean isFinal();
  }
