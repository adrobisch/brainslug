package brainslug.flow.execution.impl;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.model.EventDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.marker.EndEvent;
import brainslug.flow.model.marker.IntermediateEvent;
import brainslug.flow.model.marker.StartEvent;

import java.util.List;

public class EventNodeExecutor extends DefaultNodeExecutor<EventDefinition> {
  @Override
  public List<FlowNodeDefinition> execute(EventDefinition event, ExecutionContext execution) {
    if (event.hasMixin(StartEvent.class) || event.hasMixin(EndEvent.class)) {
      return super.execute(event, execution);
    } else if (event.hasMixin(IntermediateEvent.class)) {
      return executeIntermediateEvent(event, execution);
    }
    throw new IllegalArgumentException(String.format("dont know how to execute %s", event));
  }

  private List<FlowNodeDefinition> executeIntermediateEvent(EventDefinition event, ExecutionContext execution) {
    throw new UnsupportedOperationException("execution of intermediate events not supported yet");
  }
}
