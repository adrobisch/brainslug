package brainslug.flow.execution.token;

import brainslug.AbstractExecutionTest;
import brainslug.flow.FlowBuilder;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.context.Trigger;
import brainslug.flow.execution.CallDefinitionExecutor;
import brainslug.flow.execution.DefaultExecutionContext;
import brainslug.flow.execution.Execute;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.execution.SimpleTask;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerScheduler;
import brainslug.flow.execution.expression.ContextPredicate;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.flow.node.TaskDefinition;
import brainslug.flow.node.task.Delegate;
import brainslug.flow.node.task.GoalDefinition;
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
          .execute(task(id(TASK)).call(method(TestService.class).name("getString")))
        .end(event(id(END)));
      }

    }.getDefinition();

    context.addFlowDefinition(serviceCallFlow);

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    Identifier instanceId = context.startFlow(serviceCallFlow.getId(), id(START));

    // then:
    verify(testServiceMock).getString();

    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(START)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(END)).definitionId(serviceCallFlow.getId()).instanceId(instanceId));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void proxiedServiceMethodCallDefinition() {
    // given:
    FlowDefinition serviceCallFlow = new FlowBuilder() {

      @Override
      public void define() {
        TestService testService = service(TestService.class);

        start(event(id(START)))
          .execute(task(id(TASK)).call(method(testService.echo(testService.getString()))))
        .end(event(id(END)));

      }

    }.getDefinition();

    context.addFlowDefinition(serviceCallFlow);

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    context.startFlow(serviceCallFlow.getId(), id(START));

    // then:
    verify(testServiceMock).getString();
    verify(testServiceMock).echo(testServiceMock.getString());
  }

  @Test
  public void supportsHandlerCallDefinitionWithArgumentInjection() {
    // given:
    final Delegate testHandler = new Delegate() {
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
          .execute(task(id(TASK)).delegate(Delegate.class))
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

    class TestDelegate implements Delegate {
      @Execute
      public void doSomeThing() {
      }
    }

    TestDelegate delegateInstance = spy(new TestDelegate());
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
    AsyncTriggerScheduler asyncTriggerSchedulerMock = mock(AsyncTriggerScheduler.class);
    TaskNodeExecutor taskNodeExecutor = (TaskNodeExecutor) new TaskNodeExecutor(context.getDefinitionStore(), context.getPredicateEvaluator(), context.getCallDefinitionExecutor(), asyncTriggerSchedulerMock)
      .withTokenOperations(new TokenOperations(context.getTokenStore()));

    FlowDefinition asyncTaskFlow = new FlowBuilder() {
      @Override
      public void define() {
        flowId(id(ASYNCID));

        start(event(id(START)))
          .execute(task(id(TASK)).async(true))
          .end(event(id(END)));
      }

    }.getDefinition();

    context.addFlowDefinition(asyncTaskFlow);
    // when:
    taskNodeExecutor.execute(asyncTaskFlow.getNode(id(TASK), TaskDefinition.class), new DefaultExecutionContext(new Trigger()
    .definitionId(asyncTaskFlow.getId())
      .nodeId(id(TASK))
      .instanceId(id("instance")), context.getRegistry()));
    // then:
    verify(asyncTriggerSchedulerMock).schedule(new AsyncTrigger()
      .withNodeId(id(TASK))
      .withInstanceId(id("instance"))
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
    DefaultExecutionContext executionContext = new DefaultExecutionContext(new Trigger()
      .definitionId(goalFlow.getGoalFlow().getId())
      .nodeId(id(TASK)), context.getRegistry());

    new TaskNodeExecutor(context.getDefinitionStore(), context.getPredicateEvaluator(), new CallDefinitionExecutor(), context.getAsyncTriggerScheduler())
        .withTokenOperations(new TokenOperations(context.getTokenStore()))
        .execute((TaskDefinition) goalFlow.getGoalFlow().getNode(IdUtil.id(TASK)), executionContext);
  }

  private class GoalFlow {
    private SimpleTask simpleTask;
    private ContextPredicate goalCondition;
    private FlowDefinition goalFlow;

    public SimpleTask getSimpleTask() {
      return simpleTask;
    }

    public ContextPredicate getGoalCondition() {
      return goalCondition;
    }

    public FlowDefinition getGoalFlow() {
      return goalFlow;
    }

    public GoalFlow setup() {
      simpleTask = mock(SimpleTask.class);
      goalCondition = mock(ContextPredicate.class);

      goalFlow = new FlowBuilder() {
        @Override
        public void define() {
          GoalDefinition testGoal = goal(id("aGoal")).check(predicate(goalCondition));

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
