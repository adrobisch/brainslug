package brainslug.flow.execution.token;

import brainslug.AbstractExecutionTest;
import brainslug.flow.FlowBuilder;
import brainslug.flow.FlowDefinition;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.context.Registry;
import brainslug.flow.context.Trigger;
import brainslug.flow.execution.CallDefinitionExecutor;
import brainslug.flow.execution.BrainslugExecutionContext;
import brainslug.flow.execution.Execute;
import brainslug.flow.execution.SimpleTask;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.expression.ContextPredicate;
import brainslug.flow.node.TaskDefinition;
import brainslug.flow.node.task.Delegate;
import brainslug.flow.node.task.GoalDefinition;
import brainslug.util.IdUtil;
import org.junit.Test;

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

    TaskNodeExecutor taskNodeExecutor = createTaskNodeExecutor();

    // when:
    BrainslugExecutionContext instance = new BrainslugExecutionContext(new Trigger()
      .definitionId(serviceCallFlow.getId())
      .nodeId(id(TASK))
      .instanceId(id("instance")), registryWithServiceMock());

    taskNodeExecutor.execute(serviceCallFlow.getNode(id(TASK), TaskDefinition.class), instance);

    // then:
    verify(testServiceMock).getString();
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

    TaskNodeExecutor taskNodeExecutor = createTaskNodeExecutor();

    // when:
    BrainslugExecutionContext instance = new BrainslugExecutionContext(new Trigger()
      .definitionId(serviceCallFlow.getId())
      .nodeId(id(TASK))
      .instanceId(id("instance")), registryWithServiceMock());

    taskNodeExecutor.execute(serviceCallFlow.getNode(id(TASK), TaskDefinition.class), instance);

    // then:
    verify(testServiceMock).getString();
    verify(testServiceMock).echo(testServiceMock.getString());
  }

  @Test
  public void supportsHandlerCallDefinitionWithArgumentInjection() {
    // given:
    class TestDelegate implements Delegate {
      @Execute
      public void execute(TestService testService, ExecutionContext context) {
        // then:
        assertThat(testService.getString()).isEqualTo("a String");
        assertThat(context).isNotNull();
      }
    }

    final TestDelegate testDelegate = spy(new TestDelegate());

    FlowDefinition handlerFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START)))
          .execute(task(id(TASK)).delegate(TestDelegate.class))
        .end(event(id(END)));
      }
    }.getDefinition();

    TaskNodeExecutor taskNodeExecutor = createTaskNodeExecutor();

    Registry registry = registryWithServiceMock();
    when(registry.getService(TestDelegate.class)).thenReturn(testDelegate);

    // when:
    BrainslugExecutionContext instance = new BrainslugExecutionContext(new Trigger()
      .definitionId(handlerFlow.getId())
      .nodeId(id(TASK))
      .instanceId(id("instance")), registry);

    taskNodeExecutor.execute(handlerFlow.getNode(id(TASK), TaskDefinition.class), instance);

    // then:
    verify(testDelegate).execute(any(TestService.class), any(ExecutionContext.class));
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
    context.registerService(Delegate.class, delegateInstance);

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
    TaskNodeExecutor taskNodeExecutor = createTaskNodeExecutor();

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
    taskNodeExecutor.execute(asyncTaskFlow.getNode(id(TASK), TaskDefinition.class), new BrainslugExecutionContext(new Trigger()
    .definitionId(asyncTaskFlow.getId())
      .nodeId(id(TASK))
      .instanceId(id("instance")), registryWithServiceMock()));

    // then:
    verify(asyncTriggerScheduler).schedule(new AsyncTrigger()
      .withNodeId(id(TASK))
      .withInstanceId(id("instance"))
      .withDefinitionId(id(ASYNCID)));
  }

  private TaskNodeExecutor createTaskNodeExecutor() {
    return new TaskNodeExecutor(definitionStore, predicateEvaluator, new CallDefinitionExecutor(), asyncTriggerScheduler)
        .withTokenOperations(new TokenOperations(tokenStore));
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
    BrainslugExecutionContext executionContext = new BrainslugExecutionContext(new Trigger()
      .definitionId(goalFlow.getGoalFlow().getId())
      .nodeId(id(TASK)), registryWithServiceMock());

    new TaskNodeExecutor(definitionStore, predicateEvaluator, new CallDefinitionExecutor(), asyncTriggerScheduler)
        .withTokenOperations(new TokenOperations(tokenStore))
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

      when(definitionStore.findById(goalFlow.getId())).thenReturn(goalFlow);

      return this;
    }
  }
}
