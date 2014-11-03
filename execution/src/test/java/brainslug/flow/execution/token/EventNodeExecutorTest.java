package brainslug.flow.execution.token;

import brainslug.flow.FlowBuilder;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.execution.DefaultExecutionContext;
import brainslug.flow.execution.ExecutionContext;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.execution.expression.ContextPredicate;
import brainslug.flow.expression.PredicateDefinition;
import brainslug.flow.node.EventDefinition;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;

public class EventNodeExecutorTest {

  @Test
  public void shouldWaitForTriggerAtIntermediateEvent() {
    // given:
    EventNodeExecutor eventNodeExecutor = new EventNodeExecutor();
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
    EventNodeExecutor eventNodeExecutor = new EventNodeExecutor();
    FlowDefinition eventFlow = eventFlow();
    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext().signaling(true), new BrainslugContext());

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor.execute(eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class), execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .containsOnly(eventFlow.getNode(id(TASK2)));
  }

  @Test
  public void shouldNotContinueOnSignalingTriggerIfPredicateIsFalse() {
    // given:
    EventNodeExecutor eventNodeExecutor = new EventNodeExecutor();
    FlowDefinition eventFlow = eventFlow();

    DefaultExecutionContext execution = new DefaultExecutionContext(new TriggerContext().signaling(true), new BrainslugContext());
    EventDefinition eventDefinitionWithPredicate = eventDefinitionWithPredicate(eventFlow, false);

    // when:
    FlowNodeExecutionResult executionResult = eventNodeExecutor
      .execute(eventDefinitionWithPredicate, execution);

    // then:
    Assertions.assertThat(executionResult.getNextNodes()).isEmpty();
  }

  @Test
  public void shouldContinueOnSignalingTriggerIfPredicateIsTrue() {
    // given:
    EventNodeExecutor eventNodeExecutor = new EventNodeExecutor();
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


  private EventDefinition eventDefinitionWithPredicate(FlowDefinition eventFlow, final boolean predicateFulfilled) {
    return eventFlow.getNode(id(INTERMEDIATE), EventDefinition.class).onlyIf(new PredicateDefinition<ContextPredicate>(
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

}