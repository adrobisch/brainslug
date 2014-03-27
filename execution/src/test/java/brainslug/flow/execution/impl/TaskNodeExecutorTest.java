package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.flow.listener.TriggerContext;
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
import static org.mockito.Mockito.*;

public class TaskNodeExecutorTest extends AbstractExecutionTest {
  @Test
  public void supportsServiceMethodCallDefinition() {
    // given:
    FlowDefinition serviceCallFlow = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(START)))
          .execute(task(id(TASK)).call(service(TestService.class).method("getString")))
        .end(event(id(END)));
      }

    }.getDefinition();

    context.addFlowDefinition(serviceCallFlow);

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    context.trigger(new TriggerContext().nodeId(id(START)).definitionId(serviceCallFlow.getId()));

    // then:
    verify(testService).getString();

    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(START)).definitionId(serviceCallFlow.getId()));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).sourceNodeId(id(START)).definitionId(serviceCallFlow.getId()));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(END)).sourceNodeId(id(TASK)).definitionId(serviceCallFlow.getId()));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void supportsHandlerCallDefinitionWithParameterInjection() {
    // given:
    final TaskHandler testHandler = new TaskHandler() {
      @Execute
      public void execute(TestService testService1, ExecutionContext context) {
        // then:
        assertThat(testService1.getString()).isEqualTo("a String");
        assertThat(context).isNotNull();
      }
    };

    FlowDefinition handlerFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START)))
          .execute(task(id(TASK)).call(handler(testHandler)))
        .end(event(id(END)));
      }
    }.getDefinition();

    context.addFlowDefinition(handlerFlow);
    // when:
    context.trigger(new TriggerContext().nodeId(id(START)).definitionId(handlerFlow.getId()));
  }

  @Test
  public void supportsDelegateClassExecution() {
    // given:

    class Delegate {
      @Execute
      public void doSomeThing() {
      }
    }

    Delegate delegateInstance = spy(new Delegate());
    context.getRegistry().registerService(Delegate.class, delegateInstance);

    FlowDefinition delegateFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START)))
          .execute(task(id(TASK)).delegate(Delegate.class))
        .end(event(id(END)));
      }
    }.getDefinition();

    context.addFlowDefinition(delegateFlow);
    // when:
    context.startFlow(delegateFlow.getId(), id(START));
    // then:
    verify(delegateInstance, times(1)).doSomeThing();
  }



}
