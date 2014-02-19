package brainslug.bpmn;

import brainslug.bpmn.task.ServiceTaskDefinition;
import brainslug.bpmn.task.UserTaskDefinition;
import brainslug.flow.model.FlowNodeDefinition;
import brainslug.flow.renderer.DefaultSkin;
import brainslug.flow.renderer.ShapeInfo;

import java.util.List;

public class BpmnTaskMarkerSkin extends DefaultSkin {

  public static final String SERVICE_TASK = "Service-Task";
  public static final String USER_TASK = "User-Task";

  @Override
  protected List<ShapeInfo> getShapes() {
    List<ShapeInfo> defaultShapes = super.getShapes();
    defaultShapes.add(new ShapeInfo(SERVICE_TASK, "shapes/BPMN/Service-Task.shape", roundedRectangleStyle()));
    defaultShapes.add(new ShapeInfo(USER_TASK, "shapes/BPMN/User-Task.shape", roundedRectangleStyle()));
    return defaultShapes;
  }

  @Override
  public String getShape(FlowNodeDefinition<?> node) {
    if (node instanceof ServiceTaskDefinition) {
      return SERVICE_TASK;
    }
    if (node instanceof UserTaskDefinition) {
      return USER_TASK;
    }

    return super.getShape(node);
  }
}
