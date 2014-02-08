package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.event.Subscriber;
import brainslug.flow.event.TriggerEvent;
import brainslug.flow.model.FlowBuilder;
import brainslug.flow.model.FlowDefinition;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.flow.model.EnumIdentifier.id;
import static brainslug.util.ID.*;
import static brainslug.util.ID.SEQUENCEID;
import static brainslug.util.ID.TASK;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TaskNodeExecutorTest extends AbstractExecutionTest {
  @Test
  public void supportsMethodCallDefinition() {
      // given:
      context.addFlowDefinition(simpleTaskFlow());

      Subscriber subscriber = mock(Subscriber.class);
      context.getEventDispatcher().addSubscriber(subscriber);
      // when:
      context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(SEQUENCEID));

      // then:
      verify(testService).getString();

      InOrder eventOrder = inOrder(subscriber);
      eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).definitionId(SEQUENCEID));
      eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(START)).definitionId(SEQUENCEID));
      eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(END)).sourceNodeId(id(TASK)).definitionId(SEQUENCEID));
      eventOrder.verifyNoMoreInteractions();
  }

  private FlowDefinition simpleTaskFlow() {
    return new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(START))).execute(task(id(TASK)).call(service(TestService.class).method("getString"))).end(event(id(END)));
      }

      @Override
      public String getId() {
        return SEQUENCEID.name();
      }

    }.getDefinition();
  }
}
