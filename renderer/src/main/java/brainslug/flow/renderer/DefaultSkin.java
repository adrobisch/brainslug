package brainslug.flow.renderer;

import java.util.ArrayList;
import java.util.List;

public class DefaultSkin extends Skin {

  public DefaultSkin() {
    loadShapes(getShapes());
  }

  protected List<ShapeInfo> getShapes() {
    List<ShapeInfo> shapes = new ArrayList<ShapeInfo>();
    shapes.add(new ShapeInfo(Shapes.EndEvent, "shapes/BPMN/End-Event.shape", shapeStyle()));
    shapes.add(new ShapeInfo(Shapes.StartEvent, "shapes/BPMN/Start-Event.shape", shapeStyle()));
    shapes.add(new ShapeInfo(Shapes.GatewayExclusive, "shapes/BPMN/Gateway-Exclusive-XOR-Data-Based.shape", shapeStyle()));
    shapes.add(new ShapeInfo(Shapes.GatewayParallel, "shapes/BPMN/Gateway-Parallel-AND.shape", shapeStyle()));
    shapes.add(new ShapeInfo(Shapes.IntermediateEvent, "shapes/BPMN/Intermediate-Event.shape", shapeStyle()));
    return shapes;
  }
}
