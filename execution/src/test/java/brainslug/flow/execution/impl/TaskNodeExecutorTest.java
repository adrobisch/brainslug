package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.execution.AsyncTaskScheduler;
import brainslug.flow.model.Task;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.execution.Execute;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.model.FlowBuilder;
import brainslug.flow.model.FlowDefinition;
import brainslug.flow.model.Identifier;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static org.assertj.core.api.Assertions.assertThat;
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
    Identifier instanceId = context.startFlow(serviceCallFlow.getId(), id(START));

    // then:
    verify(testService).getString();

    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(START)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).sourceNodeId(id(START)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(END)).sourceNodeId(id(TASK)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void supportsHandlerCallDefinitionWithParameterInjection() {
    // given:
    final Task testHandler = new Task() {
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
          .execute(task(id(TASK), testHandler))
        .end(event(id(END)));
      }
    }.getDefinition();

    context.addFlowDefinition(handlerFlow);
    // when:
    context.startFlow(handlerFlow.getId(), id(START));
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

  @Test
  public void asyncTaskIsDelegatedToScheduler() {
    // given:
    AsyncTaskScheduler asyncTaskSchedulerMock = mock(AsyncTaskScheduler.class);
    context.withAsyncTaskScheduler(asyncTaskSchedulerMock);

    FlowDefinition asyncTaskFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START)))
          .execute(task(id(TASK)).async(true))
          .end(event(id(END)));
      }

      @Override
      public String getId() {
        return id(ASYNCID).stringValue();
      }

    }.getDefinition();

    context.addFlowDefinition(asyncTaskFlow);
    // when:
    Identifier instanceId = context.startFlow(asyncTaskFlow.getId(), id(START));
    // then:
    verify(asyncTaskSchedulerMock).scheduleTask(id(TASK), id(START), instanceId, id(ASYNCID));
  }

}
