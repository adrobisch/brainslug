package brainslug.flow.execution.impl;

import brainslug.AbstractExecutionTest;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.flow.execution.TriggerContext;
import brainslug.flow.model.FlowBuilder;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static brainslug.util.TestId.CHOICE;
import static brainslug.util.TestId.CHOICEID;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class ChoiceNodeExecutorTest extends AbstractExecutionTest {

  @Test
  public void shouldEvaluateEqualDefinition() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
      String x = "test";

      @Override
      public void define() {
        start(event(id(START))).choice(id(CHOICE))
          .when(expression(x).isEqualTo("test")).execute(task(id(TASK)))
          .or()
          .when(expression(x).isEqualTo("test2")).execute(task(id(TASK2)));
      }

      @Override
      public String getId() {
        return CHOICEID.name();
      }

    }.getDefinition());

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    context.trigger(new TriggerContext().nodeId(id(START)).definitionId(CHOICEID));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(START)).sourceNodeId(null).definitionId(CHOICEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(CHOICE)).sourceNodeId(id(START)).definitionId(CHOICEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).sourceNodeId(id(CHOICE)).definitionId(CHOICEID));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldEvaluatePropertyDefinition() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START))).choice(id(CHOICE))
          .when(property(id("foo")).isEqualTo("bar")).execute(task(id(TASK)))
            .or()
          .when(property(id("foo")).isEqualTo("oof")).execute(task(id(TASK2)));
      }

      @Override
      public String getId() {
        return CHOICEID.name();
      }

    }.getDefinition());

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    context.trigger(new TriggerContext().nodeId(id(START)).definitionId(CHOICEID).property("foo", "bar"));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(START)).sourceNodeId(null).definitionId(CHOICEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(CHOICE)).sourceNodeId(id(START)).definitionId(CHOICEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).sourceNodeId(id(CHOICE)).definitionId(CHOICEID));
    eventOrder.verifyNoMoreInteractions();
  }

}
