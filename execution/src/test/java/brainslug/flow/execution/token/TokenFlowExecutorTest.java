package brainslug.flow.execution.token;

import brainslug.AbstractExecutionTest;
import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.Identifier;
import brainslug.flow.context.BrainslugContext;
import brainslug.flow.context.ExecutionContext;
import brainslug.flow.context.Trigger;
import brainslug.flow.execution.node.task.SimpleTask;
import brainslug.util.IdUtil;
import org.junit.Test;
import org.mockito.InOrder;

import static brainslug.util.IdUtil.id;
import static brainslug.util.TestId.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TokenFlowExecutorTest extends AbstractExecutionTest {

  @Test
  public void shouldExecuteParallel() {
    // given:
    when(definitionStore.findById(id(PARALLELID))).thenReturn(new FlowBuilder() {
      @Override
      public void define() {

        flowId(id(PARALLELID));

        start(event(id(START))).parallel(id(PARALLEL))
          .execute(task(id(TASK)))
            .and()
          .execute(task(id(TASK2)));
      }
    }.getDefinition());

    TokenFlowExecutor tokenFlowExecutor = tokenFlowExecutorWithMocks();

    // when:
    Trigger trigger = new Trigger().definitionId(id(PARALLELID)).nodeId(id(START));
    Identifier instanceId = tokenFlowExecutor.startFlow(trigger);

    // then:
    InOrder eventOrder = inOrder(tokenFlowExecutor);
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(START)).definitionId(PARALLELID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(PARALLEL)).definitionId(PARALLELID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(TASK)).definitionId(PARALLELID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(TASK2)).definitionId(PARALLELID).instanceId(instanceId));
  }

  @Test
  public void shouldExecuteMerge() {
    // given:
    when(definitionStore.findById(id(MERGEID))).thenReturn(new FlowBuilder() {
      String x = "test";

      @Override
      public void define() {
        flowId(id(MERGEID));

        start(event(id(START)))
          .choice(id(CHOICE))
          .when(eq(constant(x) ,"test")).execute(task(id(TASK)))
          .or()
          .when(eq(constant(x), "test2")).execute(task(id(TASK2)));

        merge(id(MERGE), id(TASK), id(TASK2))
          .end(event(id(END)));
      }

    }.getDefinition());

    TokenFlowExecutor tokenFlowExecutor = tokenFlowExecutorWithMocks();

    // when:
    Trigger trigger = new Trigger().definitionId(id(MERGEID)).nodeId(id(START));
    Identifier instanceId = tokenFlowExecutor.startFlow(trigger);

    // then:
    InOrder eventOrder = inOrder(tokenFlowExecutor);
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(START)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(CHOICE)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(TASK)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(MERGE)).definitionId(MERGEID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(END)).definitionId(MERGEID).instanceId(instanceId));
  }

  @Test
  public void shouldExecuteJoin() {
    // given:
    when(definitionStore.findById(id(JOINID))).thenReturn(new FlowBuilder() {
      @Override
      public void define() {
        flowId(id(JOINID));

        start(event(id(START)))
          .parallel(id(PARALLEL))
          .execute(task(id(TASK)))
            .and()
          .execute(task(id(TASK2)));

        join(id(JOIN), id(TASK), id(TASK2))
          .end(event(id(END)));
      }

    }.getDefinition());

    TokenFlowExecutor tokenFlowExecutor = tokenFlowExecutorWithMocks();

    // when:
    Trigger trigger = new Trigger().definitionId(id(JOINID)).nodeId(id(START));
    Identifier instanceId = tokenFlowExecutor.startFlow(trigger);

    // then:
    InOrder eventOrder = inOrder(tokenFlowExecutor);
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(START)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(PARALLEL)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(TASK)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(JOIN)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(TASK2)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(JOIN)).definitionId(JOINID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(END)).definitionId(JOINID).instanceId(instanceId));
  }

  @Test
  public void shouldEvaluateServiceCallResultInChoice() {
    // given:
    when(definitionStore.findById(id(CHOICEID))).thenReturn(new FlowBuilder() {
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

    }.getDefinition());

    TokenFlowExecutor tokenFlowExecutor = tokenFlowExecutorWithMocks();

    // when:
    Trigger trigger = new Trigger().definitionId(id(CHOICEID)).nodeId(id(START));
    Identifier instanceId = tokenFlowExecutor.startFlow(trigger);

    // then:
    InOrder eventOrder = inOrder(tokenFlowExecutor);
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(START)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(CHOICE)).definitionId(CHOICEID).instanceId(instanceId));
    eventOrder.verify(tokenFlowExecutor).trigger(new Trigger().nodeId(id(TASK)).definitionId(CHOICEID).instanceId(instanceId));
  }

  @Test
  public void shouldPassPropertiesOnStartByTrigger() {
    // given:
    BrainslugContext contextSpy = spy(context);

    FlowBuilder flow = new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
          .execute(task(id("propertyTask"), new SimpleTask() {
            @Override
            public void execute(ExecutionContext context) {
              assertThat(context.getTrigger().getProperty("key", String.class)).isEqualTo("value");
            }
          }));
      }
    };
    contextSpy.addFlowDefinition(flow.getDefinition());

    // when:
    context.trigger(new Trigger().nodeId(IdUtil.id("start")).definitionId(flow.getId()).property("key", "value"));
  }
}
