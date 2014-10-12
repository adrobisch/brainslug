package brainslug.flow.execution.token;

import brainslug.AbstractExecutionTest;
import brainslug.flow.execution.*;
import brainslug.flow.execution.async.AsyncTask;
import brainslug.flow.execution.async.AsyncTaskScheduler;
import brainslug.flow.*;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.flow.FlowDefinition;
import brainslug.flow.node.task.GoalDefinition;
import brainslug.flow.node.task.Task;
import brainslug.flow.node.TaskDefinition;
import brainslug.util.IdUtil;
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
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(END)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
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

    class Delegate implements Task {
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
    verify(asyncTaskSchedulerMock).scheduleTask(new AsyncTask()
      .withTaskNodeId(id(TASK))
      .withInstanceId(instanceId)
      .withDefinitionId(id(ASYNCID)));
  }

  @Test
  public void taskIsExecutedIfGoalIsNotFulfilled() {
    // given:
    GoalFlow goalFlow = new GoalFlow().setup();
    when(goalFlow.getGoalCondition().isFulfilled(any(ExecutionContext.class))).thenReturn(false);

    // when:
    taskNodeTriggered(goalFlow);

    // then:
    verify(goalFlow.getSimpleTask()).execute(any(ExecutionContext.class));
  }

  @Test
  public void taskIsNotExecutedIfGoalIsAlreadyFulfilled() {
    // given:
    GoalFlow goalFlow = new GoalFlow().setup();
    when(goalFlow.getGoalCondition().isFulfilled(any(ExecutionContext.class))).thenReturn(true);

    // when:
    taskNodeTriggered(goalFlow);

    // then:
    verifyZeroInteractions(goalFlow.getSimpleTask());
  }

  private void taskNodeTriggered(GoalFlow goalFlow) {
    DefaultExecutionContext executionContext = new DefaultExecutionContext(new TriggerContext()
      .definitionId(goalFlow.getGoalFlow().getId())
      .nodeId(id(TASK)), context);

    new TaskNodeExecutor()
        .withTokenStore(context.getTokenStore())
        .execute((TaskDefinition) goalFlow.getGoalFlow().getNode(IdUtil.id(TASK)), executionContext);
  }

  private class GoalFlow {
    private SimpleTask simpleTask;
    private GoalCondition goalCondition;
    private FlowDefinition goalFlow;

    public SimpleTask getSimpleTask() {
      return simpleTask;
    }

    public GoalCondition getGoalCondition() {
      return goalCondition;
    }

    public FlowDefinition getGoalFlow() {
      return goalFlow;
    }

    public GoalFlow setup() {
      simpleTask = mock(SimpleTask.class);
      goalCondition = mock(GoalCondition.class);

      goalFlow = new FlowBuilder() {
        @Override
        public void define() {
          GoalDefinition testGoal = goal(id("aGoal")).check(goalCondition);

          start(event(id(START)))
            .execute(task(id(TASK), simpleTask).goal(testGoal))
            .end(event(id(END)));
        }

        @Override
        public String getId() {
          return id("GOAL").stringValue();
        }

      }.getDefinition();

      context.addFlowDefinition(goalFlow);

      return this;
    }
  }
}
