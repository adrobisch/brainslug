package brainslug.flow.renderer;

import brainslug.flow.model.AbstractTaskDefinition;
import brainslug.flow.model.ChoiceDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.model.MergeDefinition;
import brainslug.flow.model.marker.EndEvent;
import brainslug.flow.model.marker.StartEvent;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class GraphFactory {

  private final List<ShapeInfo> shapes;
  int defaultNodeSize = 30;

  public GraphFactory() {
    this(getDefaultShapes());
  }

  public GraphFactory(List<ShapeInfo> shapes) {
    this.shapes = shapes;
    loadShapes();
  }

  static List<ShapeInfo> getDefaultShapes() {
    List<ShapeInfo> shapes = new ArrayList<ShapeInfo>();
    shapes.add(new ShapeInfo(Shapes.EndEvent, "shapes/BPMN/End-Event.shape"));
    shapes.add(new ShapeInfo(Shapes.StartEvent, "shapes/BPMN/Start-Event.shape"));
    shapes.add(new ShapeInfo(Shapes.GatewayExclusive, "shapes/BPMN/Gateway-Exclusive-XOR-Data-Based.shape"));
    shapes.add(new ShapeInfo(Shapes.GatewayParallel, "shapes/BPMN/Gateway-Parallel-AND.shape"));
    shapes.add(new ShapeInfo(Shapes.IntermediateEvent, "shapes/BPMN/Intermediate-Event.shape"));
    return shapes;
  }

  void setupStyleSheets(mxGraph graph) {
    mxStylesheet stylesheet = graph.getStylesheet();
    stylesheet.getDefaultVertexStyle().put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
    stylesheet.getDefaultVertexStyle().put(mxConstants.STYLE_STROKECOLOR, "#000000");
    stylesheet.getDefaultVertexStyle().put(mxConstants.STYLE_FONTCOLOR, "#000000");

    stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_STROKECOLOR, "#000000");
    stylesheet.getDefaultEdgeStyle().put(mxConstants.STYLE_FONTCOLOR, "#000000");

    addStyles(stylesheet);
  }

  private void addStyles(mxStylesheet stylesheet) {
    stylesheet.putCellStyle(Shapes.Rounded, roundedRectangleStyle());

    for(ShapeInfo shape : shapes) {
      stylesheet.putCellStyle(shape.getName(), shapeStyle(shape.getName()));
    }
  }

  public mxRectangle getNodeSize(FlowNodeDefinition<?> node) {
    if (node instanceof AbstractTaskDefinition) {
      return new mxRectangle(0, 0, 70, 45);
    }
    return new mxRectangle(0, 0, defaultNodeSize, defaultNodeSize);
  }

  private Hashtable<String, Object> roundedRectangleStyle() {
    Hashtable<String, Object> roundedStyle = new Hashtable<String, Object>();
    roundedStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    roundedStyle.put(mxConstants.STYLE_ROUNDED, true);
    roundedStyle.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
    return roundedStyle;
  }

  private Hashtable<String, Object> shapeStyle(String shapeName) {
    Hashtable<String, Object> shapeStyle = new Hashtable<String, Object>();
    shapeStyle.put(mxConstants.STYLE_SHAPE, shapeName);
    shapeStyle.put(mxConstants.STYLE_SPACING_BOTTOM, -mxConstants.DEFAULT_FONTSIZE * 2);
    shapeStyle.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);

    return shapeStyle;
  }

  void loadShapes() {
    for (ShapeInfo shape : shapes) {
      String shapeXml = convertStreamToString(this.getClass().getClassLoader()
          .getResourceAsStream(shape.getLocation()));
      addStencilShape(shape.getName(), shapeXml);
    }
  }

  static String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

  public static void addStencilShape(String name, String shapeXml) {
    // Some editors place a 3 byte BOM at the start of files
    // Ensure the first char is a "<"
    int lessthanIndex = shapeXml.indexOf("<");
    shapeXml = shapeXml.substring(lessthanIndex);
    mxStencilShape newShape = new mxStencilShape(shapeXml);
    mxGraphics2DCanvas.putShape(name, newShape);
  }

  public mxGraph createGraph() {
    mxGraph graph = new mxGraph();
    setupStyleSheets(graph);
    return graph;
  }
}
