package brainslug.flow.execution.node;

import brainslug.AbstractExecutionTest;
import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.builder.FlowBuilderSupport;
import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.flow.context.BrainslugExecutionContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.context.Trigger;
import brainslug.flow.definition.FlowDefinition;
import brainslug.flow.definition.Identifier;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.expression.ContextPredicate;
import brainslug.flow.execution.instance.DefaultFlowInstance;
import brainslug.flow.expression.PredicateExpression;
import brainslug.flow.execution.instance.FlowInstance;
import brainslug.flow.node.EventDefinition;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static org.mockito.Mockito.*;

public class EventNodeExecutorTest extends AbstractExecutionTest {
  @Test
  public void shouldWaitForTriggerAtIntermediateEvent() {
    // given:
    FlowDefinition eventFlow = eventFlow();
    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(eventFlow.getId()), new Trigger(), new BrainslugContextBuilder().build().getRegistry());

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor.execute(eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class), execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes()).isEmpty();
  }

  private FlowInstance instanceMock(Identifier<?> definitionId) {
    return new DefaultFlowInstance(id("instance"), definitionId, propertyStore, tokenStore);
  }

  @Test
  public void shouldContinueOnSignalingTrigger() {
    // given:
    FlowDefinition eventFlow = eventFlow();
    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(eventFlow.getId()), new Trigger().signaling(true), new BrainslugContextBuilder().build().getRegistry());

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor.execute(eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class), execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .containsOnly(eventFlow.getNode(id(TASK2)));
  }

  @Test
  public void shouldContinueOnSignalingTriggerIfPredicateIsFalse() {
    // given:
    FlowDefinition eventFlow = eventFlow();

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(eventFlow.getId()), new Trigger().signaling(true), new BrainslugContextBuilder().build().getRegistry());
    EventDefinition eventDefinitionWithPredicate = eventDefinitionWithPredicate(eventFlow, false);

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(eventDefinitionWithPredicate, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .containsOnly(eventFlow.getNode(id(TASK2)));
  }

  @Test
  public void shouldContinueOnSignalingTriggerIfPredicateIsTrue() {
    // given:
    FlowDefinition eventFlow = eventFlow();

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(eventFlow.getId()), new Trigger().signaling(true), new BrainslugContextBuilder().build().getRegistry());
    EventDefinition eventDefinitionWithPredicate = eventDefinitionWithPredicate(eventFlow, true);

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(eventDefinitionWithPredicate, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .containsOnly(eventFlow.getNode(id(TASK2)));
  }

  @Test
  public void shouldContinueOnNonSignalingTriggerIfPredicateIsTrue() {
    // given:
    FlowDefinition eventFlow = eventFlow();

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(eventFlow.getId()), new Trigger(), new BrainslugContextBuilder().build().getRegistry());
    EventDefinition eventDefinitionWithPredicate = eventDefinitionWithPredicate(eventFlow, true);

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(eventDefinitionWithPredicate, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .containsOnly(eventFlow.getNode(id(TASK2)));
  }

  @Test
  public void shouldNotContinueOnNonSignalingTriggerIfPredicateIsFalse() {
    // given:
    FlowDefinition eventFlow = eventFlow();

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(eventFlow.getId()), new Trigger(), new BrainslugContextBuilder().build().getRegistry());
    EventDefinition eventDefinitionWithPredicate = eventDefinitionWithPredicate(eventFlow, false);

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(eventDefinitionWithPredicate, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes()).isEmpty();
  }

  @Test
  public void shouldStoreAsyncTriggerForElapsedTimerDefinition() {
    // given:
    FlowDefinition eventFlow = timerEventFlow();
    EventDefinition timerEventNode = eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class);
    FlowInstance flowInstance = instanceMock(eventFlow.getId());

    BrainslugExecutionContext execution = new BrainslugExecutionContext(flowInstance, new Trigger(), registryWithServiceMock());

    // when:
    currentTimeIsMocked();
    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(timerEventNode, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes()).isEmpty();

    verify(asyncTriggerStore).storeTrigger(
      new AsyncTrigger()
        .withDefinitionId(eventFlow.getId())
        .withInstanceId(flowInstance.getIdentifier())
        .withNodeId(id(INTERMEDIATE))
        .withDueDate(5042l)
    );
  }

  private void currentTimeIsMocked() {
    when(eventNodeExecutor.getCurrentTime()).thenReturn(42l);
  }

  @Test
  public void shouldContinueElapsedTimerDefinitionOnSignalingTrigger () {
    // given:
    FlowDefinition eventFlow = timerEventFlow();
    EventDefinition timerEventNode = eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class);

    Trigger signalingTrigger = new Trigger().signaling(true);

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(eventFlow.getId()),signalingTrigger, registryWithServiceMock());

    // when:
    currentTimeIsMocked();

    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(timerEventNode, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .containsOnly(eventFlow.getNode(id(TASK2)));

    verify(asyncTriggerStore, times(0)).storeTrigger(any(AsyncTrigger.class));
  }

  @Test
  public void shouldStoreAsyncTriggerForConditionalEventDefinition() {
    // given:
    ConditionalEventSetup eventSetup = new ConditionalEventSetup().create();
    EventDefinition conditionalEvent = eventSetup.getConditionalEvent();
    Identifier eventId = eventSetup.getEventId();
    Identifier definitionId = id("event_flow");
    FlowInstance flowInstance = instanceMock(definitionId);

    BrainslugExecutionContext execution = new BrainslugExecutionContext(flowInstance, new Trigger(), registryWithServiceMock());

    // when:
    currentTimeIsMocked();
    eventNodeExecutor.execute(conditionalEvent, execution);

    // then:
    verify(asyncTriggerStore).storeTrigger(
      new AsyncTrigger()
        .withDefinitionId(definitionId)
        .withInstanceId(flowInstance.getIdentifier())
        .withNodeId(eventId)
        .withDueDate(3042));
  }

  @Test
  public void shouldSetDueDateForConditionalEventWithPollingInterval() {
    // given:
    ConditionalEventSetup eventSetup = new ConditionalEventSetup().create();
    EventDefinition conditionalEvent = eventSetup.getConditionalEvent();
    conditionalEvent.pollingInterval(6, TimeUnit.SECONDS);
    Identifier eventId = eventSetup.getEventId();
    Identifier definitionId = id("event_flow");
    FlowInstance flowInstance = instanceMock(definitionId);

    BrainslugExecutionContext execution = new BrainslugExecutionContext(flowInstance, new Trigger(), registryWithServiceMock());

    // when:
    currentTimeIsMocked();
    eventNodeExecutor.execute(conditionalEvent, execution);

    // then:
    verify(asyncTriggerStore).storeTrigger(
      new AsyncTrigger()
        .withDefinitionId(definitionId)
        .withInstanceId(flowInstance.getIdentifier())
        .withNodeId(eventId)
        .withDueDate(6042));
  }

  @Test
  public void shouldExecuteConditionalEventDefinitionOnAsyncTrigger() {
    // given:
    ConditionalEventSetup eventSetup = new ConditionalEventSetup().create();
    EventDefinition conditionalEvent = eventSetup.getConditionalEvent();

    when(eventSetup.getContextPredicate().isFulfilled(any(ExecutionContext.class))).thenReturn(true);

    Trigger trigger = new Trigger().async(true);

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(id("event_flow")),trigger, registryWithServiceMock());
    // when:
    FlowNodeExecutionResult result = eventNodeExecutor.execute(conditionalEvent, execution);

    // then:
    Assertions.assertThat(result.getNextNodes()).hasSize(1);

    verify(asyncTriggerStore, times(0)).storeTrigger(any(AsyncTrigger.class));
  }

  @Test
  public void shouldRescheduleConditionalEventTriggerIfPredicateNotFulfilled() {
    // given:
    ConditionalEventSetup eventSetup = new ConditionalEventSetup().create();
    EventDefinition conditionalEvent = eventSetup.getConditionalEvent();
    Identifier eventId = eventSetup.getEventId();
    Identifier definitionId = id("event_flow");
    FlowInstance flowInstance = instanceMock(definitionId);

    when(eventSetup.getContextPredicate().isFulfilled(any(ExecutionContext.class))).thenReturn(false);

    Trigger trigger = new Trigger().async(true);

    BrainslugExecutionContext execution = new BrainslugExecutionContext(flowInstance,trigger, registryWithServiceMock());

    // when:
    currentTimeIsMocked();
    FlowNodeExecutionResult result = eventNodeExecutor.execute(conditionalEvent, execution);

    // then:
    Assertions.assertThat(result.getNextNodes()).hasSize(0);

    verify(asyncTriggerStore, times(1)).storeTrigger(
      new AsyncTrigger()
        .withDefinitionId(definitionId)
        .withInstanceId(flowInstance.getIdentifier())
        .withNodeId(eventId)
        .withDueDate(3042));
  }

  @Test
  public void shouldAlwaysExecuteConditionalEventDefinitionOnSignalingTrigger() {
    // given:
    ConditionalEventSetup eventSetup = new ConditionalEventSetup().create();
    EventDefinition conditionalEvent = eventSetup.getConditionalEvent();

    Trigger trigger = new Trigger().signaling(true);

    BrainslugExecutionContext execution = new BrainslugExecutionContext(instanceMock(id("event_flow")),trigger, registryWithServiceMock());
    // when:
    FlowNodeExecutionResult result = eventNodeExecutor.execute(conditionalEvent, execution);

    // then:
    Assertions.assertThat(result.getNextNodes()).hasSize(1);

    verify(asyncTriggerStore, times(0)).storeTrigger(any(AsyncTrigger.class));
  }

  EventNodeExecutor eventNodeExecutor = (EventNodeExecutor) spy(new EventNodeExecutor(asyncTriggerStore, expressionEvaluator));

  private EventDefinition eventDefinitionWithPredicate(FlowDefinition eventFlow, final boolean predicateFulfilled) {
    return eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class).continueIf(new PredicateExpression<ContextPredicate>(
      new ContextPredicate() {
        @Override
        public boolean isFulfilled(ExecutionContext executionContext) {
          return predicateFulfilled;
        }
      }
    ));
  }

  private FlowDefinition eventFlow() {
    final Identifier definitionId = id("eventFlow");

    return new FlowBuilder() {

        @Override
        public void define() {
          start(event(id(START)))
            .execute(task(id(TASK)))
            .waitFor(event(id(INTERMEDIATE)))
            .execute(task(id(TASK2)))
            .end(event(id(END)));
        }

        @Override
        public String getId() {
          return definitionId.stringValue();
        }
      }.getDefinition();
  }

  private FlowDefinition timerEventFlow() {
    final Identifier definitionId = id("timerEventFlow");

    return new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(START)))
          .execute(task(id(TASK)))
          .waitFor(event(id(INTERMEDIATE)).timePassed(5, TimeUnit.SECONDS))
          .execute(task(id(TASK2)));
      }

      @Override
      public String getId() {
        return definitionId.stringValue();
      }
    }.getDefinition();
  }


  private class ConditionalEventSetup {
    private Identifier eventId;
    private EventDefinition conditionalEvent;
    private ContextPredicate contextPredicate;

    public Identifier getEventId() {
      return eventId;
    }

    public EventDefinition getConditionalEvent() {
      return conditionalEvent;
    }

    public ContextPredicate getContextPredicate() {
      return contextPredicate;
    }

    public ConditionalEventSetup create() {
      contextPredicate = mock(ContextPredicate.class);
      when(contextPredicate.isFulfilled(any(ExecutionContext.class))).thenReturn(false);

      eventId = FlowBuilder.id("conditionalEvent");

      conditionalEvent = FlowBuilderSupport
        .event(eventId)
        .condition(FlowBuilderSupport.predicate(contextPredicate));

      conditionalEvent.addOutgoing(FlowBuilderSupport.event(id("end")));
      return this;
    }
  }
}