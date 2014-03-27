package brainslug.flow.execution;

import brainslug.AbstractExecutionTest;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.flow.listener.TriggerContext;
import brainslug.flow.model.EnumIdentifier;
import brainslug.flow.model.FlowBuilder;
import brainslug.flow.model.Identifier;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class TokenFlowExecutorTest extends AbstractExecutionTest {

  @Test
  public void shouldExecuteParallel() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
      @Override
      public void define() {
        start(event(id(START))).parallel(id(PARALLEL))
          .execute(task(id(TASK)))
            .and()
          .execute(task(id(TASK2)));
      }

      @Override
      public String getId() {
        return PARALLELID.name();
      }

    }.getDefinition());

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    context.trigger(new TriggerContext().nodeId(id(START)).definitionId(PARALLELID));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(START)).definitionId(PARALLELID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(PARALLEL)).sourceNodeId(id(START)).definitionId(PARALLELID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).sourceNodeId(id(PARALLEL)).definitionId(PARALLELID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK2)).sourceNodeId(id(PARALLEL)).definitionId(PARALLELID));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteMerge() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
      String x = "test";

      @Override
      public void define() {
        start(event(id(START)))
          .choice(id(CHOICE))
          .when(constant(x).isEqualTo("test")).execute(task(id(TASK)))
          .or()
          .when(constant(x).isEqualTo("test2")).execute(task(id(TASK2)));

        merge(id(MERGE), id(TASK), id(TASK2))
          .end(event(id(END)));
      }

      @Override
      public String getId() {
        return MERGEID.name();
      }

    }.getDefinition());

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    context.trigger(new TriggerContext().nodeId(id(START)).definitionId(MERGEID).property(new String("bla")));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(START)).definitionId(MERGEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(CHOICE)).sourceNodeId(id(START)).definitionId(MERGEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).sourceNodeId(id(CHOICE)).definitionId(MERGEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(MERGE)).sourceNodeId(id(TASK)).definitionId(MERGEID));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(END)).sourceNodeId(id(MERGE)).definitionId(MERGEID));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteJoin() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
      String x = "test";

      @Override
      public void define() {
        start(event(id(START)))
          .parallel(id(PARALLEL))
          .execute(task(id(TASK)))
            .and()
          .execute(task(id(TASK2)));

        join(id(JOIN), id(TASK), id(TASK2))
          .end(event(id(END)));
      }

      @Override
      public String getId() {
        return JOINID.name();
      }

    }.getDefinition());

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    Identifier instanceId = context.startFlow(new EnumIdentifier(JOINID), new EnumIdentifier(START));

    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(START)).sourceNodeId(id(START)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(PARALLEL)).sourceNodeId(id(START)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK)).sourceNodeId(id(PARALLEL)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(JOIN)).sourceNodeId(id(TASK)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(TASK2)).sourceNodeId(id(PARALLEL)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(JOIN)).sourceNodeId(id(TASK2)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new TriggerContext().nodeId(id(END)).sourceNodeId(id(JOIN)).definitionId(JOINID).instanceId(instanceId));

    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldEvalauteServiceCallResultInChoice() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
      String x = "test";

      @Override
      public void define() {
        start(event(id(START))).choice(id(CHOICE))
          .when(resultOf(service(TestService.class).method("getString"))
            .isEqualTo("a String"))
          .execute(task(id(TASK)))
            .or()
          .when(constant(x).isEqualTo("test2")).execute(task(id(TASK2)));
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

  @Test(expected = IllegalArgumentException.class)
  public void shoudOnlyStartFlowWithStartEvent() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {

      @Override
      public void define() {
        start(event(id("start"))).end(event(id("end")));
      }

      @Override
      public String getId() {
        return "startEventTest";
      }
    }.getDefinition());

    // when:
    context.startFlow(id("startEventTest"), id("end"));
  }

  @Test
  public void shouldWaitForTriggerAtIntermediateEvent() {
    // given:
    final Identifier definitionId = id("intermediateEventTest");

    context.addFlowDefinition(new FlowBuilder() {

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
    }.getDefinition());

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);

    // when:
    Identifier instanceId = context.startFlow(definitionId, id(START));

    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new TriggerContext().instanceId(instanceId).nodeId(id(START)).sourceNodeId(id(START)).definitionId(definitionId));
    eventOrder.verify(listener).notify(new TriggerContext().instanceId(instanceId).nodeId(id(TASK)).sourceNodeId(id(START)).definitionId(definitionId));
    eventOrder.verifyNoMoreInteractions();
  }

}
