package brainslug.flow.execution.token;

import brainslug.AbstractExecutionTest;
import brainslug.flow.FlowBuilder;
import brainslug.flow.FlowDefinition;
import brainslug.flow.Identifier;
import brainslug.flow.context.BrainslugContextBuilder;
import brainslug.flow.context.DefaultBrainslugContext;
import brainslug.flow.context.ExecutionProperties;
import brainslug.flow.context.Trigger;
import brainslug.flow.execution.DefaultExecutionContext;
import brainslug.flow.execution.DefaultExecutionProperties;
import brainslug.flow.execution.FlowNodeExecutionResult;
import brainslug.flow.execution.FlowNodeExecutor;
import brainslug.flow.execution.expression.PropertyPredicate;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.flow.node.ChoiceDefinition;
import brainslug.util.IdUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class ChoiceNodeExecutorTest extends AbstractExecutionTest {

  @Test
  public void shouldExecuteChoiceTruePath() {
    // given:
    Listener listener = choiceFlowWithListener();
    // when:

    Identifier instanceId = context.startFlow(id(CHOICEID), id(START), DefaultExecutionProperties.with("foo", "bar"));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(START)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(CHOICE)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteChoiceFalsePath() {
    // given:
    Listener listener = choiceFlowWithListener();
    // when:

    Identifier instanceId = context.startFlow(id(CHOICEID), id(START), DefaultExecutionProperties.with("foo", "oof"));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(START)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(CHOICE)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK2)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldEvaluatePropertyPredicate() {
    // given:
    DefaultBrainslugContext brainslugContext = new BrainslugContextBuilder().build();
    FlowNodeExecutor<ChoiceDefinition> choiceNodeExecutor = new ChoiceNodeExecutor(context.getPredicateEvaluator()).withTokenOperations(new TokenOperations(brainslugContext.getTokenStore()));
    DefaultExecutionContext executionContext = new DefaultExecutionContext(new Trigger(), context.getRegistry());

    FlowDefinition flowDefinition = propertyPredicateFlow(true);
    // when:
    FlowNodeExecutionResult executionResult = choiceNodeExecutor.execute(flowDefinition.getNode(IdUtil.id("choice"), ChoiceDefinition.class), executionContext);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .hasSize(1)
      .contains(flowDefinition.getNode(id("task")));
  }

  @Test
  public void shouldTakeOtherwisePathIfNoneMatches() {
    // given:
    DefaultBrainslugContext brainslugContext = new BrainslugContextBuilder().build();
    FlowNodeExecutor<ChoiceDefinition> choiceNodeExecutor = new ChoiceNodeExecutor(context.getPredicateEvaluator()).withTokenOperations(new TokenOperations(brainslugContext.getTokenStore()));
    DefaultExecutionContext executionContext = new DefaultExecutionContext(new Trigger(), context.getRegistry());

    FlowDefinition flowDefinition = propertyPredicateFlow(false);

    // when:
    FlowNodeExecutionResult executionResult = choiceNodeExecutor.execute(flowDefinition.getNode(IdUtil.id("choice"), ChoiceDefinition.class), executionContext);

    // then:
    Assertions.assertThat(executionResult.getNextNodes())
      .hasSize(1)
      .containsOnly(flowDefinition.getNode(id("end")));
  }

  private FlowDefinition propertyPredicateFlow(final boolean predicateFulfilled) {
    return new FlowBuilder(){
      @Override
      public void define() {
        start(id("start")).choice(id("choice"))
          .when(predicate(new PropertyPredicate() {
            @Override
            public boolean isFulfilled(ExecutionProperties executionProperties) {
              return predicateFulfilled;
            }
          })).then().execute(task(id("task")))
        .otherwise().end(id("end"));
      }
    }.getDefinition();
  }

  private Listener choiceFlowWithListener() {
    FlowDefinition definition = choiceFlow();

    context.addFlowDefinition(definition);

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    return listener;
  }

  private FlowDefinition choiceFlow() {
    return new FlowBuilder() {
        @Override
        public void define() {
          start(event(id(START))).choice(id(CHOICE))
            .when(eq(property(id("foo")), "bar")).execute(task(id(TASK)))
            .or()
            .when(eq(property(id("foo")), "oof")).execute(task(id(TASK2)))
            .otherwise()
            .end(id("otherwise"));
        }

        @Override
        public String getId() {
          return CHOICEID.name();
        }

      }.getDefinition();
  }

}
