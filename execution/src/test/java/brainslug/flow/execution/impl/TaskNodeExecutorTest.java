package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.event.EventPath;
import brainslug.flow.event.Subscriber;
import brainslug.flow.event.TriggerEvent;
import brainslug.flow.execution.Execute;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.TaskHandler;
import brainslug.flow.model.FlowBuilder;
import brainslug.flow.model.FlowDefinition;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.flow.model.EnumIdentifier.id;
import static brainslug.util.ID.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class TaskNodeExecutorTest extends AbstractExecutionTest {
  @Test
  public void supportsMethodCallDefinition() {
    // given:
    FlowDefinition serviceCallFlow = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(START))).execute(task(id(TASK)).call(service(TestService.class).method("getString"))).end(event(id(END)));
      }

    }.getDefinition();

    context.addFlowDefinition(serviceCallFlow);

    Subscriber subscriber = mock(Subscriber.class);
    context.getEventDispatcher().addSubscriber(EventPath.TRIGGER_PATH, subscriber);
    // when:
    context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(serviceCallFlow.getId()));

    // then:
    verify(testService).getString();

    InOrder eventOrder = inOrder(subscriber);
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).definitionId(serviceCallFlow.getId()));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(START)).definitionId(serviceCallFlow.getId()));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(END)).sourceNodeId(id(TASK)).definitionId(serviceCallFlow.getId()));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void supportsHandlerCallDefinitionWithParameterInjection() {
    // given:
    FlowDefinition handlerFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START))).execute(task(id(TASK)).call(handler(new TaskHandler() {
          @Execute
          public void execute(TestService testService1, ExecutionContext context) {
            // then:
            assertThat(testService1.getString()).isEqualTo("a String");
            assertThat(context).isNotNull();
          }
        }))).end(event(id(END)));
      }
    }.getDefinition();

    context.addFlowDefinition(handlerFlow);
    // when:
    context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(handlerFlow.getId()));
  }

}
