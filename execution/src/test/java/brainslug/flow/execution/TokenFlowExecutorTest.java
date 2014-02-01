package brainslug.flow.execution;

import brainslug.flow.context.BrainslugContext;
import brainslug.flow.event.Subscriber;
import brainslug.flow.event.TriggerEvent;
import brainslug.flow.model.EnumIdentifier;
import static brainslug.flow.model.EnumIdentifier.id;
import brainslug.flow.model.FlowBuilder;
import brainslug.flow.model.FlowDefinition;
import brainslug.flow.model.Identifier;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.util.ID.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;

public class TokenFlowExecutorTest {

  BrainslugContext context = createContext();

  @Test
  public void shouldExecuteSimpleSequence() {
    // given:
    context.addFlowDefinition(simpleSequence());
    Subscriber subscriber = mock(Subscriber.class);
    context.getEventDispatcher().addSubscriber(subscriber);
    // when:
    context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(SEQUENCEID));

    // then:
    InOrder eventOrder = inOrder(subscriber);
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).definitionId(SEQUENCEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(START)).definitionId(SEQUENCEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(END)).sourceNodeId(id(TASK)).definitionId(SEQUENCEID));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteChoice() {
    // given:
    context.addFlowDefinition(simpleChoice());
    Subscriber subscriber = mock(Subscriber.class);
    context.getEventDispatcher().addSubscriber(subscriber);
    // when:
    context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(CHOICEID));
    // then:
    InOrder eventOrder = inOrder(subscriber);
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).sourceNodeId(null).definitionId(CHOICEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(CHOICE)).sourceNodeId(id(START)).definitionId(CHOICEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(CHOICE)).definitionId(CHOICEID));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteParallel() {
    // given:
    context.addFlowDefinition(simpleParallel());
    Subscriber subscriber = mock(Subscriber.class);
    context.getEventDispatcher().addSubscriber(subscriber);
    // when:
    context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(PARALLELID));
    // then:
    InOrder eventOrder = inOrder(subscriber);
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).definitionId(PARALLELID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(PARALLEL)).sourceNodeId(id(START)).definitionId(PARALLELID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK2)).sourceNodeId(id(PARALLEL)).definitionId(PARALLELID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(PARALLEL)).definitionId(PARALLELID));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteMerge() {
    // given:
    context.addFlowDefinition(simpleMerge());
    Subscriber subscriber = mock(Subscriber.class);
    context.getEventDispatcher().addSubscriber(subscriber);
    // when:
    context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(MERGEID));
    // then:
    InOrder eventOrder = inOrder(subscriber);
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).definitionId(MERGEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(CHOICE)).sourceNodeId(id(START)).definitionId(MERGEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(CHOICE)).definitionId(MERGEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(MERGE)).sourceNodeId(id(TASK)).definitionId(MERGEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(END)).sourceNodeId(id(MERGE)).definitionId(MERGEID));
    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldExecuteJoin() {
    // given:
    context.addFlowDefinition(simpleJoin());
    Subscriber subscriber = mock(Subscriber.class);
    context.getEventDispatcher().addSubscriber(subscriber);
    // when:
    Identifier instanceId = context.startFlow(new EnumIdentifier(JOINID), new EnumIdentifier(START));

    // then:
    InOrder eventOrder = inOrder(subscriber);
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).sourceNodeId(id(START)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(PARALLEL)).sourceNodeId(id(START)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK2)).sourceNodeId(id(PARALLEL)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(JOIN)).sourceNodeId(id(TASK2)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(PARALLEL)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(JOIN)).sourceNodeId(id(TASK)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(END)).sourceNodeId(id(JOIN)).definitionId(JOINID).instanceId(instanceId));

    eventOrder.verifyNoMoreInteractions();
  }

  @Test
  public void shouldEvalauteServiceCallResultInChoice() {
    // given:
    context.addFlowDefinition(serviceCallChoice());
    Subscriber subscriber = mock(Subscriber.class);
    context.getEventDispatcher().addSubscriber(subscriber);
    // when:
    context.trigger(new TriggerEvent().nodeId(id(START)).definitionId(CHOICEID));
    // then:
    InOrder eventOrder = inOrder(subscriber);
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(START)).sourceNodeId(null).definitionId(CHOICEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(CHOICE)).sourceNodeId(id(START)).definitionId(CHOICEID));
    eventOrder.verify(subscriber).notify(new TriggerEvent().nodeId(id(TASK)).sourceNodeId(id(CHOICE)).definitionId(CHOICEID));

    eventOrder.verifyNoMoreInteractions();
  }

  BrainslugContext createContext() {
    BrainslugContext context = new BrainslugContext();
    context.getRegistry().registerService(TestService.class, new TestService());
    return context;
  }

  public FlowDefinition simpleSequence() {
    return new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(START))).execute(task(id(TASK))).end(event(id(END)));
      }

      @Override
      public String getId() {
        return SEQUENCEID.name();
      }

    }.getDefinition();
  }

  public FlowDefinition simpleChoice() {
    return new FlowBuilder() {
      String x = "test";

      @Override
      public void define() {
        start(event(id(START))).choice(id(CHOICE))
          .when(constant(x).isEqualTo("test")).execute(task(id(TASK)))
            .or()
          .when(constant(x).isEqualTo("test2")).execute(task(id(TASK2)));
      }

      @Override
      public String getId() {
        return CHOICEID.name();
      }

    }.getDefinition();
  }

  public FlowDefinition serviceCallChoice() {
    return new FlowBuilder() {
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

    }.getDefinition();
  }

  public FlowDefinition simpleMerge() {
    return new FlowBuilder() {
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

    }.getDefinition();
  }

  public FlowDefinition simpleJoin() {
    return new FlowBuilder() {
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

    }.getDefinition();
  }

  public FlowDefinition simpleParallel() {
    return new FlowBuilder() {
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

    }.getDefinition();
  }

  public static class TestService {
    public String getString() {
      return "a String";
    }
  }
}
