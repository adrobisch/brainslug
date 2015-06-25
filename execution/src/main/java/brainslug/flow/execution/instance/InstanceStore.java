package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.InstanceSelector;
import brainslug.util.Option;

import java.util.List;

public interface InstanceStore {
  List<? extends FlowInstance> findInstances(InstanceSelector instanceSelector);

  Option<? extends FlowInstance> findInstance(InstanceSelector instanceSelector);

  FlowInstance createInstance(Identifier definitionId);
}
