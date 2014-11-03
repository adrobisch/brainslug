package brainslug.flow;

import brainslug.flow.node.marker.EndEvent;
import brainslug.flow.node.marker.IntermediateEvent;
import brainslug.flow.node.marker.StartEvent;
import brainslug.flow.node.*;
import brainslug.flow.node.task.GoalPredicate;
import brainslug.flow.node.task.RetryStrategy;
import brainslug.util.IdUtil;
import brainslug.util.Option;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Date;

import static brainslug.util.ID.*;

import static brainslug.util.FlowDefinitionAssert.assertThat;

public class FlowBuilderTest {

  class MyService {
  }

  @Test(expected = IllegalStateException.class)
  public void notUniqueIdThrowsException() {
    FlowBuilder flowBuilder = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(StartEvent))).waitFor(event(id(StartEvent))).end(event(id(End)));
      }
    };
    flowBuilder.getDefinition();
  }

  @Test
  public void buildStartEndFlow() {
    FlowBuilder flowBuilder = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(StartEvent))).end(event(id(End)));
      }
    };

    assertThat(flowBuilder.getDefinition())
      .hasTotalNodes(2)
      .hasTotalEdges(1)
      .hasNodesWithMarker(1, StartEvent.class)
      .hasNodesWithMarker(1, EndEvent.class)
      .hasEdge(StartEvent, End);
  }

  @Test
  public void buildWaitEventFlow() {
    FlowBuilder flowBuilder = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(StartEvent))).waitFor(event(id(WaitEvent))).end(event(id(End)));

        on(id(WaitEvent)).execute(task(id(Task6)).delegate(MyService.class));
      }

    };

    assertThat(flowBuilder.getDefinition())
        .hasTotalNodes(4)
        .hasTotalEdges(3)
        .hasNodesWithMarker(1, IntermediateEvent.class)
        .hasEdge(StartEvent, WaitEvent)
        .hasEdge(WaitEvent, End)
        .hasEdge(WaitEvent, Task6);
  }

  @Test
  public void buildSingleTaskFlow() {
    FlowBuilder flowBuilder = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(StartEvent))).execute(task(id(TestTask))).end(event(id(End)));
      }

    };

    assertThat(flowBuilder.getDefinition())
      .hasTotalNodes(3)
      .hasTotalEdges(2)
      .hasEdge(StartEvent, TestTask)
      .hasEdge(TestTask, End);
  }

  @Test
  public void buildTaskSequence() {
    FlowBuilder flowBuilder = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(StartEvent)))
          .execute(task(id(TestTask)))
          .execute(task(id(SecondTask)))
        .end(event(id(End)));
      }

    };

    assertThat(flowBuilder.getDefinition())
      .hasTotalNodes(4)
      .hasTotalEdges(3)
      .hasNodesWithType(2, TaskDefinition.class)
      .hasNodesWithType(2, EventDefinition.class)
      .hasEdge(StartEvent, TestTask)
      .hasEdge(TestTask, SecondTask)
      .hasEdge(SecondTask, End);
  }


  @Test
  public void buildChoiceWithMergeFlow() {
    FlowBuilder flowBuilder = new FlowBuilder() {
      Integer meaningOfLife = 42;

      @Override
      public void define() {
        start(event(id(StartEvent)))
            .choice(id(Choice))
            .when(constant(meaningOfLife).isEqualTo(42))
              .execute(task(id(SecondTask)))
              .or()
            .when(constant(meaningOfLife).isEqualTo(43))
              .execute(task(id(ThirdTask)));

        merge(id(Merge), id(SecondTask), id(ThirdTask)).end(event(id(EndEvent2)));
      }

    };

    assertThat(flowBuilder.getDefinition())
        .hasTotalNodes(6)
        .hasTotalEdges(6)
        .hasNodesWithType(2, TaskDefinition.class)
        .hasNodesWithType(2, EventDefinition.class)
        .hasNodesWithType(1, ChoiceDefinition.class)
        .hasNodesWithType(1, MergeDefinition.class)
        .hasNodesWithMarker(1, EndEvent.class)
        .hasNodesWithMarker(1, StartEvent.class)
        .hasEdge(StartEvent, Choice)
        .hasEdge(Choice, SecondTask)
        .hasEdge(Choice, ThirdTask)
        .hasEdge(SecondTask, Merge)
        .hasEdge(ThirdTask, Merge)
        .hasEdge(Merge, EndEvent2);
  }

  @Test
  public void buildParallelWithJoinFlow() {
    FlowBuilder flowBuilder = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(StartEvent)))
            .parallel(id(Parallel))
            .execute(task(id(SecondTask)))
              .and()
            .execute(task(id(ThirdTask)));

        join(id(Join), id(SecondTask), id(ThirdTask)).end(event(id(EndEvent2)));
      }

    };

    assertThat(flowBuilder.getDefinition())
        .hasTotalNodes(6)
        .hasTotalEdges(6)
        .hasNodesWithType(2, TaskDefinition.class)
        .hasNodesWithType(2, EventDefinition.class)
        .hasNodesWithType(1, ParallelDefinition.class)
        .hasNodesWithType(1, JoinDefinition.class)
        .hasNodesWithMarker(1, EndEvent.class)
        .hasNodesWithMarker(1, StartEvent.class)
        .hasEdge(StartEvent, Parallel)
        .hasEdge(Parallel, SecondTask)
        .hasEdge(Parallel, ThirdTask)
        .hasEdge(SecondTask, Join)
        .hasEdge(ThirdTask, Join)
        .hasEdge(Join, EndEvent2);
  }

  @Test
  public void connectNodesWithAfter() {
    FlowBuilder flowBuilder = new FlowBuilder() {

      @Override
      public void define() {
        start(event(id(StartEvent))).execute(task(id(TestTask)));

        after(id(TestTask)).execute(task(id(SecondTask)));
      }

    };

    assertThat(flowBuilder.getDefinition())
        .hasTotalNodes(3)
        .hasTotalEdges(2)
        .hasNodesWithType(2, TaskDefinition.class)
        .hasNodesWithType(1, EventDefinition.class)
        .hasEdge(StartEvent, TestTask)
        .hasEdge(TestTask, SecondTask);
  }

  @Test
  public void buildComplexFlow() {
    FlowBuilder flowBuilder = new FlowBuilder() {
      Integer meaningOfLife = 42;

      @Override
      public void define() {
        start(event(id(StartEvent))).execute(task(id(TestTask)));

        after(id(TestTask))
          .choice(id(Choice))
            .when(constant(meaningOfLife).isEqualTo(42))
              .execute(task(id(SecondTask)))
                .then()
              .execute(task(id(FourthTask)))
            .or()
            .when(constant(meaningOfLife).isEqualTo(43))
              .execute(task(id(ThirdTask)))
              .end(event(id(End)));

        after(id(FourthTask))
          .parallel(id(Parallel))
            .execute(task(id(Task5))).end(event(id(EndEvent2)))
              .and()
            .waitFor(event(id(WaitEvent)));

        on(id(WaitEvent)).execute(task(id(Task6)).delegate(MyService.class));
      }

    };

    assertThat(flowBuilder.getDefinition())
      .hasTotalNodes(12)
      .hasTotalEdges(11)
      .hasNodesWithType(6, TaskDefinition.class)
      .hasNodesWithType(4, EventDefinition.class)
      .hasNodesWithType(1, ParallelDefinition.class)
      .hasNodesWithType(1, ChoiceDefinition.class)
      .hasNodesWithMarker(2, EndEvent.class)
      .hasNodesWithMarker(1, StartEvent.class)
      .hasEdge(StartEvent, TestTask)
      .hasEdge(TestTask, Choice)
      .hasEdge(Choice, SecondTask)
      .hasEdge(SecondTask, FourthTask)
      .hasEdge(Choice, ThirdTask)
      .hasEdge(ThirdTask, End)
      .hasEdge(FourthTask, Parallel)
      .hasEdge(Parallel, Task5)
      .hasEdge(Task5, EndEvent2)
      .hasEdge(Parallel, WaitEvent)
      .hasEdge(WaitEvent, Task6);
  }

  @Test
  public void buildFlowWithGoal() {
    FlowDefinition goalFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(id("start"))
          .execute(task(id("simpleTask"))
            .retryAsync(true)
            .goal(id("taskExecuted")))
        .end(id("end"));

        goal(id("taskExecuted")).check(new GoalPredicate<Void>() {
          @Override
          public boolean isFulfilled(Void aVoid) {
            return true;
          }
        });
      }
    }.getDefinition();

    FlowNodeDefinition node = goalFlow.getNode(IdUtil.id("simpleTask"));
    Assertions.assertThat(node).isInstanceOf(TaskDefinition.class);

    TaskDefinition taskNode = (TaskDefinition) node;
    Assertions.assertThat(taskNode.isRetryAsync()).isTrue();
    Assertions.assertThat(taskNode.getGoal()).isEqualTo(Option.of(IdUtil.id("taskExecuted")));
  }

  @Test
  public void buildFlowWithInlineGoal() {
    FlowDefinition goalFlow = new FlowBuilder() {
      @Override
      public void define() {
        start(id("start"))
          .execute(task(id("simpleTask"))
            .goal(check(id("inlineGoal"), new GoalPredicate() {
              @Override
              public boolean isFulfilled(Object o) {
                return false;
              }
            })))
          .end(id("end"));
      }
    }.getDefinition();

    TaskDefinition taskNode = (TaskDefinition) goalFlow.getNode(IdUtil.id("simpleTask"));
    Assertions.assertThat(taskNode.getGoal()).isEqualTo(Option.of(IdUtil.id("inlineGoal")));
  }

  @Test
  public void buildFlowWithRetryStrategy() {
    FlowDefinition goalFlow = new FlowBuilder() {
      @Override
      public void define() {
        RetryStrategy retryStrategy = new RetryStrategy() {

          @Override
          public Date nextRetry(long retryCount, Date baseDate) {
            return new Date();
          }
        };

        start(id("start"))
          .execute(task(id("simpleTask")).retryAsync(true).retryStrategy(retryStrategy))
        .end(id("end"));

      }
    }.getDefinition();

    FlowNodeDefinition node = goalFlow.getNode(IdUtil.id("simpleTask"));
    Assertions.assertThat(node).isInstanceOf(TaskDefinition.class);

    TaskDefinition taskNode = (TaskDefinition) node;
    Assertions.assertThat(taskNode.isRetryAsync()).isTrue();
    Assertions.assertThat(taskNode.getRetryStrategy()).isNotNull();
  }

}
