package brainslug.flow.execution.instance;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceSelector;
import brainslug.util.Option;

import java.util.List;

public interface InstanceStore {
  Option<? extends FlowInstance> findInstance(FlowInstanceSelector instanceSelector);

  List<? extends FlowInstance> findInstances(FlowInstanceSelector instanceSelector);

  FlowInstance createInstance(Identifier definitionId);
}
