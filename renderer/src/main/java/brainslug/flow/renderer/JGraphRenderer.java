package brainslug.flow.renderer;

import brainslug.flow.model.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JGraphRenderer implements Renderer {

  public static final int INTER_RANK_CELL_SPACING = 75;

  private final GraphFactory graphFactory;
  double scale = 1.0f;
  int padding = 20;

  public JGraphRenderer(GraphFactory graphFactory) {
    this.graphFactory = graphFactory;
  }

  private mxGraph createGraph(FlowBuilder flowBuilder) {
    mxGraph graph = graphFactory.createGraph();
    graph.getModel().beginUpdate();
    try {
      convertFlowToGraph(flowBuilder, graph);

      mxHierarchicalLayout layout = new mxHierarchicalLayout(graph, SwingConstants.WEST);
      layout.setInterRankCellSpacing(INTER_RANK_CELL_SPACING);
      layout.execute(graph.getDefaultParent());
    } finally {
      graph.getModel().endUpdate();
    }
    return graph;
  }

  private void convertFlowToGraph(FlowBuilder flowBuilder, mxGraph graph) {
    List<FlowEdgeDefinition> edges = new ArrayList<FlowEdgeDefinition>();
    Map<String, Object> vertices = new HashMap<String, Object>();

    for (FlowNodeDefinition<?> node : flowBuilder.getDefinition().getNodes()) {
      mxRectangle size = graphFactory.getNodeSize(node);
      Object vertex = graph.insertVertex(graph.getDefaultParent(), node.getId().toString(),
          node.getDisplayName(), 0, 0, size.getWidth(), size.getHeight());
      vertices.put(node.getId().toString(), vertex);
      collectNodeEdges(edges, node);

      graph.setCellStyle(Shapes.getShape(node),new Object[] {vertex});
    }

    addEdgesToGraph(edges, vertices, graph);
  }

  private void addEdgesToGraph(List<FlowEdgeDefinition> edges, Map<String, Object> vertices, mxGraph graph) {
    for (FlowEdgeDefinition edge : edges) {
      Object source = vertices.get(edge.getSource().getId().toString());
      Object target = vertices.get(edge.getTarget().getId().toString());
      graph.insertEdge(graph.getDefaultParent(), null, getEdgeLabel(edge), source, target);
    }
  }

  private String getEdgeLabel(FlowEdgeDefinition edge) {
    String label = edge.getDisplayName();
    if (edge.getSource() instanceof ChoiceDefinition) {
      for (ThenDefinition then : ((ChoiceDefinition) edge.getSource()).getThenPaths()) {
        if (then.getPathNodes().getFirst().equals(edge.getTarget())) {
          return then.getPredicateDefinition().getActual().toString();
        }
      }
    }
    return label;
  }

  private void collectNodeEdges(List<FlowEdgeDefinition> edges, FlowNodeDefinition<?> node) {
    for (FlowEdgeDefinition edge : node.getOutgoing()) {
      edges.add(edge);
    }
  }

  private mxRectangle getScaledGraphBounds(mxGraph graph, double scale, int padding) {
    mxRectangle bounds = graph.getGraphBounds();
    bounds.setWidth(bounds.getWidth() * scale);
    bounds.setHeight(bounds.getHeight() * scale);
    bounds.grow(padding);
    return bounds;
  }

  @Override
  public void render(FlowBuilder flowBuilder, OutputStream outputStream, Format format) {
    mxGraph graph = createGraph(flowBuilder);
    mxRectangle bounds = getScaledGraphBounds(graph, scale, padding);

    BufferedImage img = mxCellRenderer.createBufferedImage(
        graph, graph.getChildCells(graph.getModel().getRoot()), scale, null,
        false, bounds);
    save(img, format.name().toLowerCase(), outputStream);
  }

  private void save(BufferedImage image, String format, OutputStream outputStream) {
    try {
      ImageIO.write(image, format, outputStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
