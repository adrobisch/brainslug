package brainslug.flow.execution.token;

import brainslug.AbstractExecutionTest;
import brainslug.flow.EnumIdentifier;
import brainslug.flow.FlowBuilder;
import brainslug.flow.Identifier;
import brainslug.flow.context.DefaultBrainslugContext;
import brainslug.flow.context.Trigger;
import brainslug.flow.context.TriggerContext;
import brainslug.flow.execution.FlowExecutor;
import brainslug.flow.listener.EventType;
import brainslug.flow.listener.Listener;
import brainslug.util.IdUtil;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
    Identifier instanceId = context.startFlow(id(PARALLELID), id(START));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(START)).definitionId(PARALLELID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(PARALLEL)).definitionId(PARALLELID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK)).definitionId(PARALLELID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK2)).definitionId(PARALLELID).instanceId(instanceId));
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
          .when(eq(constant(x) ,"test")).execute(task(id(TASK)))
          .or()
          .when(eq(constant(x), "test2")).execute(task(id(TASK2)));

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
    Identifier instanceId = context.startFlow(id(MERGEID), id(START));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(START)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(CHOICE)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(MERGE)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(END)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteJoin() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
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
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(START)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(PARALLEL)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(JOIN)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK2)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(JOIN)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(END)).definitionId(JOINID).instanceId(instanceId));

    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldEvaluateServiceCallResultInChoice() {
    // given:
    context.addFlowDefinition(new FlowBuilder() {
      String x = "test";

      @Override
      public void define() {
        start(event(id(START))).choice(id(CHOICE))
          .when(resultOf(method(TestService.class).name("getString"))
            .isEqualTo("a String"))
          .execute(task(id(TASK)))
            .or()
          .when(eq(constant(x), "test2")).execute(task(id(TASK2)));
      }

      @Override
      public String getId() {
        return CHOICEID.name();
      }

    }.getDefinition());

    Listener listener = mock(Listener.class);
    context.getListenerManager().addListener(EventType.BEFORE_EXECUTION, listener);
    // when:
    Identifier instanceId = context.startFlow(id(CHOICEID), id(START));
    // then:
    InOrder eventOrder = inOrder(listener);
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(START)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(CHOICE)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(listener).notify(new Trigger().nodeId(id(TASK)).definitionId(CHOICEID).instanceId(instanceId));

    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldPassPropertiesOnStartByTrigger() {
    // given:
    DefaultBrainslugContext contextSpy = spy(context);

    FlowBuilder flow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")));
      }
    };
    contextSpy.addFlowDefinition(flow.getDefinition());
    FlowExecutor executor = spy(context.getFlowExecutor());

    // when:
    executor.startFlow(new Trigger().nodeId(IdUtil.id("start")).definitionId(flow.getId()).property("key", "value"));
    ArgumentCaptor<TriggerContext> triggerCaptor = ArgumentCaptor.forClass(TriggerContext.class);

    // then:
    verify(executor).trigger(triggerCaptor.capture());
    assertThat(triggerCaptor.getValue().getProperties().get("key", String.class)).isEqualTo("value");
  }
}
