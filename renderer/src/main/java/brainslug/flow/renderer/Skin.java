package brainslug.flow.renderer;

import brainslug.flow.model.*;
import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Skin {

  private List<ShapeInfo> shapes = new ArrayList<ShapeInfo>();
  int defaultNodeSize = 30;

  public mxGraph apply(mxGraph graph) {
    setupStyleSheets(graph);
    return graph;
  }

  public String getShape(FlowNodeDefinition<?> node) {
    if (node instanceof AbstractTaskDefinition) {
      return Shapes.Rounded;
    }
    if (node instanceof ChoiceDefinition) {
      return Shapes.GatewayExclusive;
    }
    if (node instanceof MergeDefinition) {
      return Shapes.GatewayExclusive;
    }
    if (node instanceof JoinDefinition) {
      return Shapes.GatewayParallel;
    }
    if (node instanceof ParallelDefinition) {
      return Shapes.GatewayParallel;
    }
    if (node.hasMixin(brainslug.flow.model.marker.EndEvent.class)) {
      return Shapes.EndEvent;
    }
    if (node.hasMixin(brainslug.flow.model.marker.StartEvent.class)) {
      return Shapes.StartEvent;
    }
    if (node.hasMixin(brainslug.flow.model.marker.IntermediateEvent.class)) {
      return Shapes.IntermediateEvent;
    }

    throw new IllegalArgumentException("no style definition available for " + node);
  }

  public mxRectangle getNodeSize(FlowNodeDefinition<?> node) {
    if (node instanceof AbstractTaskDefinition) {
      return new mxRectangle(0, 0, 70, 45);
    }
    return new mxRectangle(0, 0, defaultNodeSize, defaultNodeSize);
  }

  public void loadShapes(List<ShapeInfo> shapes) {
    this.shapes = shapes;

    for (ShapeInfo shape : shapes) {
      String shapeXml = convertStreamToString(this.getClass().getClassLoader()
        .getResourceAsStream(shape.getLocation()));
      addStencilShape(shape.getName(), shapeXml);
    }
  }

  public void addStencilShape(String name, String shapeXml) {
    // Some editors place a 3 byte BOM at the start of files
    // Ensure the first char is a "<"
    int lessthanIndex = shapeXml.indexOf("<");
    shapeXml = shapeXml.substring(lessthanIndex);
    mxStencilShape newShape = new mxStencilShape(shapeXml);
    mxGraphics2DCanvas.putShape(name, newShape);
  }

  private void setupStyleSheets(mxGraph graph) {
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
      stylesheet.putCellStyle(shape.getName(), shape.getStyle());
    }
  }

  protected Hashtable<String, Object> roundedRectangleStyle() {
    Hashtable<String, Object> roundedStyle = new Hashtable<String, Object>();
    roundedStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    roundedStyle.put(mxConstants.STYLE_ROUNDED, true);
    roundedStyle.put(mxConstants.STYLE_WHITE_SPACE, "wrap");
    return roundedStyle;
  }

  protected Hashtable<String, Object> shapeStyle() {
    Hashtable<String, Object> shapeStyle = new Hashtable<String, Object>();
    shapeStyle.put(mxConstants.STYLE_SPACING_BOTTOM, -mxConstants.DEFAULT_FONTSIZE * 2);
    shapeStyle.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_BOTTOM);

    return shapeStyle;
  }

  private String convertStreamToString(java.io.InputStream is) {
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }

}
