package brainslug.flow.execution.token;

import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.marker.EndEvent;
import brainslug.flow.node.marker.IntermediateEvent;
import brainslug.flow.node.marker.StartEvent;

public class StrictEventNode extends DefaultNodeExecutor<EventDefinition> {
  @Override
  public brainslug.flow.execution.FlowNodeExecutionResult execute(EventDefinition event, ExecutionContext execution) {
    if (event.hasMixin(StartEvent.class) || event.hasMixin(EndEvent.class)) {
      return super.execute(event, execution);
    } else if (event.hasMixin(IntermediateEvent.class)) {
      return executeIntermediateEvent(event, execution);
    }
    throw new IllegalArgumentException(String.format("dont know how to execute %s", event));
  }

  private FlowNodeExecutionResult executeIntermediateEvent(EventDefinition event, ExecutionContext execution) {
    // TODO: check for token here
    throw new UnsupportedOperationException("execution of intermediate events not supported yet");
  }
}
