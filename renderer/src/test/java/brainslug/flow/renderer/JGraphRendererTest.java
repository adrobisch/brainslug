package brainslug.flow.renderer;

import brainslug.flow.FlowBuilder;
import brainslug.flow.node.FlowNodeDefinition;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphView;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.FileOutputStream;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class JGraphRendererTest {

  @Test
  public void writesPng() throws IOException {
    // GIVEN:
    JGraphRenderer renderer = new JGraphRenderer(new DefaultSkin());
    FileOutputStream outputStream = mock(FileOutputStream.class);
    // WHEN:
    renderer.render(simpleFlow(), outputStream, Format.PNG);
    // THEN:
    verify(outputStream, atLeastOnce()).write(any(byte[].class), any(Integer.class), any(Integer.class));
  }

  @Test
  public void writesJpg() throws IOException {
    // GIVEN:
    JGraphRenderer renderer = new JGraphRenderer(new DefaultSkin());
    FileOutputStream outputStream = mock(FileOutputStream.class);
    // WHEN:
    renderer.render(simpleFlow(), outputStream, Format.JPG);
    // THEN:
    verify(outputStream, atLeastOnce()).write(any(byte[].class), any(Integer.class), any(Integer.class));
  }

  @Test
  public void supportsTask() throws IOException {
    // GIVEN:
    RendererWithMocks rendererWithMocks = new RendererWithMocks().create();
    FileOutputStream outputStream = mock(FileOutputStream.class);
    // WHEN:
    rendererWithMocks.getRenderer().render(taskFlow(), outputStream, Format.JPG);
    // THEN:
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("start"),
      eq("start"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("task"),
      eq("Task 1"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("task2"),
        eq("Task 2"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("end"),
      eq("end"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "start", "task");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "task", "task2");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "task2", "end");
  }

  @Test
  public void supportsChoice() throws IOException {
    // GIVEN:
    RendererWithMocks rendererWithMocks = new RendererWithMocks().create();
    // WHEN:
    rendererWithMocks.getRenderer()
      .render(choiceFlow(), rendererWithMocks.getOutputStream(), Format.JPG);
    // THEN:
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("start"),
      eq("start"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("choice"),
      eq("Fish or Ships?"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("task2"),
      eq("task2"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("task3"),
      eq("task3"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));

    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "start", "choice");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "Fish", "choice", "task2");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "Ships", "choice", "task3");
  }

  @Test
  public void supportsMerge() throws IOException {
    // GIVEN:
    RendererWithMocks rendererWithMocks = new RendererWithMocks().create();
    // WHEN:
    rendererWithMocks.getRenderer()
        .render(mergeFlow(), rendererWithMocks.getOutputStream(), Format.JPG);
    // THEN:
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("merge"),
        eq(""), eq(0.0), eq(0.0), eq(30.0), eq(30.0));

    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "task2", "merge");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "task3", "merge");
  }

  @Test
  public void supportsJoin() throws IOException {
    // GIVEN:
    RendererWithMocks rendererWithMocks = new RendererWithMocks().create();
    // WHEN:
    rendererWithMocks.getRenderer()
        .render(joinFlow(), rendererWithMocks.getOutputStream(), Format.JPG);
    // THEN:
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("join"),
        eq(""), eq(0.0), eq(0.0), eq(30.0), eq(30.0));

    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "task2", "join");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "task3", "join");
  }

  @Test
  public void supportsParallel() throws IOException {
    // GIVEN:
    RendererWithMocks rendererWithMocks = new RendererWithMocks().create();
    // WHEN:
    rendererWithMocks.getRenderer()
        .render(parallelFlow(), rendererWithMocks.getOutputStream(), Format.JPG);
    // THEN:
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("start"),
        eq("start"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("parallel"),
        eq(""), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("task2"),
        eq("task2"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("task3"),
        eq("task3"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));

    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "start", "parallel");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "parallel", "task2");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "parallel", "task3");
  }

  @Test
  public void supportsIntermediateCatch() throws IOException {
    // GIVEN:
    RendererWithMocks rendererWithMocks = new RendererWithMocks().create();
    // WHEN:
    rendererWithMocks.getRenderer()
        .render(simpleWaitEventFlow(), rendererWithMocks.getOutputStream(), Format.JPG);
    // THEN:
    // THEN:
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("start"),
        eq("start"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("intermediate"),
        eq("Something happened"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertVertex(anyObject(), eq("end"),
        eq("end"), eq(0.0), eq(0.0), eq(30.0), eq(30.0));
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "start", "intermediate");
    verify(rendererWithMocks.getGraph()).insertEdge(null, null, "", "intermediate", "end");
  }

  @Test
  public void canGetLabelForNodesOnThenPath() throws IOException {
    // GIVEN:
    RendererWithMocks rendererWithMocks = new RendererWithMocks().create();
    // WHEN:
    rendererWithMocks.getRenderer()
        .render(afterFlow(), rendererWithMocks.getOutputStream(), Format.JPG);
    // THEN:
  }

  static FlowBuilder simpleFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start"))).end(event(id("end")));
      }
    };
  }

  static FlowBuilder simpleWaitEventFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start"))).waitFor(event(id("intermediate")).display("Something happened")).end(event(id("end")));
      }
    };
  }

  static FlowBuilder taskFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
          .execute(task(id("task")).display("Task 1"))
          .execute(task(id("task2")).display("Task 2"))
          .end(event(id("end")));
      }
    };
  }

  static FlowBuilder choiceFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
          .choice(id("choice")).display("Fish or Ships?")
            .when(isTrue(expression("Fish"))).execute(task(id("task2")))
              .or()
            .when(isTrue(expression("Ships"))).execute(task(id("task3")));
      }
    };
  }

  static FlowBuilder mergeFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .choice(id("choice")).display("Fish or Ships?")
            .when(isTrue(expression("Fish"))).execute(task(id("task2")))
              .or()
            .when(isTrue(expression("Ships"))).execute(task(id("task3")));
        merge(id("merge"), id("task2"), id("task3"));
      }
    };
  }

  static FlowBuilder afterFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .choice(id("choice")).display("Fish or Ships?")
            .when(isTrue(expression("Fish"))).execute(task(id("task2")))
              .or()
            .when(isTrue(expression("Ships"))).execute(task(id("task3")));
        merge(id("merge"), id("task2"), id("task3"));

        after(id("task2")).waitFor(event(id("something"))).end(event(id("end2")));
      }
    };
  }

  static FlowBuilder joinFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .choice(id("choice")).display("Fish or Ships?")
            .when(isTrue(expression("Fish"))).execute(task(id("task2")))
            .or()
            .when(isTrue(expression("Ships"))).execute(task(id("task3")));
        join(id("join"), id("task2"), id("task3")).end(event(id("end")));
      }
    };
  }

  static FlowBuilder parallelFlow() {
    return new FlowBuilder() {
      @Override
      public void define() {
        start(event(id("start")))
            .parallel(id("parallel"))
            .execute(task(id("task2")))
              .and()
            .execute(task(id("task3")));
      }
    };
  }

  private class RendererWithMocks {
    private Skin skin;
    private mxGraph graph;
    private JGraphRenderer renderer;
    private FileOutputStream outputStream;

    public mxGraph getGraph() {
      return graph;
    }

    public RendererWithMocks create() {
      skin = mock(Skin.class);
      graph = mock(mxGraph.class);
      mxIGraphModel model = mock(mxIGraphModel.class);
      mxGraphView view = mock(mxGraphView.class);
      when(graph.getModel()).thenReturn(model);
      when(graph.getGraphBounds()).thenReturn(new mxRectangle(0,0,100,100));
      when(graph.getView()).thenReturn(view);
      when(graph.insertVertex(any(), anyString(),
          anyString(), anyInt(), anyInt(), anyInt(), anyInt())).thenAnswer(new Answer<Object>() {
        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
          return invocation.getArguments()[1];
        }
      });
      when(skin.apply(any(mxGraph.class))).thenReturn(graph);
      when(skin.getNodeSize(any(FlowNodeDefinition.class))).thenReturn(new mxRectangle(0,0,30,30));

      renderer = new JGraphRenderer(skin);
      outputStream = mock(FileOutputStream.class);

      return this;
    }

    public JGraphRenderer getRenderer() {
      return renderer;
    }

    public FileOutputStream getOutputStream() {
      return outputStream;
    }
  }
}
