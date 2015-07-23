package brainslug.flow.context;

import brainslug.flow.definition.Identifier;
import brainslug.flow.instance.FlowInstance;
import brainslug.flow.instance.FlowInstanceProperties;
import brainslug.flow.instance.FlowInstanceProperty;

public interface TriggerContext {
  Identifier getDefinitionId();

  Identifier getInstanceId();

  Identifier getNodeId();

  void setProperty(String key, Object value);

  void setProperties(FlowInstanceProperties executionProperties);

  <P> P getProperty(String key, Class<P> type);

  FlowInstanceProperties<?, FlowInstanceProperty<?>> getProperties();

  /**
   * @return true if this trigger should enable async node execution,
   * meaning that async nodes {@link brainslug.flow.node.task.AbstractTaskDefinition#async(boolean)}
   * are executed and not scheduled
   */
  Boolean isAsync();

  /**
   * @return true if this trigger should signal events,
   * meaning that the execution of waiting events {@link brainslug.flow.node.event.IntermediateEvent}
   * is executed and not skipped
   */
  Boolean isSignaling();
}
