package brainslug.flow.execution.token;

import brainslug.flow.FlowBuilder;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.*;
import brainslug.flow.execution.async.AsyncTrigger;
import brainslug.flow.execution.async.AsyncTriggerStore;
import brainslug.flow.execution.expression.ContextPredicate;
import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.EventDefinition;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static org.mockito.Mockito.*;

public class EventNodeExecutorTest {
  AsyncTriggerStore asyncTriggerStoreMock = mock(AsyncTriggerStore.class);

  BrainslugContext brainslugContextWithMock = new BrainslugContext()
    .withAsyncTriggerStore(asyncTriggerStoreMock)
    .withTokenStore(tokenStoreMock());

  EventNodeExecutor eventNodeExecutor = (EventNodeExecutor) spy(new EventNodeExecutor().withTokenStore(brainslugContextWithMock.getTokenStore()));

  TokenStore tokenStoreMock() {
    TokenStore tokenStoreMock = mock(TokenStore.class);
    when(tokenStoreMock.getNodeTokens(any(Identifier.class), any(Identifier.class))).thenReturn(new TokenList());
    return tokenStoreMock;
  }

  @Test
  public void shouldWaitForTriggerAtIntermediateEvent() {
    // given:
    FlowDefinition eventFlow = eventFlow();
    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext(), new BrainslugContext());

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor.execute(eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class), execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes()).isEmpty();
  }

  @Test
  public void shouldContinueOnSignalingTrigger() {
    // given:
    FlowDefinition eventFlow = eventFlow();
    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext().signaling(true), new BrainslugContext());

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

    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext().signaling(true), new BrainslugContext());
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

    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext().signaling(true), new BrainslugContext());
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

    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext(), new BrainslugContext());
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

    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext(), new BrainslugContext());
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

    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext(), brainslugContextWithMock);

    // when:
    when(eventNodeExecutor.getCurrentTime()).thenReturn(42l);
    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(timerEventNode, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes()).isEmpty();

    verify(asyncTriggerStoreMock).storeTrigger(
      new AsyncTrigger()
      .withNodeId(id(INTERMEDIATE))
      .withDueDate(5042l)
    );
  }

  private EventDefinition eventDefinitionWithPredicate(FlowDefinition eventFlow, final boolean predicateFulfilled) {
    return eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class).continueIf(new PredicateDefinition<ContextPredicate>(
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
          .waitFor(event(id(INTERMEDIATE)).elapsedTime(5, TimeUnit.SECONDS))
          .execute(task(id(TASK2)));
      }

      @Override
      public String getId() {
        return definitionId.stringValue();
      }
    }.getDefinition();
  }


}