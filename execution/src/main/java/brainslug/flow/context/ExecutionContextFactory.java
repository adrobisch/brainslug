package brainslug.flow.context;

import brainslug.flow.execution.property.store.PropertyStore;
import brainslug.flow.instance.FlowInstanceProperties;

public class ExecutionContextFactory {
  private final PropertyStore propertyStore;
  private final Registry registry;

  public ExecutionContextFactory(PropertyStore propertyStore, Registry registry) {
    this.propertyStore = propertyStore;
    this.registry = registry;
  }

  public ExecutionContext createExecutionContext(TriggerContext trigger) {
    ExecutionContext executionContext = new BrainslugExecutionContext(trigger, registry);

    if (trigger.getInstanceId() != null) {
      executionContext.getTrigger().setProperties(mergeProperties(trigger, executionContext));
    }

    return executionContext;
  }

  protected FlowInstanceProperties mergeProperties(TriggerContext trigger, ExecutionContext executionContext) {
    FlowInstanceProperties properties = propertyStore
      .getProperties(executionContext.getTrigger().getInstanceId());

    properties.withAll(trigger.getProperties());
    return properties;
  }
}