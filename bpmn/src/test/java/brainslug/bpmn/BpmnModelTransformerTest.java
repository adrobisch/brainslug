package brainslug.bpmn;

import brainslug.flow.FlowBuilder;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static brainslug.util.FlowElementAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;

public class BpmnModelTransformerTest {

  @Test
  public void shouldTransformEmptyFlow() {
    // GIVEN:
    FlowBuilder emptyFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {}

      @Override
      public String getName() {
        return "emptyProcess";
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(emptyFlow);
    // THEN:
    assertThat(bpmnModel.getProcesses()).hasSize(1);
    Process process = bpmnModel.getProcesses().get(0);
    assertThat(process.getFlowElements()).hasSize(0);
    assertThat(process.getId()).isEqualTo(emptyFlow.getDefinition().getId().toString());
    assertThat(process.getName()).isEqualTo(emptyFlow.getDefinition().getName());
    assertThat(process.isExecutable()).isTrue();
  }

  @Test
  public void shouldTransformStartEvent() {
    // GIVEN:
    FlowBuilder startEventFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")).display("foo"));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(startEventFlow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);

    assertThat(process.getFlowElements()).hasSize(1);
    assertThat(process.getFlowElements()).hasFlowElement(StartEvent.class, "start", "foo");
  }

  @Test
  public void shouldTransformEndEvent() {
    // GIVEN:
    FlowBuilder startEventFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start"))).end(event(id("end")).display("End"));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(startEventFlow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);

    assertThat(process.getFlowElements()).hasSize(3);
    assertThat(process.getFlowElements()).hasFlowElement(EndEvent.class, "end", "End");
  }

  @Test
  public void shouldTransformSequenceFlow() {
    // GIVEN:
    FlowBuilder startEventFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start"))).end(event(id("end")));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(startEventFlow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);

    assertThat(process.getFlowElements()).hasSize(3);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(StartEvent.class, 1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(SequenceFlow.class, 1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(EndEvent.class, 1);

    assertThat(process.getFlowElements()).hasSequenceFlow("start", "end");
  }

  @Test
  public void shouldTransformServiceTask() {
    // GIVEN:
    FlowBuilder taskFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(serviceTask(id("task")).delegate(Object.class).display("Task"));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(taskFlow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);
    ArrayList<FlowElement> flowElements = new ArrayList<FlowElement>(process.getFlowElements());

    assertThat(process.getFlowElements()).hasSize(1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(Task.class, 1);
    assertThat(process.getFlowElements()).hasFlowElement(ServiceTask.class, "task", "Task");

    ServiceTask serviceTask = (ServiceTask) flowElements.get(0);
    assertThat(serviceTask.getImplementation()).isEqualTo("java.lang.Object");
    assertThat(serviceTask.getImplementationType()).isEqualTo("class");
  }

  @Test
  public void shouldTransformUserTask() {
    // GIVEN:
    FlowBuilder taskFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(userTask(id("task")).assignee("slug").display("User Task"));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(taskFlow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);
    ArrayList<FlowElement> flowElements = new ArrayList<FlowElement>(process.getFlowElements());

    assertThat(process.getFlowElements()).hasSize(1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(Task.class, 1);
    assertThat(process.getFlowElements()).hasFlowElement(UserTask.class, "task", "User Task");

    UserTask userTask = (UserTask) flowElements.get(0);
    assertThat(userTask.getAssignee()).isEqualTo("slug");
  }

  @Test
  public void shouldTransformWaitEvent() {
    // GIVEN:
    FlowBuilder startEventFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start"))).waitFor(event(id("intermediate")).display("Something happened")).end(event(id("end")));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(startEventFlow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);

    assertThat(process.getFlowElements()).hasSize(5);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(SequenceFlow.class, 2);
    assertThat(process.getFlowElements()).hasFlowElement(IntermediateCatchEvent.class, "intermediate", "Something happened");
    assertThat(process.getFlowElements()).hasSequenceFlow("start", "intermediate");
    assertThat(process.getFlowElements()).hasSequenceFlow("intermediate", "end");
  }

  @Test
  public void shouldTransformChoiceWithJuelAndMerge() {
    // GIVEN:
    FlowBuilder choiceFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .choice(id("choice")).display("Foo or Bar")
            .when(juel("foo == '42'").isTrue()).then()
            .execute(userTask(id("task1")).display("Task 1"))
              .or()
            .when(isTrue(expression("bar"))).then()
            .execute(userTask(id("task2")).display("Task 2"));
        merge(id("merge"), id("task1"), id("task2")).end(event(id("end")));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(choiceFlow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);

    assertThat(process.getFlowElements()).hasSize(12);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(ExclusiveGateway.class, 2);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(SequenceFlow.class, 6);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(StartEvent.class, 1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(EndEvent.class, 1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(Task.class, 2);

    assertThat(process.getFlowElements()).hasFlowElement(ExclusiveGateway.class, "choice", "Foo or Bar");

    assertThat(process.getFlowElements()).hasSequenceFlow("start", "choice");
    assertThat(process.getFlowElements()).hasSequenceFlowWithExpression("choice", "task1", "${foo == '42'}");
    assertThat(process.getFlowElements()).hasSequenceFlowWithExpression("choice", "task2", "bar");
    assertThat(process.getFlowElements()).hasSequenceFlow("task1", "merge");
    assertThat(process.getFlowElements()).hasSequenceFlow("task2", "merge");
    assertThat(process.getFlowElements()).hasSequenceFlow("merge", "end");
  }

  @Test
  public void shouldTransformParallelAndJoin() {
    // GIVEN:
    FlowBuilder flow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .parallel(id("parallel"))
            .execute(userTask(id("task1")).display("Task 1"))
              .and()
            .execute(userTask(id("task2")).display("Task 2"));
        join(id("join"), id("task1"), id("task2")).end(event(id("end")));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(flow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);

    assertThat(process.getFlowElements()).hasSize(12);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(ParallelGateway.class, 2);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(SequenceFlow.class, 6);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(StartEvent.class, 1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(EndEvent.class, 1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(Task.class, 2);

    assertThat(process.getFlowElements()).hasFlowElement(ParallelGateway.class, "parallel", null);

    assertThat(process.getFlowElements()).hasSequenceFlow("start", "parallel");
    assertThat(process.getFlowElements()).hasSequenceFlow("parallel", "task1");
    assertThat(process.getFlowElements()).hasSequenceFlow("parallel", "task2");
    assertThat(process.getFlowElements()).hasSequenceFlow("task1", "join");
    assertThat(process.getFlowElements()).hasSequenceFlow("task2", "join");
    assertThat(process.getFlowElements()).hasSequenceFlow("join", "end");
  }

  @Test
  public void shouldTransformMerge() {
    // GIVEN:
    FlowBuilder flow = new BpmnFlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .choice(id("choice")).display("Foo or Bar")
            .when(isTrue(expression("foo"))).then()
              .execute(userTask(id("task1")).display("Task 1"))
                .or()
              .when(isTrue(expression("bar"))).then()
            .execute(userTask(id("task2")).display("Task 2"));
        merge(id("merge"), id("task1"), id("task2"));
      }
    };

    BpmnModelTransformer modelTransformer = new BpmnModelTransformer();
    // WHEN:
    BpmnModel bpmnModel = modelTransformer.toBpmnModel(flow);
    // THEN:
    Process process = bpmnModel.getProcesses().get(0);

    assertThat(process.getFlowElements()).hasSize(10);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(ExclusiveGateway.class, 2);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(SequenceFlow.class, 5);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(StartEvent.class, 1);
    assertThat(process.getFlowElements()).hasFlowElementsWithType(Task.class, 2);


    assertThat(process.getFlowElements()).hasFlowElement(ExclusiveGateway.class, "merge", "");
    assertThat(process.getFlowElements()).hasSequenceFlow("task1", "merge");
    assertThat(process.getFlowElements()).hasSequenceFlow("task2", "merge");
  }

  @Test
  public void shouldTransformToBpmnXmlString() throws IOException {
    // given:
    BpmnModelTransformer bpmnModelTransformer = new BpmnModelTransformer();
    FlowBuilder emptyFlow = new BpmnFlowBuilder() {
      @Override
      public void define() {}

      @Override
      public String getId() {
        return "emptyFlow";
      }
    };
    // when:
    String bpmnXml = bpmnModelTransformer.toBpmnXml(emptyFlow);
    // then:
    assertThat(bpmnXml).isEqualTo(emptyFlowContent());
  }

  private String emptyFlowContent() throws IOException {
    return IOUtils.toString(getClass().getClassLoader().getResourceAsStream("emptyFlow.bpmn"), "UTF-8");
  }

  private void writeBpmnXml(BpmnModel bpmnModel)  {
    try {
      FileUtils.copyInputStreamToFile(bpmn20Stream(bpmnModel), new File("target/diagram.bpmn.xml"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  InputStream bpmn20Stream(BpmnModel bpmnModel) {
    BpmnXMLConverter bpmnXMLConverter = new BpmnXMLConverter();
    try {
      String bpmn20Xml = new String(bpmnXMLConverter.convertToXML(bpmnModel), "UTF-8");
      return new ByteArrayInputStream(bpmn20Xml.getBytes("UTF-8"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
