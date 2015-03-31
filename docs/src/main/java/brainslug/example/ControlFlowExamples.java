package brainslug.example;

import brainslug.flow.builder.FlowBuilder;
import brainslug.flow.definition.Identifier;
import brainslug.flow.expression.EqualDefinition;
import brainslug.flow.expression.Expression;
import brainslug.flow.node.EventDefinition;
import brainslug.flow.node.TaskDefinition;

import java.util.concurrent.TimeUnit;

public class ControlFlowExamples {

  static
  //# tag::event-flow[]
  class EventFlow extends FlowBuilder {

    Identifier eventFlowId = id("helloFlow");

    EventDefinition eventFlowStart = event(id("start")).display("Every 5 Seconds");

    EventDefinition fiveSecondsPassed = event(id("wait")).display("After 5 Seconds")
      .timePassed(5, TimeUnit.SECONDS);

    TaskDefinition theTask = task(id("doIt")).display("Do Something");

    @Override
    public void define() {
      flowId(eventFlowId);

      start(eventFlowStart, every(5, TimeUnit.SECONDS))
        .waitFor(fiveSecondsPassed)
        .execute(theTask);
    }
  }
  //# end::event-flow[]

  static
  //# tag::choice-flow[]
  class ChoiceFlow extends FlowBuilder {

    Identifier choiceFlowId = id("choiceFlow");

    EventDefinition choiceFlowStart = event(id("start"));
    EventDefinition choiceEnd = event(id("end")).display("end");

    Identifier meaningOfLiveChoice = id("meaning_choice");
    Expression meaningProperty = property(id("meaning"));

    EqualDefinition<?,?> equalsFortyTwo = eq(meaningProperty, 42);
    EqualDefinition<?,?> equalsFortyThree = eq(meaningProperty, 43);

    TaskDefinition meaningfulTask = task(id("meaning_ful")).display("Meaningful Task");
    TaskDefinition meaninglessTask = task(id("meaning_less")).display("Meaningless Task");

    @Override
    public void define() {
      flowId(choiceFlowId);

      start(choiceFlowStart)
        .choice(meaningOfLiveChoice).display("Meaning of live?")
          .when(equalsFortyTwo)
            .execute(meaningfulTask)
          .or()
          .when(equalsFortyThree)
            .execute(meaninglessTask);

      merge(meaningfulTask, meaninglessTask)
        .end(choiceEnd);
    }
  }
  //# end::choice-flow[]


  static
    //# tag::parallel-flow[]
  class ParallelFlow extends FlowBuilder {

    Identifier parallelFlowId = id("parallel_flow");

    EventDefinition flowStart = event(id("start"));
    EventDefinition flowEnd = event(id("end")).display("end");

    TaskDefinition firstTask = task(id("first_task")).display("Do Something");
    TaskDefinition secondTask = task(id("second_task")).display("Do another Thing");

    @Override
    public void define() {
      flowId(parallelFlowId);

      start(flowStart)
        .parallel(id())
          .execute(firstTask)
            .and()
          .execute(secondTask);

      join(firstTask, secondTask)
        .end(flowEnd);
    }
  }
  //# end::parallel-flow[]

}
